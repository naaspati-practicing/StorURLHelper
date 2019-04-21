package sam.bookmark.view.right;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.HostServices;
import javafx.geometry.VPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sam.bookmark.model.Category;
import sam.bookmark.model.IUrl;
import sam.fx.helpers.FxConstants;
import sam.fx.helpers.FxCss;
import sam.fx.helpers.FxGridPane;
import sam.myutils.Checker;
import sam.nopkg.EnsureSingleton;
import sam.reference.WeakAndLazy;

@Singleton
class UrlDetailsView extends GridPane  {
	private static final EnsureSingleton singleton = new EnsureSingleton();
	{ singleton.init(); }
	
	private final Text id = new Text();
	private final Text site_name = new Text();
	private final Text category = new Text();
	private final Text time = new Text();
	private final Hyperlink url = new Hyperlink();
	private final TextArea description = new TextArea();
	
	@Inject
	public UrlDetailsView(HostServices services) {
		setHgap(5);
		setVgap(5);
		
		description.setEditable(false);
		
		int row = 0;
		addRow(row++, new Text("id: "), id);
		addRow(row++, new Text("Site Name: "), site_name);
		addRow(row++, new Text("Category: "), category);
		addRow(row++, new Text("Added Date: "), time);
		addRow(row++, new Text("Site Url: "), url);
		row++;
		addRow(row++, new Text("Description: "));
		add(description, 0, row++, GridPane.REMAINING, GridPane.REMAINING);
		
		GridPane.setRowSpan(url, 2);
		GridPane.setValignment(url, VPos.TOP);
		url.setWrapText(true);
		
		ColumnConstraints c = new ColumnConstraints();
		c.setFillWidth(true);
		c.setHgrow(Priority.ALWAYS);
		FxGridPane.setColumnConstraint(this, 1, c);
		
		RowConstraints r = new RowConstraints();
		r.setFillHeight(true);
		r.setVgrow(Priority.ALWAYS);
		FxGridPane.setRowConstraint(this, row - 1, r);
		
		setPadding(FxConstants.INSETS_10);
		setBackground(FxCss.background(Color.WHITE));
		
		url.setOnAction(e -> {
			url.setVisited(false);
			if(Checker.isNotEmptyTrimmed(url.getText()))
				services.showDocument(url.getText());
		});
	}
	
	private final WeakAndLazy<StringBuilder> wsb = new WeakAndLazy<>(StringBuilder::new);
	
	public void set(IUrl url, Category parent) {
		
		if(url == null) {
			site_name.setText(null);	
			category.setText(null);
			this.url.setText(null);
			id.setText(null);
			description.clear();
			time.setText(null);
		} else {
			site_name.setText(url.getName());	
			this.url.setText(url.getUrl());
			id.setText(String.valueOf(url.getId()));
			description.setText(url.getDescription());
			time.setText(url.getDateAdded());

			StringBuilder sb = wsb.get();
			sb.setLength(0);
			
			category.setText((cat(parent, sb)).toString());
		}
	}

	private StringBuilder cat(TreeItem<String> parent, StringBuilder sb) {
		return (parent.getParent() == null ? sb : cat(parent.getParent(), sb).append(" / ")).append(parent.getValue());
	}
}
