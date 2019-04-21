package sam.bookmark.model;

import static sam.bookmark.model.UrlMeta.STATUS;
import static sam.bookmark.model.UrlMeta.TAGS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

class UrlMedium extends UrlPartial {
    private final int status;
    private final String tags;
    
    public static String[] columns() {
    	String[] array = UrlPartial.columns();
    	int n = array.length;
    	String[] s = Arrays.copyOf(array, n + 2);
    	
    	s[n] = STATUS;
    	s[n + 1] = TAGS;
    	
    	return s;
    }
    
    public UrlMedium(ResultSet rs) throws SQLException {
    	super(rs);
        this.status = rs.getInt(STATUS);
        this.tags = rs.getString(TAGS);
    }
    public UrlMedium(int id, int parent, String name, String url, String description, int status, String date_added, String tags){
    	super(id, parent, name, url, description, date_added);
        this.status = status;
        this.tags = tags;
    }

    public int getStatus(){ return this.status; }
    public String getTags(){ return this.tags; }
}
