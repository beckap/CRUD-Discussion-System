package guiListAllUsers;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import javafx.scene.layout.Background;

/*******
 * <p>
 * Title: ControllerListAllUsers Class. </p>
 * 
 * <p> Description: The Java/FX-based List all users Page. This class provides the
 * controller actions basic on the user's use of the JavaFX GUI widgets defined
 * by the View class.
 *
 * The class has been written assuming that the View or the Model are the only
 * class methods that can invoke these methods. This is why each has been
 * declared at "protected". </p>
 * 
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-01-30 Initial version
 * @version 1.01 2026-01-31 GUI changes
 */
public class ControllerListAllUsers {

	// In-memory database reference
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/*
	 * Default Constructor not used.
	 * 
	 */
	public ControllerListAllUsers() {
	}

	/**********
	 * <p>
	 * Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper
	 * set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewListAllUsers.theRootPane.getChildren().clear();
		
		// Define the view to show the user
		ViewListAllUsers.theRootPane.getChildren().addAll(ViewListAllUsers.label_PageTitle,
				ViewListAllUsers.label_UserDetails, ViewListAllUsers.line_Separator1, ViewListAllUsers.scrollPane_Users,
				ViewListAllUsers.line_Separator2, ViewListAllUsers.button_Return, ViewListAllUsers.button_Logout,
				ViewListAllUsers.button_Quit);

		// Add the list of widgets to the stage and show it
		ViewListAllUsers.label_UserDetails.setText("User: " + ViewListAllUsers.theUser.getUserName());
		// Set the title for the window
		ViewListAllUsers.theStage.setTitle("List All Users Page");
		ViewListAllUsers.theStage.setScene(ViewListAllUsers.theListAllUsersScene);
		ViewListAllUsers.theStage.show();
	}

	/******
	 * <p> 
	 * Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method populates the user list 
	 * and adds it to the ListView. </p>
	 * 
	 */
	protected static void addUserList() {
		// Clear what had been displayed
		ViewListAllUsers.usersList.getItems().clear();

		// Define the new list of users
		List<String> users = new ArrayList<String>();
		if (ViewListAllUsers.theUser.getAdminRole()) {
			users = theDatabase.getUserList(true);
		} else {
			users = theDatabase.getUserList(false);
		}

		for (String userName : users) {

			if (userName.equals("<Select a User>")) {
				continue;
			}

			if (!theDatabase.getUserAccountDetails(userName)) {
				continue;
			}

			// Store the first name, middle name and last name of the user
			String firstName = theDatabase.getFirstName(userName);
			String middleName = theDatabase.getMiddleName(userName);
			String lastName = theDatabase.getLastName(userName);

			// Handle empty names
			if (firstName == null)
				firstName = "";
			if (middleName == null)
				middleName = "";
			if (lastName == null)
				lastName = "";

			// Store the full name of the user
			String fullName = (firstName + " " + middleName + " " + lastName).replaceAll("\\s+", " ").trim();

			if (fullName.isEmpty()) {
				fullName = "No name on file";
			}

			ViewListAllUsers.usersList.getItems().add(fullName);
			ViewListAllUsers.usersList.setBackground(Background.EMPTY);
			ViewListAllUsers.usersList.setSelectionModel(null);
			ViewListAllUsers.usersList.setPrefWidth(650);
		}

	}

	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user home page. </p>
	 * 
	 */
	protected static void performReturn() {
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewListAllUsers.theStage, ViewListAllUsers.theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(ViewListAllUsers.theStage, ViewListAllUsers.theUser);
			break;
		default:
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal
	 * login page where existing users can log in or potential new users with a
	 * invitation code can start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewListAllUsers.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted. </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}
