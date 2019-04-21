package sam.bookmark.model;

public interface IUrl {
	int getId();
    int getParent();
    String getName();
    String getUrl();
    String getDescription();
    String getDateAdded();
}
