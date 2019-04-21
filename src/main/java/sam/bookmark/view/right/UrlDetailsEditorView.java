package sam.bookmark.view.right;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sam.bookmark.Utils;
import sam.bookmark.model.Category;
import sam.bookmark.model.IUrl;
import sam.fx.helpers.FxConstants;
import sam.fx.helpers.FxCss;
import sam.fx.helpers.FxGridPane;
import sam.nopkg.EnsureSingleton;

public class UrlDetailsEditorView extends GridPane  {
	
	private final TextField site_name = new TextField();
	private final Text category = new Text();
	private final TextField url = new TextField();
	private final TextArea description = new TextArea();
	
	@Inject
	public UrlDetailsEditorView() {
		setHgap(5);
		setVgap(5);
		
		int row = 0;
		addRow(row++, new Text("Site Name: "), site_name);
		addRow(row++, new Text("Category: "), category);
		addRow(row++, new Text("Site Url: "), url);
		addRow(row++, new Text("Description: "));
		add(description, 0, row++, GridPane.REMAINING, GridPane.REMAINING);
		
		setPadding(FxConstants.INSETS_10);
		setBackground(FxCss.background(Color.WHITE));
	}
	
	public void set(IUrl url, Category parent) {
		
		if(url == null) {
			site_name.setText(null);	
			category.setText(null);
			this.url.setText(null);
			description.clear();
		} else {
			site_name.setText(url.getName());	
			this.url.setText(url.getUrl());
			description.setText(url.getDescription());
			
			category.setText(Utils.toTreeString(parent));
		}
	}
}
