package sam.bookmark.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.codejargon.feather.Provides;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sam.bookmark.model.Category;
import sam.bookmark.model.IUrl;
import sam.bookmark.model.UrlMeta;
import sam.bookmark.model.UrlPartial;
import sam.bookmark.view.right.RightPane;
import sam.di.FeatherInjector;
import sam.di.Injector;
import sam.fx.alert.FxAlert;
import sam.fx.helpers.FxStageState;
import sam.myutils.MyUtilsPath;
import sam.sql.JDBCHelper;
import sam.sql.sqlite.SQLiteDB;

public class App extends Application {

	private static final Path selfDir = MyUtilsPath.selfDir();
	private static final Path db_path = (Path) Objects.requireNonNull(System.getProperties().get("db_path"));

	private final Path cache_path = selfDir.resolve("cache.dat");

	private int mod;
	private int next_category_id;
	private int next_url_id;
	private Category[] old_data;
	private Category[] new_data;

	private BookmarkTree bookmarks;
	private SplitPane splitPane;
	private StackPane root;
	private Stage stage;

	private FeatherInjector feather;

	private SQLiteDB db;
	private RightPane right;
	private FxStageState stageState;

	@Override
	public void start(Stage stage) throws Exception {
		try {
			this.stage = stage;

			this.feather = new FeatherInjector(this);
			Injector.init(feather);
			this.bookmarks = feather.instance(BookmarkTree.class);
			this.right = feather.instance(RightPane.class);
			
			Text bottom = new Text();
			BorderPane.setMargin(bottom, new Insets(5));
			splitPane = new SplitPane(new BorderPane(bookmarks, null, null, bottom, null), right);
			Platform.runLater(() -> splitPane.setDividerPositions(0.2, 0.8));
			root = new StackPane(splitPane);

			bookmarks.setRoot(load());
			bottom.setText(
					"count: "+Arrays.stream(old_data).filter(d -> d != null).count()+
					"\nmax_category_id: "+next_category_id+
					"\nmax_url_id: "+next_url_id
					);

			stage.setScene(new Scene(root));
			stage.getScene().getStylesheets().add("styles.css");
			stageState = new FxStageState(selfDir.resolve("stage.state"));
			stageState.apply(stage);
			stage.show();
		} catch (Throwable e) {
			error(e);
		}
	}

	private void error(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String s = sw.toString();
		
		if(stage.getScene() == null)
			stage.setScene(new Scene(new TextArea(s)));
		else
			stage.getScene().setRoot(new TextArea(s));

		stage.show();
		stage.sizeToScene();

		System.out.println(s);
	}
	
	private static final StringBuilder select_urls = JDBCHelper.selectSQL(UrlMeta.URL_TABLE_NAME, UrlPartial.columns()).append(" WHERE parent = ");
	private static final int select_urls_n = select_urls.length(); 
	
	private class Cat extends Category {
		public Cat(int id, String name) {
			super(id, name);
		}
		
		@Override
		public List<IUrl> getUrls() {
			select_urls.setLength(select_urls_n);
			select_urls.append(id).append(';');
			
			try {
				return db().collectToList(select_urls.toString(), UrlPartial::new);
			} catch (SQLException e) {
				FxAlert.showErrorDialog("", "failed to get urls for : "+this, e);
				return Collections.emptyList();
			}
		}
		
		@Override
		public String toString() {
			return id+"@[\""+getValue()+"\"]";
		}
	}
	
	private TreeItem<String> load() throws SQLException {
		Loader d = loader();

		Category c = d.load();

		this.mod = d.mod;
		this.next_category_id = d.next_category_id;
		this.next_url_id = d.next_url_id;
		this.old_data = d.old_data;

		return c;
	}
	private Loader loader() {
		return new Loader() {
			@Override
			protected Category newCategory(int id, String name) {
				return new Cat(id, name);
			}
			
			@Override
			protected Path db_path() {
				return db_path;
			}
			@Override
			protected SQLiteDB db() {
				return App.this.db();
			}
			@Override
			protected Path cache_path() {
				return cache_path;
			}
		};
	}
	
	@Provides
	public SQLiteDB db() {
		if(db == null) {
			try {
				db = new SQLiteDB(db_path);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} 
		return db;
	}
	@Override
	public void stop() throws Exception {
		if(mod != 0) {
			Loader d = loader();

			d.mod = this.mod;
			d.next_category_id = this.next_category_id;
			d.next_url_id = this.next_url_id;
			d.old_data = this.old_data;
			d.root = (Category) bookmarks.getRoot();

			d.save();
		}

		try {
			if(db != null)
				db.close();
		} finally {
			db = null;
		}
		
		try {
			if(stageState.save(stage))
				System.out.println("saved: "+stageState.path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Provides
	public HostServices hostServices() {
		return getHostServices();
	}
}
