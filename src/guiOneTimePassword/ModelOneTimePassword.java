package guiOneTimePassword;

import java.util.ArrayList;
import java.util.List;
import database.Database;

/**
 * Model for the One-Time Password GUI. Provides database access helpers.
 */
public class ModelOneTimePassword {

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	public static List<String> getUserList() {
		List<String> user = new ArrayList<>();
		
		if (ViewOneTimePassword.theUser.getNewStudentRole()) {
			user.add(ViewOneTimePassword.theUser.getUserName());
			return user;
		}
		
		if (ViewOneTimePassword.theUser.getNewStaffRole()) {
			return theDatabase.getUserList(false);
		}
		return theDatabase.getUserList(true);
	}

	public static void createOneTimePassword(String username, String tempPassword) {
		theDatabase.createOneTimePassword(username, tempPassword);
	}
}
