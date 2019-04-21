package sam.bookmark.model;

import java.util.List;

import javafx.scene.control.TreeItem;

public abstract class Category extends TreeItem<String> {
	public final int id;

	public Category(int id, String name) {
		this.id = id;
		super.setValue(name);
	}

	public abstract List<IUrl> getUrls();
}
