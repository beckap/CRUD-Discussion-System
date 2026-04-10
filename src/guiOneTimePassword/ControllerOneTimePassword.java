package guiOneTimePassword;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import entityClasses.PasswordValidation;

/*******
 * <p>
 * Title: ControllerOneTimePassword Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Program that allows to create a one-time use
 * password. This class provides the controller the ability to alter the
 * password without seeing the main key
 * </p>
 * 
 * 
 * @author Diogo Oliveira Moscato
 * 
 * @version 1.00 2026-01-31 Initial version
 * @version 2.00 2026-02-09 Shows specific list combo box based on role 
 * 							by Becka Perez Guerrero
 */
public class ControllerOneTimePassword {
	
	/*
	 * Class Attributes
	 * 
	 */
	protected static Alert a = new Alert(AlertType.INFORMATION);
	
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
		ViewOneTimePassword.theRoot.getChildren().clear();
		
		// Define the view to show the user
		ViewOneTimePassword.theRoot.getChildren().addAll(ViewOneTimePassword.label_PageTitle, ViewOneTimePassword.label_UserDetails, 
				ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1, 
				ViewOneTimePassword.labelSelect, ViewOneTimePassword.comboUsers, ViewOneTimePassword.labelTemp, 
				ViewOneTimePassword.textTempPassword, ViewOneTimePassword.button_Generate,
				ViewOneTimePassword.line_Separator2, ViewOneTimePassword.button_Return, 
				ViewOneTimePassword.button_Logout, ViewOneTimePassword.button_Quit);
		

		// Add the list of widgets to the stage and show it
		ViewOneTimePassword.label_UserDetails.setText("User: " + ViewOneTimePassword.theUser.getUserName());
		
		ViewOneTimePassword.users = ModelOneTimePassword.getUserList();
		if (ViewOneTimePassword.users != null)
			ViewOneTimePassword.comboUsers.setItems(FXCollections.observableArrayList(ViewOneTimePassword.users));
		
		// Set the title for the window
		ViewOneTimePassword.theStage.setTitle("One Time Password");
		ViewOneTimePassword.theStage.setScene(ViewOneTimePassword.theScene);
		ViewOneTimePassword.theStage.show();
	}
	
	public ControllerOneTimePassword() {
	}

	protected static void generateOTP(String username, String tempPassword) {
		a.setHeaderText(null);
		if (username == null || username.length() == 0 || username.compareTo("<Select a User>") == 0) {
			a.setContentText("Select a valid user");
			a.showAndWait();
			return;
		}
		if (tempPassword == null || tempPassword.length() == 0) {
			a.setContentText("Enter a temporary password");
			a.showAndWait();
			return;
		}
		
		// Validate the temporary password
		String errorMessage = PasswordValidation.checkForValidPassword(tempPassword);
		if (!errorMessage.isEmpty()) {
			a.setTitle("Password Error.");
			a.setHeaderText(null);
			a.setContentText(errorMessage);
			a.showAndWait();
			return;
		}
		
		ModelOneTimePassword.createOneTimePassword(username, tempPassword);
		a.setContentText("One-time password set for user: " + username);
		a.showAndWait();
	}

	/**********
	 * Logout: return to the normal login page using the existing stage
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewOneTimePassword.theStage);
	}

	/**********
	 * Quit: terminate the application
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
