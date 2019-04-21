package sam.bookmark.view.right;

import javax.inject.Inject;
import javax.inject.Singleton;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import sam.di.Injector;
import sam.nopkg.EnsureSingleton;

@Singleton
public class RightPane extends SplitPane {
	private static final EnsureSingleton singleton = new EnsureSingleton();
	{ singleton.init(); }
	
	private final UrlsView top;
	private final UrlDetailsView bottom;

	@Inject
	public RightPane(Injector injector) {
		bottom = injector.instance(UrlDetailsView.class);
		top = injector.instance(UrlsView.class);
		
		setOrientation(Orientation.VERTICAL);
		ScrollPane sp = new ScrollPane(bottom);
		sp.setFitToWidth(true);
		getItems().addAll(top, sp);
		Platform.runLater(() -> setDividerPositions(0.5,0.5));
	} 

}
