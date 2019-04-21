import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import sam.bookmark.app.App;
import sam.fx.helpers.ErrorApp;
import sam.myutils.Checker;
import sam.myutils.System2;

public class Main {
	public static void main(String[] args) {
		String s = System2.lookup("db_path");
		
		if(Checker.isEmptyTrimmed(s)) {
			error("db_path not specified");
		} else {
			Path db_path = Paths.get(s.trim());
			
			if(Files.notExists(db_path))
				error("db not found: \n".concat(s));
			else {
				System.getProperties().put("db_path", db_path);
				Application.launch(App.class, args);
			}
		}
	}

	private static void error(String string) {
		ErrorApp.title =  string;
		Application.launch(ErrorApp.class, new String[]{});
	}

}
