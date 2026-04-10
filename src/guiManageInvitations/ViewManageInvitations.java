package guiManageInvitations;

import java.util.ArrayList;
import java.util.List;

import entityClasses.Invitation;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * MVC view component of the invitation management page.
 * Displays a table of all invitations ever sent,
 * with options provided to revoke outstanding ones.
 * @author Hannah Henderson
 * @version 1.1 [3 Feb. 2026]
 */
public class ViewManageInvitations {
	
	// Required fields
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// Singleton instance
	private static ViewManageInvitations theView;
	
	// Copied references
	protected static Stage theStage;
	protected static User theUser;
	protected static Pane theRootPane;
	public static Scene theManageInvitationsScene;
	
	// Header widgets (title)
	protected static Label label_PageTitle = new Label("Manage Invitations");
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	
	// Body widgets (scrollable table)
	protected static ScrollPane scrollPane_Invitations = new ScrollPane();
	protected static VBox vBox_InvitationList = new VBox(10);
	protected static Button button_Revoke = new Button("Revoke selected invitations");
	
	// Footer widgets (navigation)
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
	// Helper class to associate Invitation objects with their checkboxes
	public static class InvitationRow {
		
		public CheckBox checkBox;
		public Invitation invitation;
		
		public InvitationRow(CheckBox cb, Invitation inv) {
			this.checkBox = cb;
			this.invitation = inv;
		}
	}
	
	protected static List<InvitationRow> invitationRows = new ArrayList<>();
	
	/**
	 * Entry point for displaying the page.
	 */
	public static void displayManageInvitations(Stage ps, User user) {
		
		// Copy references
		theStage = ps;
		theUser = user;
		
		// Instantiate singleton
		// (this calls the private constructor)
		if (theView == null) {
			theView = new ViewManageInvitations();
		}
		
		// Update user details label
		label_UserDetails.setText("User: " + theUser.getUserName());
		
		// Refresh data
		ControllerManageInvitations.populateInvitationList();
		theStage.setTitle("Manage Invitations Page");
		theStage.setScene(theManageInvitationsScene);
		theStage.show();
		
		// sanity check - this is just to make sure the correct font is being used
		System.out.println(label_PageTitle.getFont());
	}
	
	/**
	 * Private constructor to enforce singleton pattern.
	 * (Remember the entry point is displayManageInvitations)
	 */
	private ViewManageInvitations() {
		
		// Cause a scene
		theRootPane = new Pane();
		theManageInvitationsScene = new Scene(theRootPane, width, height);
		
		// ===== Header widgets =====
		
		// Apply styles
		setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 5);
		
		label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(55);
		label_UserDetails.setMinWidth(width);
		label_UserDetails.setAlignment(Pos.BASELINE_LEFT);
		
		setupButtonUI(button_UpdateThisUser, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser);
		});
		
		// ===== Body widgets =====
		
		// Setup body table ScrollPane container
		scrollPane_Invitations.setLayoutX(20);
		scrollPane_Invitations.setLayoutY(110);
		scrollPane_Invitations.setPrefSize(width - 40, 340);
		scrollPane_Invitations.setContent(vBox_InvitationList);
		scrollPane_Invitations.setFitToWidth(true);
		vBox_InvitationList.setPadding(new Insets(10));
		
		// Register revoke button listener
		setupButtonUI(button_Revoke, 250, Pos.CENTER, width / 2 - 125, 465);
		button_Revoke.setDisable(true); // Disabled until/unless selection made
		button_Revoke.setOnAction((_) -> ControllerManageInvitations.performRevoke());
		
		// ===== Footer widgets =====
		
		// Register nav button listeners
		setupButtonUI(button_Return, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> ControllerManageInvitations.performReturn());
		setupButtonUI(button_Logout, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> ControllerManageInvitations.performLogout());
		setupButtonUI(button_Quit, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> ControllerManageInvitations.performQuit());
		
		
		String css = getClass().getResource("/application.css").toExternalForm();
		theManageInvitationsScene.getStylesheets().add(css);
		
		label_PageTitle.getStyleClass().add("title");
		
		// Add all widgets to page
		theRootPane.getChildren().addAll(
			label_PageTitle,
			label_UserDetails,
			button_UpdateThisUser,
			line_Separator1,
			scrollPane_Invitations,
			button_Revoke,
			line_Separator4,
			button_Return,
			button_Logout,
			button_Quit);
	}
	
	/**
	 * Helper for applying label styles
	 */
	private void setupLabelUI(Label l, double w, Pos p, double x, double y) {
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	/**
	 * Helper for applying button styles
	 */
	private void setupButtonUI(Button b, double w, Pos p, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}