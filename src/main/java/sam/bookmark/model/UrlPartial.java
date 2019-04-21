package sam.bookmark.model;

import static sam.bookmark.model.UrlMeta.DATE_ADDED;
import static sam.bookmark.model.UrlMeta.DESCRIPTION;
import static sam.bookmark.model.UrlMeta.ID;
import static sam.bookmark.model.UrlMeta.NAME;
import static sam.bookmark.model.UrlMeta.PARENT;
import static sam.bookmark.model.UrlMeta.URL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UrlPartial implements IUrl {
	public static String[] columns() {
    	return new String[]{
    			ID,
    			PARENT,
    			NAME,
    			URL,
    			DESCRIPTION,
    			DATE_ADDED
    	};
    }
	
	private final int id;
    private final int parent;
    private final String name;
    private final String url;
    private final String description;
    private final String date_added;
    
    public UrlPartial(ResultSet rs) throws SQLException {
        this.id = rs.getInt(ID);
        this.parent = rs.getInt(PARENT);
        this.name = rs.getString(NAME);
        this.url = rs.getString(URL);
        this.description = rs.getString(DESCRIPTION);
        this.date_added = rs.getString(DATE_ADDED);
    }
    
    public UrlPartial(int id, int parent, String name, String url, String description, String date_added){
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.url = url;
        this.description = description;
        this.date_added = date_added;
    }
    
    public int getId(){ return this.id; }
    public int getParent(){ return this.parent; }
    public String getName(){ return this.name; }
    public String getUrl(){ return this.url; }
    public String getDescription(){ return this.description; }
    public String getDateAdded(){ return this.date_added; }
}
