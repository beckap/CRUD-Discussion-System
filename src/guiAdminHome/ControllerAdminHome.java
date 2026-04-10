package guiAdminHome;

import database.Database;
import entityClasses.EmailValidation;

/*******
 * <p>
 * Title: GUIAdminHomePage Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Admin Home Page. This class provides the
 * controller actions basic on the user's use of the JavaFX GUI widgets defined
 * by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.
 * WHen those buttons are pressed, an alert pops up to tell the user that the
 * function associated with the button has not been implemented. Also, be aware
 * that What has been implemented may not work the way the final product
 * requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only
 * class methods that can invoke these methods. This is why each has been
 * declared at "protected". Do not change any of these methods to public.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-17 Initial version
 * @version 1.01 2025-09-16 Update Javadoc documentation
 * @version 1.02 2026-01-30 Update Admin user stories implementation
 * @version 1.03 2026-02-01 Updated email verification
 * 
 */

public class ControllerAdminHome {

	/*-*******************************************************************************************
	
	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p>
	 * 
	 * Title: performInvitation () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method to send an email inviting a potential user to
	 * establish an account and a specific role.
	 * </p>
	 */
	protected static void performInvitation() {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (!invalidEmailAddress(emailAddress).isEmpty()) {
			return;
		}

		// Check to ensure that we are not sending a message to a user who already
		// exists.
		// (Modified to check userDB for active users)
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError
					.setContentText("This email address is already associated with an existing user account.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}

		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();

		// Check to ensure that we are not sending a duplicate invitation for the same
		// role
		// to the same email if one is already Outstanding.
		if (theDatabase.invitationExists(emailAddress, theSelectedRole)) {
			ViewAdminHome.alertEmailError
					.setContentText("An outstanding invitation for this Role already exists for this email.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}

		// Inform the user that the invitation has been sent and display the invitation
		// code
		String invitationCode = theDatabase.generateInvitationCode(emailAddress, theSelectedRole,
				ViewAdminHome.theUser.getUserName());
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + " was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();

		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations
				.setText("Number of outstanding invitations: " + theDatabase.getNumberOfInvitations());
	}

	/**********
	 * <p>
	 * 
	 * Title: manageInvitations () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that calls the manage invitations GUI.
	 * This allows the user to manage email invitations in the application.
	 * </p>
	 */
	protected static void manageInvitations() {
		guiManageInvitations.ViewManageInvitations.displayManageInvitations(ViewAdminHome.theStage,
				ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: setOnetimePassword () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that lets the user set a one time password.
	 * The user is brought to the GUI being called by this method.
	 * </p>
	 */
	protected static void setOnetimePassword() {
		// Open the One-Time Password GUI
		guiOneTimePassword.ViewOneTimePassword.displayOneTimePassword(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: deleteUser () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that allows an admin to delete a user
	 * currently in the system. This is done by invoking the deleteUser Page. There
	 * is no need to specify the home page for the return as this can only be
	 * initiated by an admin.
	 * 
	 * This method removes a user from the list and deletes it from the database.
	 * </p>
	 */
	protected static void deleteUser() {
		guiDeleteUser.ViewDeleteUser.displayDeleteUser(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: listUsers () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that calls the List All Users
	 * Display and goes to its wndow. This allows an admin to list all users in the
	 * current database.
	 * </p>
	 */
	protected static void listUsers() {
		guiListAllUsers.ViewListAllUsers.displayListAllUsers(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: addRemoveRoles () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that allows an admin to add and remove roles
	 * for any of the users currently in the system. This is done by invoking the
	 * AddRemoveRoles Page. There is no need to specify the home page for the return
	 * as this can only be initiated by and Admin.
	 * </p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: invalidEmailAddress () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is intended to check an email address
	 * before it is used to reduce errors. The code currently only checks to see
	 * that the email address is not empty. In the future, a syntactic check must be
	 * performed and maybe there is a way to check if a properly email address is
	 * active.
	 * </p>
	 * 
	 * @param emailAddress This String holds what is expected to be an email address
	 */
	protected static String invalidEmailAddress(String emailAddress) {
		String errorMessage = EmailValidation.checkForValidEmail(emailAddress);
		if (!errorMessage.isEmpty()) {
			ViewAdminHome.alertEmailError.setTitle("Incorrect Email Address");
			ViewAdminHome.alertEmailError.setHeaderText(null);
			ViewAdminHome.alertEmailError.setContentText(errorMessage);
			ViewAdminHome.alertEmailError.showAndWait();
			return errorMessage;
		}
		return "";
	}

	/**********
	 * <p>
	 * 
	 * Title: performLogout () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that logs this user out of the system and
	 * returns to the login page for future use.
	 * </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}

	/**********
	 * <p>
	 * 
	 * Title: performQuit () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that gracefully terminates the execution of the
	 * program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
