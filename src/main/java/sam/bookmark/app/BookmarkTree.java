package sam.bookmark.app;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sam.bookmark.model.Category;
import sam.bookmark.view.right.UrlsView;
import sam.nopkg.EnsureSingleton;
import sam.sql.sqlite.SQLiteDB;
import sam.thread.DelayedActionThread;

@Singleton
public class BookmarkTree extends TreeView<String> implements ChangeListener<TreeItem<String>> {
	private static final EnsureSingleton singleton = new EnsureSingleton();
	{ singleton.init(); }
	
	private final UrlsView urls;
	private final DelayedActionThread<TreeItem<String>> delay = new DelayedActionThread<>(300, d -> change(d));
	
	@Inject
	public BookmarkTree(UrlsView urls) {
		this.urls = urls;
		setShowRoot(false);
		getSelectionModel().selectedItemProperty().addListener(this);
	}

	private void change(TreeItem<String>  d) {
		Platform.runLater(() -> {
			urls.set((Category) d);
			System.out.println("selected: "+d);
		});
	}

	
	@Override
	public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
			TreeItem<String> newValue) {
		delay.queue(newValue);
	}
}
