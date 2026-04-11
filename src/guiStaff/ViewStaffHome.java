package guiStaff;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import database.Database;
import entityClasses.User;

/*******
 * <p>
 * Title: GUIStaffHomePage Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Staff Home Page. The page is a stub for some
 * role needed for the application. The widgets on this page are likely the
 * minimum number and kind for other role pages that may be needed.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-20 Initial version
 * @version 2.00 2026-02-01 Updated GUI changes based on Role by Becka Perez Guerrero
 * 
 */

public class ViewStaffHome {

	/*-*******************************************************************************************
	
	Attributes
	
	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page, whose account
	// is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// GUI Area 2: Widgets the staff can use.
	// This is the first of two areas provided the staff with a set of action buttons
	// that can be used to perform the tasks allocated to the staff role.  This part is about
	// inviting potential new users to establish an account and what role that user will have.
	protected static Label label_Invitations = new Label("Send An Invitation:");
	protected static Label label_InvitationEmailAddress = new Label("Email Address");
	protected static TextField text_InvitationEmailAddress = new TextField();
	protected static ComboBox <String> combobox_SelectRole = new ComboBox <String>();
	protected static String [] roles = {"Staff", "Student"};
	protected static Button button_SendInvitation = new Button("Send Invitation");
	protected static Button button_Discussion = new Button("Discussion");
	protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);
	protected static Alert alertEmailSent = new Alert(AlertType.INFORMATION);
		
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator3 = new Line(20, 200, width-20, 200);
	
	protected static Button button_SetOnetimePassword = new Button("Set a One-Time Password");
	protected static Button button_ListUsers = new Button("List All Users");

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: This is last of the GUI areas. It is used for quitting the
	// application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.

	// These attributes are used to configure the page and populate it with this
	// user's information
	private static ViewStaffHome theView; // Used to determine if instantiation of the class
											// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current logged in User

	private static Scene theViewStaffHomeScene; // The shared Scene each invocation populates
	protected static final int theRole = 2; // Admin: 1; Staff: 2; Student: 3

	/*-*******************************************************************************************
	
	Constructors
	
	 */

	/**********
	 * <p>
	 * Method: displayStaffHome(Stage ps, User user)
	 * </p>
	 * 
	 * <p>
	 * Description: This method is the single entry point from outside this package
	 * to cause the Staff Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup. If not, it instantiates the
	 * class, initializes all the static aspects of the GIUI widgets (e.g., location
	 * on the page, font, size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change
	 * based on the user and the system's current state. It then sets the Scene onto
	 * the stage, and makes it visible to the user.
	 * 
	 * @param ps   specifies the JavaFX Stage to be used for this GUI and it's
	 *             methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStaffHome(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null)
			theView = new ViewStaffHome(); // Instantiate singleton if needed

		// Populate the dynamic aspects of the GUI with the data from the user and the
		// current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());
		
		// Set the role for potential users to the default (No role selected)
		combobox_SelectRole.getSelectionModel().select(0);

		// Set the title for the window, display the page, and wait for the Admin to do
		// something
		theStage.setTitle("Staff Home Page");
		theStage.setScene(theViewStaffHomeScene);
		theStage.show();
	}

	/**********
	 * <p>
	 * Method: ViewStaffHome()
	 * </p>
	 * 
	 * <p>
	 * Description: This method initializes all the elements of the graphical user
	 * interface. This method determines the location, size, font, color, and change
	 * and event handlers for each GUI object.
	 * </p>
	 * 
	 * This is a singleton and is only performed once. Subsequent uses fill in the
	 * changeable fields using the displayStudentHome method.
	 * </p>
	 * 
	 */
	private ViewStaffHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewStaffHomeScene = new Scene(theRootPane, width, height); // Create the scene

		// Set the title for the window

		// Populate the window with the title and other common widgets and set their
		// static state

		// GUI Area 1
		label_PageTitle.setText("Staff Home");
		setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {
			ControllerStaffHome.performUpdate();
		});

		// GUI Area 2
		setupLabelUI(label_Invitations, width, Pos.BASELINE_LEFT, 20, 110);
		
		setupLabelUI(label_InvitationEmailAddress, width, Pos.BASELINE_LEFT,
		20, 150);
	
		setupTextUI(text_InvitationEmailAddress, 360, Pos.BASELINE_LEFT,
		130, 150, true);
	
		setupComboBoxUI(combobox_SelectRole, 90, 500, 150);
	
		List<String> list = new ArrayList<String>();	// Create a new list empty list of the
		for (int i = 0; i < roles.length; i++) {		// roles this code currently supports
			list.add(roles[i]);
		}
		combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
		combobox_SelectRole.getSelectionModel().select(0);
		alertEmailSent.setTitle("Invitation");
		alertEmailSent.setHeaderText("Invitation was sent");

		setupButtonUI(button_SendInvitation, 150, Pos.CENTER, 630, 150);
		button_SendInvitation.setOnAction((_) -> {ControllerStaffHome.performInvitation(); });
		
		setupButtonUI(button_SetOnetimePassword, 250, Pos.CENTER, 300, 270);
		button_SetOnetimePassword.setOnAction((_) -> {
			ControllerStaffHome.setOnetimePassword();
		});

		setupButtonUI(button_ListUsers, 250, Pos.CENTER, 300, 330);
		button_ListUsers.setOnAction((_) -> {
			ControllerStaffHome.listUsers();
		});
		
		setupButtonUI(button_Discussion, 250, Pos.CENTER, 300, 390);
		button_Discussion.setOnAction((_) -> {
			ControllerStaffHome.performDiscussions();
		});

		// GUI Area 3
		setupButtonUI(button_Logout, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> {
			ControllerStaffHome.performLogout();
		});

		setupButtonUI(button_Quit, 250, Pos.CENTER, 500, 540);
		button_Quit.setOnAction((_) -> {
			ControllerStaffHome.performQuit();
		});

		// This is the end of the GUI initialization code
		
		// CSS Styling for the page
		String css = getClass().getResource("/application.css").toExternalForm();
		theViewStaffHomeScene.getStylesheets().add(css);
		
		label_PageTitle.getStyleClass().add("title");
		text_InvitationEmailAddress.getStyleClass().add("text-fields");
		
		DialogPane alert1 = alertEmailError.getDialogPane();
		alert1.getStylesheets().add(css);
		
		DialogPane alert2 = alertEmailSent.getDialogPane();
		alert2.getStylesheets().add(css);

		// Place all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_Invitations, 
	    		label_InvitationEmailAddress, text_InvitationEmailAddress,
	    		combobox_SelectRole, button_SendInvitation,
				line_Separator3, line_Separator4, button_SetOnetimePassword, button_Discussion, button_ListUsers, 
				button_Logout, button_Quit);
	}

	/*-********************************************************************************************
	
	Helper methods to reduce code length
	
	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l  The Label object to be initialized
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, double w, Pos p, double x, double y) {
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
	private static void setupButtonUI(Button b, double w, Pos p, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param b		The TextField object to be initialized
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		Is this TextField user editable?
	 */
	private void setupTextUI(TextField t, double w, Pos p, double x, double y, boolean e){
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	

	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, double w, double x, double y){
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
