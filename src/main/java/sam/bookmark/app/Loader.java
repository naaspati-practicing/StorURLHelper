package sam.bookmark.app;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.scene.control.TreeItem;
import sam.bookmark.model.Category;
import sam.io.IOUtils;
import sam.io.serilizers.DataReader;
import sam.io.serilizers.DataWriter;
import sam.sql.sqlite.SQLiteDB;

abstract class Loader {
	int mod;
	int next_category_id;
	int next_url_id;
	Category[] old_data;
	Category root;

	protected abstract Path cache_path();
	protected abstract Path db_path();
	protected abstract SQLiteDB db();
	protected abstract Category newCategory(int id, String name);

	private static final int END_MARKER = -100;

	public Category load() throws SQLException {
		if(Files.notExists(cache_path()))
			return full_load();
		else {
			try(FileChannel fc = FileChannel.open(cache_path(), READ);
					) {

				ByteBuffer buf = ByteBuffer.allocate(16);

				if(IOUtils.read(buf, true, fc) < 16)
					return full_load();

				long last_mod = buf.getLong();
				if(last_mod != db_path().toFile().lastModified())
					return full_load();

				next_category_id = buf.getInt();
				next_url_id = buf.getInt();
				old_data = new Category[next_category_id];
				LinkedList<Temp> missings = new LinkedList<>();

				try(DataReader reader = new DataReader(fc, ByteBuffer.allocate(Math.min((int)fc.size() - 16, 8124)));) {
					while(true) {
						int id = reader.readInt();
						if(id == END_MARKER)
							break;

						add(id, reader.readInt(), reader.readUTF(), missings);
					}
				}

				handleMissings(missings);

				System.out.println("read cache: "+cache_path());
				return old_data[1];
			} catch (Throwable e) {
				e.printStackTrace();
				return full_load();
			}
		}
	}

	private class Temp {
		final int parent;
		final Category c;

		public Temp(int parent, Category c) {
			this.parent = parent;
			this.c = c;
		}
	}

	protected void add(int id, int parent, String name, List<Temp> missings) {
		Category c = old_data[id] = newCategory(id, name);
		
		if(parent < 0)
			return;
		Category p = old_data[parent];

		if(p == null)
			missings.add(new Temp(parent, c));
		else 
			p.getChildren().add(c);

	}
	public Category full_load() throws SQLException {
		mod = 10;

		SQLiteDB db = db();

		db.iterate("select * from sqlite_sequence", rs -> {
			switch (rs.getString("name")) {
				case "categories":
					this.next_category_id = rs.getInt("seq") + 1;
					break;
				case "urls":
					this.next_url_id = rs.getInt("seq") + 1;
					break;
				default:
					break;
			}
		});

		old_data = new Category[next_category_id];
		LinkedList<Temp> misssings = new LinkedList<>();
		db.iterate("select id, parent, name from categories order by id", rs -> add(rs.getInt("id"), rs.getInt("parent"), rs.getString("name"), misssings));

		handleMissings(misssings);

		System.out.println("read db");
		return old_data[1];
	}

	private void handleMissings(LinkedList<Temp> misssings) {
		for (Temp t : misssings) {
			if(old_data[t.parent] == null)
				System.out.println("item null at: "+t.parent);
			else
				old_data[t.parent].getChildren().add(t.c);
		}
		
		System.out.println("found categories: "+Arrays.stream(old_data).filter(Objects::nonNull).count());
	}
	
	public void save() throws IOException {
		if(mod == 0)
			return;

		Objects.requireNonNull(root);

		try(FileChannel fc = FileChannel.open(cache_path(), WRITE, TRUNCATE_EXISTING, CREATE);
				DataWriter writer = new DataWriter(fc, ByteBuffer.allocate(8124));
				) {

			writer.writeLong(db_path().toFile().lastModified());
			writer.writeInt(next_category_id);
			writer.writeInt(next_url_id);

			Category c = (Category)root;
			write(-1, c, writer);
			write(c.id, root.getChildren(), writer);
			writer.writeInt(END_MARKER);

			System.out.println("saved cache: "+cache_path());
		}
	}
	private void write(int parent_id, List<TreeItem<String>> children, DataWriter writer) throws IOException {
		if(children.isEmpty())
			return;

		for (TreeItem<String> t : children) {
			if(t != null) {
				Category c = (Category) t;
				write(parent_id, c, writer);

				if(!c.getChildren().isEmpty())
					write(c.id, c.getChildren(), writer);
			}
		}
	}
	private void write(int parent_id, Category c, DataWriter writer) throws IOException {
		writer.writeInt(c.id);
		writer.writeInt(parent_id);
		writer.writeUTF(c.getValue());
	}
}
