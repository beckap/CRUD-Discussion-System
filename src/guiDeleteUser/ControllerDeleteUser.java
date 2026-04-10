package guiDeleteUser;

import database.Database;
import javafx.collections.FXCollections;

public class ControllerDeleteUser {

	/**
	 * Default constructor is not used.
	 */
	public ControllerDeleteUser() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p>
	 * Method: repaintTheWindow()
	 * </p>
	 * 
	 * <p>
	 * Description: This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper
	 * set of current values.
	 * </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDeleteUser.theRootPane.getChildren().clear();

		// Show all the fields as there is a selected user (as opposed to the prompt)
		ViewDeleteUser.theRootPane.getChildren().addAll(ViewDeleteUser.label_PageTitle,
				ViewDeleteUser.label_UserDetails, ViewDeleteUser.button_UpdateThisUser, ViewDeleteUser.line_Separator1,
				ViewDeleteUser.label_SelectUserToBeDeleted, ViewDeleteUser.combobox_SelectUserToDelete,
				ViewDeleteUser.button_DeleteUser, ViewDeleteUser.line_Separator4, ViewDeleteUser.button_Return,
				ViewDeleteUser.button_Logout, ViewDeleteUser.button_Quit);

		// Add the list of widgets to the stage and show it

		// Set the title for the window
		ViewDeleteUser.theStage.setTitle("Delete User Page");
		ViewDeleteUser.theStage.setScene(ViewDeleteUser.theDeleteUserScene);
		ViewDeleteUser.theStage.show();
	}

	/**********
	 * <p>
	 * Method: performDeleteUser()
	 * </p>
	 * 
	 * <p>
	 * Description: This method removes an existing role to the list of role in the
	 * ComboBox select list.
	 * </p>
	 * 
	 */
	protected static void performDeleteUser() {

		// Determine which item in the ComboBox list was selected
		ViewDeleteUser.theSelectedUser = (String) ViewDeleteUser.combobox_SelectUserToDelete.getValue();

		// If the selection is the list header (e.g., "<Select a user>") don't do
		// anything
		if (ViewDeleteUser.theSelectedUser.compareTo("<Select a user>") != 0) {

			// If an actual user was deleted, update the GUI and database
			if (theDatabase.deleteUser(ViewDeleteUser.theSelectedUser)) {

				// pop-up message to alert admin that the delete was successful
				String msg = "User: " + ViewDeleteUser.theSelectedUser + " was successfully deleted.";
				System.out.println(msg);
				ViewDeleteUser.alertUserDeleted.setContentText(msg);
				ViewDeleteUser.alertUserDeleted.showAndWait();

				// update the GUI
				ViewDeleteUser.combobox_SelectUserToDelete
						.setItems(FXCollections.observableArrayList(theDatabase.getUserList(true)));
				ViewDeleteUser.combobox_SelectUserToDelete.getSelectionModel().clearAndSelect(0);
			}
		}
	}

	/**********
	 * <p>
	 * Method: performReturn()
	 * </p>
	 * 
	 * <p>
	 * Description: This method returns the user (who must be an Admin as only
	 * admins are the only users who have access to this page) to the Admin Home
	 * page.
	 * </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage, ViewDeleteUser.theUser);
	}

	/**********
	 * <p>
	 * Method: performLogout()
	 * </p>
	 * 
	 * <p>
	 * Description: This method logs out the current user and proceeds to the normal
	 * login page where existing users can log in or potential new users with a
	 * invitation code can start the process of setting up an account.
	 * </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
	}

	/**********
	 * <p>
	 * Method: performQuit()
	 * </p>
	 * 
	 * <p>
	 * Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted.
	 * </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}
