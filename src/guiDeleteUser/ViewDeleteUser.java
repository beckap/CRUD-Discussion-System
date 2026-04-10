package guiDeleteUser;

import java.util.List;

import database.Database;
import entityClasses.User;
import guiDeleteUser.ViewDeleteUser;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/*******
 * <p>
 * Title: ViewDeleteUser Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Delete User Page.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-20 Initial version
 * @version 2.00 2026-02-01 Updated GUI changes based on Role by Genesee Harmon
 * 
 */

public class ViewDeleteUser {

	/*-*******************************************************************************************
	
	Attributes
	
	*/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page, whose account
	// is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// Area 2: This allows the admin to select a user of the system and delete them.
	protected static Button button_DeleteUser = new Button("Delete This User");
	protected static Label label_SelectUserToBeDeleted = new Label("Select a user to be deleted:");
	protected static ComboBox<String> combobox_SelectUserToDelete = new ComboBox<String>();
	protected static Alert alertUserDeleted = new Alert(AlertType.INFORMATION); // to alert successful user deletion

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: This is last of the GUI areas. It is used for quitting the
	// application, logging
	// out, and on other pages a return is provided so the user can return to a
	// previous page when
	// the actions on that page are complete. Be advised that in most cases in this
	// code, the
	// return is to a fixed page as opposed to the actual page that invoked the
	// pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.

	// These attributes are used to configure the page and populate it with this
	// user's information
	private static ViewDeleteUser theView; // Used to determine if instantiation of the class
											// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current user of the application

	public static Scene theDeleteUserScene = null; // The Scene each invocation populates
	protected static String theSelectedUser = ""; // The user who is being deleted

	/*-*******************************************************************************************
	
	Constructors
	
	*/

	/**********
	 * <p>
	 * Method: displayDeleteUser(Stage ps, User user)
	 * </p>
	 * 
	 * <p>
	 * Description: This method is the single entry point from outside this package
	 * to cause the DeleteUser page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup. If not, it instantiates the
	 * class, initializes all the static aspects of the GUI widgets (e.g., location
	 * on the page, font, size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change
	 * based on the user and the system's current state. It then sets the Scene onto
	 * the stage, and makes it visible to the user.
	 * 
	 * @param ps   specifies the JavaFX Stage to be used for this GUI and it's
	 *             methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayDeleteUser(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI by creating
		// the
		// singleton instance of this class
		if (theView == null)
			theView = new ViewDeleteUser();

		// Default to no user selected
		combobox_SelectUserToDelete.getSelectionModel().select(0);

		// Populate the GUI with the data from the user and the current state of the
		// system.
		List<String> userList = theDatabase.getUserList(true);
		combobox_SelectUserToDelete.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUserToDelete.getSelectionModel().select(0);
		ControllerDeleteUser.repaintTheWindow();
	}

	/**********
	 * <p>
	 * Method: GUIDeleteUserPage()
	 * </p>
	 * 
	 * <p>
	 * Description: This method initializes all the elements of the graphical user
	 * interface. This method determines the location, size, font, color, and change
	 * and event handlers for each GUI object.
	 * </p>
	 * 
	 */
	public ViewDeleteUser() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theDeleteUserScene = new Scene(theRootPane, width, height);

		// Populate the window with the title and other common widgets and set their
		// static state

		// GUI Area 1
		label_PageTitle.setText("Delete User");
		setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser);
		});

		// GUI Area 2 -- Delete label, combobox, button, and pop-up confirmation alert
		setupLabelUI(label_SelectUserToBeDeleted, 300, Pos.BASELINE_LEFT, 20, 210);
		setupComboBoxUI(combobox_SelectUserToDelete, 250, 280, 205);
		List<String> userList = theDatabase.getUserList(true);
		combobox_SelectUserToDelete.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUserToDelete.getSelectionModel().select(0);
		setupButtonUI(button_DeleteUser, 150, Pos.CENTER, 560, 205);
		button_DeleteUser.setOnAction((_) -> {
			ControllerDeleteUser.performDeleteUser();
		});
		alertUserDeleted.setTitle("Delete User");
		alertUserDeleted.setHeaderText("User deleted");

		// GUI Area 3
		setupButtonUI(button_Return, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {
			ControllerDeleteUser.performReturn();
		});

		setupButtonUI(button_Logout, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {
			ControllerDeleteUser.performLogout();
		});

		setupButtonUI(button_Quit, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {
			ControllerDeleteUser.performQuit();
		});

		// This is the end of the GUI Widgets for the page
		
		//CSS Styling for the page
		String css = getClass().getResource("/application.css").toExternalForm();
		theDeleteUserScene.getStylesheets().add(css);
		
		label_PageTitle.getStyleClass().add("title");
		
		DialogPane alert = alertUserDeleted.getDialogPane();
		alert.getStylesheets().add(css);
		
		theRootPane.getChildren().addAll(label_PageTitle, label_UserDetails, label_SelectUserToBeDeleted, combobox_SelectUserToDelete,
				button_DeleteUser, button_Return, button_Logout, button_Quit);
		theStage.setScene(theDeleteUserScene);
		theStage.show();
	}

	/*-*******************************************************************************************
	
	Helper methods used to minimizes the number of lines of code needed above
	
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
	protected static void setupButtonUI(Button b, double w, Pos p, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c  The ComboBox object to be initialized
	 * @param w  The width of the ComboBox
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox<String> c, double w, double x, double y) {
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}

}
