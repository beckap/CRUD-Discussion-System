package guiListAllUsers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p>
 * Title: ViewListAllUsers Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based page for listing all users in the database.
 * </p>
 * 
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-01-30 Initial version
 * 
 */

public class ViewListAllUsers {
	/********************************************************************************************
	
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

	// This tells the user that the users will be listed
	protected static Label label_UsersList = new Label("Users:");

	// Scroll Pane to scroll through the list
	protected static ScrollPane scrollPane_Users = new ScrollPane();

	// VBox to list all users in a vertical layout
	// protected static VBox vBox_UsersList = new VBox(10);

	protected static ListView<String> usersList = new ListView<>();

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator2 = new Line(20, 95, width - 20, 95);

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

	// These attributes are used to configure the page and populate it with this
	// user's information
	private static ViewListAllUsers theView; // Used to determine if instantiation of the class
												// is needed

	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current user of the application

	public static Scene theListAllUsersScene;

	/*-*******************************************************************************************
	
	Constructors
	
	*/

	/**********
	 * <p>
	 * Method: displayAddRemoveRoles(Stage ps, User user)
	 * </p>
	 * 
	 * <p>
	 * Description: This method is the single entry point from outside this package
	 * to cause the AddRevove page to be displayed.
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
	public static void displayListAllUsers(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null)
			theView = new ViewListAllUsers();

		ControllerListAllUsers.repaintTheWindow();
		ControllerListAllUsers.addUserList();
	}

	
	/**********
	 * <p> Method: GUIListAllUsers() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayListAllUsers method.</p>
	 * 
	 */
	public ViewListAllUsers() {
		theRootPane = new Pane();
		theListAllUsersScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		// Set up page title
		label_PageTitle.setText("View All Users");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		// Set up current user details
		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(55);
		label_UserDetails.setMinWidth(width);
		label_UserDetails.setAlignment(Pos.BASELINE_LEFT);

		// Set up GUI Area 2
		scrollPane_Users.setLayoutX(30);
		scrollPane_Users.setLayoutY(120);
		scrollPane_Users.setMinHeight(400);
		scrollPane_Users.setMaxWidth(width);
		scrollPane_Users.setFitToWidth(true);
		scrollPane_Users.setContent(usersList);
		scrollPane_Users.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {
			ControllerListAllUsers.performReturn();
		});

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {
			ControllerListAllUsers.performLogout();
		});

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {
			ControllerListAllUsers.performQuit();
		});
		
		String css = getClass().getResource("/application.css").toExternalForm();
		theListAllUsersScene.getStylesheets().add(css);
		
	}

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l  The Label object to be initialized
	 * @param ff The font to be used
	 * @param f  The size of the font to be used
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b  The Button object to be initialized
	 * @param ff The font to be used
	 * @param f  The size of the font to be used
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}
