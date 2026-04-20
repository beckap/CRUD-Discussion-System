package guiStaff;

import database.Database;
import entityClasses.EmailValidation;

/*******
 * <p>
 * Title: ControllerStaffHome Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Staff Home Page. This class provides the
 * controller actions basic on the user's use of the JavaFX GUI widgets defined
 * by the View class.
 * 
 * 
 * The class has been written assuming that the View or the Model are the only
 * class methods that can invoke these methods. This is why each has been
 * declared at "protected". Do not change any of these methods to public.
 * </p>
 * 
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-17 Initial version
 * @version 1.01 2025-09-16 Update Javadoc documentation 
 * @version 2.00 2026-02-01 Updated class for Staff role by Becka Perez Guerrero, Diogo Moscato
 */

public class ControllerStaffHome {

	/*-*******************************************************************************************
	
	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor is not used.
	 */
	public ControllerStaffHome() {
	}

	/**********
	 * <p>
	 * 
	 * Title: setOnetimePassword () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is currently a stub informing the user
	 * that this function has not yet been implemented.
	 * </p>
	 */
	protected static void setOnetimePassword() {
		// Open the One-Time Password GUI
		guiOneTimePassword.ViewOneTimePassword.displayOneTimePassword(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: listUsers () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that allows a Staff to list all users in the
	 * current database.
	 * </p>
	 */
	protected static void listUsers() {
		guiListAllUsers.ViewListAllUsers.displayListAllUsers(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p>
	 * Method: performUpdate()
	 * </p>
	 * 
	 * <p>
	 * Description: This method directs the user to the User Update Page so the user
	 * can change the user account attributes.
	 * </p>
	 * 
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}
	
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
		String emailAddress = ViewStaffHome.text_InvitationEmailAddress.getText();
		if (!invalidEmailAddress(emailAddress).isEmpty()) {
			return;
		}

		// Check to ensure that we are not sending a message to a user who already
		// exists.
		// (Modified to check userDB for active users)
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewStaffHome.alertEmailError
					.setContentText("This email address is already associated with an existing user account.");
			ViewStaffHome.alertEmailError.showAndWait();
			return;
		}

		String theSelectedRole = (String) ViewStaffHome.combobox_SelectRole.getValue();

		// Check to ensure that we are not sending a duplicate invitation for the same
		// role
		// to the same email if one is already Outstanding.
		if (theDatabase.invitationExists(emailAddress, theSelectedRole)) {
			ViewStaffHome.alertEmailError
					.setContentText("An outstanding invitation for this Role already exists for this email.");
			ViewStaffHome.alertEmailError.showAndWait();
			return;
		}

		// Inform the user that the invitation has been sent and display the invitation
		// code
		String invitationCode = theDatabase.generateInvitationCode(emailAddress, theSelectedRole,
				ViewStaffHome.theUser.getUserName());
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + " was sent to: " + emailAddress;
		System.out.println(msg);
		ViewStaffHome.alertEmailSent.setContentText(msg);
		ViewStaffHome.alertEmailSent.showAndWait();

		// Update the Staff Home pages status
		ViewStaffHome.text_InvitationEmailAddress.setText("");
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
			ViewStaffHome.alertEmailError.setTitle("Incorrect Email Address");
			ViewStaffHome.alertEmailError.setHeaderText(null);
			ViewStaffHome.alertEmailError.setContentText(errorMessage);
			ViewStaffHome.alertEmailError.showAndWait();
			return errorMessage;
		}
		return "";
	}
	
	/**********
	 * <p>
	 * 
	 * Title: performDiscussions() Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that displays the discussion page after
	 * 	button is clicked.
	 * </p>
	 */
	protected static void performDiscussions() {
		guiDiscussionSystem.ViewDiscussionSystem.displayDiscussionSystem(ViewStaffHome.theStage, ViewStaffHome.theUser);
		
	}

	/**********
	 * <p>
	 * 
	 * Title: performDiscussions() Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that displays the discussion page after
	 * 	button is clicked.
	 * </p>
	 */
	protected static void performGradingDashboard() {
		guiGradingDashboard.ViewGradingDashboard.displayGradingDashboard(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}

	/**********
	 * <p>
	 * Title: performReportedPosts() Method.
	 * </p>
	 *
	 * <p>
	 * Description: Displays the reported posts moderation page.
	 * </p>
	 */
	protected static void performReportedPosts() {
		guiReportPosts.ViewReportPosts.displayReportPosts(ViewStaffHome.theStage, ViewStaffHome.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
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
