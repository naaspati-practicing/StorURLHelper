package sam.bookmark.view.right;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import sam.bookmark.model.Category;
import sam.bookmark.model.IUrl;
import sam.myutils.Checker;
import sam.nopkg.EnsureSingleton;

@Singleton
public class UrlsView extends ListView<IUrl> implements ChangeListener<IUrl> {
	private static final EnsureSingleton singleton = new EnsureSingleton();
	{ singleton.init(); }

	private final UrlDetailsView details;
	private Category category;

	@Inject
	public UrlsView(UrlDetailsView details) {
		this.details = details;

		setPlaceholder(new Text("EMPTY"));
		setCellFactory(c -> new Lc());
		getSelectionModel().selectedItemProperty()
		.addListener(this);
	}

	public void set(Category c) {
		this.category = c;
		List<IUrl> urls = c == null ? null : c.getUrls();
		getSelectionModel().clearSelection();

		details.set(null, null);

		if(Checker.isEmpty(urls))
			getItems().clear();
		else
			getItems().setAll(urls);		
	}



	private static class Lc extends ListCell<IUrl> {
		@Override
		protected void updateItem(IUrl item, boolean empty) {
			super.updateItem(item, empty);
			setText(empty || item == null ? null : item.getName());
		}
	}
	
	/*
	 * private static class Lc extends ListCell<IUrl> {
		final Text top = new Text();
		final Text bottom = new Text();
		final VBox box = new VBox(2, top, bottom);

		{
			setGraphic(box);
			box.setFillWidth(true);
		}

		@Override
		protected void updateItem(IUrl item, boolean empty) {
			super.updateItem(item, empty);
			if(empty || item == null)
				setGraphic(null);
			else {
				if(getGraphic() == null)
					setGraphic(box);

				top.setText(item.getName());
				bottom.setText(item.getUrl());
			}
		}
	}
	 */

	@Override
	public void changed(ObservableValue<? extends IUrl> observable, IUrl oldValue, IUrl newValue) {
		details.set(newValue, category);
	}
}
