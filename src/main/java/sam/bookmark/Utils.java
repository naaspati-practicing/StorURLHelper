package sam.bookmark;

import javafx.scene.control.TreeItem;
import sam.reference.WeakAndLazy;

public final class Utils {
	
	private static final WeakAndLazy<StringBuilder> wsb = new WeakAndLazy<>(StringBuilder::new);

	public static  String toTreeString(TreeItem<String> parent) {
		StringBuilder sb = wsb.get();
		sb.setLength(0);
		
		return (toTreeString(parent, sb)).toString();
	}
	private static  StringBuilder toTreeString(TreeItem<String> parent, StringBuilder sb) {
		return (parent.getParent() == null ? sb : toTreeString(parent.getParent(), sb).append(" / ")).append(parent.getValue());
	}

}
