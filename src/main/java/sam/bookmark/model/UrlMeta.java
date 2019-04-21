package sam.bookmark.model;



public interface UrlMeta {
    String URL_TABLE_NAME = "urls";

    String ID = "id";    // id 	INTEGER PRIMARY KEY AUTOINCREMENT
    String PARENT = "parent";    // parent 	INTEGER
    String NAME = "name";    // name 	VARCHAR ( 255 )
    String URL = "url";    // url 	TEXT
    String DESCRIPTION = "description";    // description 	TEXT
    String STATUS = "status";    // status 	INTEGER
    String SCANDATE = "scandate";    // scandate 	TEXT
    String STARRED = "starred";    // starred 	INTEGER DEFAULT 0
    String DATE_ADDED = "date_added";    // date_added 	TEXT
    String USERNAME = "username";    // username 	TEXT
    String PASSWORD = "password";    // password 	TEXT
    String FAVICON = "favicon";    // favicon 	BLOB
    String TAGS = "tags";    // tags 	TEXT


}