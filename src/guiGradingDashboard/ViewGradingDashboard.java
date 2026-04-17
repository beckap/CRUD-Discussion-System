package guiGradingDashboard;

import java.util.ArrayList;
import java.util.List;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/*******
 * <p>
 * Title: ViewGradingDashboard Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based page for the discussion system of the application.
 * </p>
 * 
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-04-16 Initial version
 * 
 */
public class ViewGradingDashboard {
	// ATTRIBUTES

	/**
	 * This is application value for width required by the user interface
	 */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	
	/**
	 * This is application value for height required by the user interface
	 */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Label labelPageTitle = new Label();
	protected static Label labelSelect = new Label();
	
	/**
	 * List of students for the comboBox
	 */
	protected static List<String> students = new ArrayList<String>();
	
	/**
	 * ComboBox containing the list of students for the staff to choose from
	 */
	protected static ComboBox<String> comboStudents = new ComboBox<String>();
	protected static ProgressIndicator studentProgress = new ProgressIndicator(0);
	
	/***
	 * This is a separator and it is used to partition the GUI for various tasks
	 */
	protected static Line line_Separator2 = new Line(20, 95, width - 20, 95);
	
	/**
	 * This button is used to return to the previous page, usually the Staff home page
	 */
	protected static Button button_Return = new Button("Return");
	
	/**
	 * This button is used to logout
	 */
	protected static Button button_Logout = new Button("Logout");
	/***
	 * These attributes are used to configure the page and populate it with this
	 * user's information
	 */
	private static ViewGradingDashboard theView;
	
	protected static Alert error = new Alert(AlertType.INFORMATION);

	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current user of the application

	public static Scene theGradingDashboardScene;

	/****
	Constructors
	*/

	/**********
	 * <p>
	 * Method: displayGradingDashboard(Stage ps, User user)
	 * </p>
	 * 
	 * <p>
	 * Description: This method is the single entry point from outside this package
	 * to cause the DiscussionSystem page to be displayed.
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
	public static void displayGradingDashboard(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null)
			theView = new ViewGradingDashboard();
		ControllerGradingDashboard.repaintTheWindow();
	}

	
	/**********
	 * <p> Method: ViewGradingDashboard()
	 * </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayGradingDashboard method.
	 * </p>
	 * 
	 */
	@SuppressWarnings("null")
	public ViewGradingDashboard() {
		theRootPane = new Pane();
		theGradingDashboardScene = new Scene(theRootPane, width, height);
		labelPageTitle.setText("Student Participation");
		setupLabelUI(labelPageTitle, width, Pos.CENTER, 0, 5);
		labelSelect.setText("Select a student:");
		setupLabelUI(labelSelect, 190, Pos.CENTER, 50, 80);
		
		students = ModelGradingDashboard.getUserList();
		if (students != null) {
			comboStudents.setItems(FXCollections.observableArrayList(students));
		}
		comboStudents.setLayoutX(80);
		comboStudents.setLayoutY(100);
		comboStudents.getSelectionModel().selectFirst();
		
		comboStudents.setOnAction((_) -> {
			String studentUsername = comboStudents.getSelectionModel().getSelectedItem();
			
			// Check if the selected username is null first
			if (studentUsername == null) {
				return;
			}
			
			// Check if the selected username is empty
			if (studentUsername.isEmpty()) {
				return;
			} 
		
			// Check if the selected option is not the default option, then update
			// progress bar
			if (!studentUsername.equals("<Select a User>")) {
				ControllerGradingDashboard.addProgress(studentUsername);
			}
		});
		
		studentProgress.setLayoutX(80);
		studentProgress.setLayoutY(170);
		studentProgress.setMinSize(220, 220);
		
		setupButtonUI(button_Return, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {
			ControllerGradingDashboard.performReturn();
		});
		
		setupButtonUI(button_Logout, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {
			ControllerGradingDashboard.performLogout();
		});
		
		String css = getClass().getResource("/application.css").toExternalForm();
		theGradingDashboardScene.getStylesheets().add(css);
		labelPageTitle.getStyleClass().add("title");
		
		DialogPane error1 = error.getDialogPane();
		error1.getStylesheets().add(css);
		
		theRootPane.getChildren().addAll(labelPageTitle, labelSelect, comboStudents, studentProgress, button_Return,
				button_Logout);
		theStage.setScene(theGradingDashboardScene);
		theStage.show();
		
	}

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
}
