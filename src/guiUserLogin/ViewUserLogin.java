package guiUserLogin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*******
 * <p>
 * Title: GUIStartupPage Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based System Startup Page.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter Â© 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-04-20 Initial version
 * 
 */

public class ViewUserLogin {

	/*-********************************************************************************************
	
	Attributes
	
	 *********************************************************************************************/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_PageTitle = new Label("Welcome back!");
	private static Label label_PageSubTitle = new Label("We're so excited to see you again!");

	// This set is for all subsequent starts of the system
	// private static Label label_OperationalStartTitle = new Label("Log In or Invited User Account Setup ");
	private static Label label_LogInInstructions = new Label(
			"Enter your user name and password:");
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// private User user;
	protected static TextField text_Username = new TextField();
	protected static PasswordField text_Password = new PasswordField();
	private static Button button_Login = new Button("Log In");

	private static Label label_AccountSetupInsrtuctions = new Label(
			"No account? " + "Enter your invitation code:");
	private static TextField text_Invitation = new TextField();
	private static Button button_SetupAccount = new Button("Setup Account");

	private static Button button_Quit = new Button("Quit");

	private static Stage theStage;
	private static Pane theRootPane;
	public static Scene theUserLoginScene = null;

	private static ViewUserLogin theView = null; // private static guiUserLogin.ControllerUserLogin theController;

	/*-********************************************************************************************
	
	Constructor
	
	 *********************************************************************************************/

	public static void displayUserLogin(Stage ps) {

		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null)
			theView = new ViewUserLogin();

		// Populate the dynamic aspects of the GUI with the data from the user and the
		// current
		// state of the system.
		text_Username.setText(""); // Reset the username and password from the last use
		text_Password.setText("");
		text_Invitation.setText(""); // Same for the invitation code

		// Set the title for the window, display the page, and wait for the Admin to do
		// something
		theStage.setTitle("User Login Page");
		theStage.setScene(theUserLoginScene);
		theStage.show();
		
		// sanity check - this is just to make sure the correct font is being used
		//System.out.println(label_ApplicationTitle.getFont());
		
	}

	/**********
	 * <p>
	 * Method: ViewUserLoginPage()
	 * </p>
	 * 
	 * <p>
	 * Description: This method is called when the application first starts. It must
	 * handle two cases: 1) when no has been established and 2) when one or more
	 * users have been established.
	 * 
	 * If there are no users in the database, this means that the person starting
	 * the system jmust be an administrator, so a special GUI is provided to allow
	 * this Admin to set a username and password.
	 * 
	 * If there is at least one user, then a different display is shown for existing
	 * users to login and for potential new users to provide an invitation code and
	 * if it is valid, they are taken to a page where they can specify a username
	 * and password.
	 * </p>
	 * 
	 * @param ps      specifies the JavaFX Stage to be used for this GUI and it's
	 *                methods
	 * 
	 * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's
	 *                methods
	 * 
	 * @param db      specifies the Database to be used by this GUI and it's methods
	 * 
	 */
	private ViewUserLogin() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserLoginScene = new Scene(theRootPane, width, height);
		

		// Populate the window with the title and other common widgets and set their
		// static state
		setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 10);
		
		setupLabelUI(label_PageSubTitle, width, Pos.CENTER, 0, 50);

		// Existing user log in portion of the page

		setupLabelUI(label_LogInInstructions, width, Pos.CENTER, 0, 100);
		

		// Establish the text input operand field for the username
		setupTextUI(text_Username, 300, Pos.CENTER, 245, 140, true);
		text_Username.setPromptText("Enter Username");

		// Establish the text input operand field for the password
		setupTextUI(text_Password, 300, Pos.CENTER, 245, 190, true);
		text_Password.setPromptText("Enter Password");
		
		// Set up the Log In button
		setupButtonUI(button_Login, 300, Pos.CENTER, 245, 250);
		button_Login.setOnAction((_) -> {
			ControllerUserLogin.doLogin(theStage);
		});

		alertUsernamePasswordError.setTitle("Invalid username/password!");
		alertUsernamePasswordError.setHeaderText(null);

		// The invitation to setup an account portion of the page

		setupLabelUI(label_AccountSetupInsrtuctions, width, Pos.CENTER, 0, 320);

		// Establish the text input operand field for the password
		setupTextUI(text_Invitation, 300, Pos.CENTER, 245, 360, true);
		text_Invitation.setPromptText("Enter Invitation Code");

		// Set up the setup button
		setupButtonUI(button_SetupAccount, 300, Pos.CENTER, 245, 410);
		button_SetupAccount.setOnAction((_) -> {
			System.out.println("**** Calling doSetupAccount");
			ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
		});

		// Set up the Quit button
		setupButtonUI(button_Quit, 100, Pos.CENTER, 350, 510);
		button_Quit.setOnAction((_) -> {
			ControllerUserLogin.performQuit();
		});

		// CSS Styling for the page
		String css = getClass().getResource("/application.css").toExternalForm();
		theUserLoginScene.getStylesheets().add(css);
		// sanity check - this just shows if the correct stylesheet file is loading in 
		System.out.println("Stylesheets now = " + theUserLoginScene.getStylesheets());
		
		text_Username.getStyleClass().add("text-fields");
		text_Password.getStyleClass().add("text-fields");
		text_Invitation.getStyleClass().add("text-fields");
		
		DialogPane alert = alertUsernamePasswordError.getDialogPane();
		alert.getStylesheets().add(css);

		// To create CSS classes in the stylesheet
		label_PageTitle.getStyleClass().add("title");
		label_PageSubTitle.getStyleClass().add("sub-title");
		label_LogInInstructions.getStyleClass().add("user-login-instructions");
		label_AccountSetupInsrtuctions.getStyleClass().add("user-account-setup-instructions");
		

		theRootPane.getChildren().addAll(label_PageTitle, label_PageSubTitle, label_LogInInstructions,
				label_AccountSetupInsrtuctions, text_Username, button_Login, text_Password, text_Invitation,
				button_SetupAccount, button_Quit);
	}

	/*-********************************************************************************************
	
	Helper methods to reduce code length
	
	 *********************************************************************************************/

	/**********
	 * Private local method to initialize the standard fields for a label
	 */

	private void setupLabelUI(Label l, double w, Pos p, double x, double y) {
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	

	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b  The Button object to be initialized
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, double w, Pos p, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, double w, Pos p, double x, double y, boolean e) {
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}
