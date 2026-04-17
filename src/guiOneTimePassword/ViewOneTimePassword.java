package guiOneTimePassword;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p>
 * Title: ViewOneTimePassword Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based page for creating a one time use password.
 * </p>
 * 
 * 
 * @author Diogo Oliveira Moscato, Becka Perez Guerrero
 * 
 * @version 1.00 2026-01-31 Initial version
 * @version 2.00 2026-02-09 Shows specific list combo box based on role 
 * 							by Becka Perez Guerrero
 * 
 */

public class ViewOneTimePassword {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	protected static Line line_Separator2 = new Line(20, 525, width - 20, 525);
	
	protected static Label labelTemp = new Label();
	protected static Label labelSelect = new Label();
	protected static List<String> users = new ArrayList<String>();
	protected static ComboBox<String> comboUsers = new ComboBox<String>();
	protected static PasswordField textTempPassword = new PasswordField();
	protected static Button button_Generate = new Button("Generate OTP");
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
	private static ViewOneTimePassword theView;

	protected static Stage theStage;
	protected static Pane theRoot;
	public static Scene theScene;
	private static javafx.scene.Scene previousScene;
	protected static User theUser;

	public static void displayOneTimePassword(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		// store previous scene so Return can go back
		previousScene = ps.getScene();
		
		if(theView == null) theView = new ViewOneTimePassword();
		
		ControllerOneTimePassword.repaintTheWindow();
	}
	
	private ViewOneTimePassword() {
		theRoot = new Pane();
		theScene = new Scene(theRoot, width, height);

		label_PageTitle = new Label("One-Time Password Generator");
		label_PageTitle.setLayoutX(0);
		label_PageTitle.setLayoutY(5);
		label_PageTitle.setMinWidth(width);
		label_PageTitle.setAlignment(Pos.CENTER);

		label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(55);
		label_UserDetails.setMinWidth(width);
		label_UserDetails.setAlignment(Pos.BASELINE_LEFT);

		setupButtonUI(button_UpdateThisUser, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser);
		});

		labelSelect = new Label("Select user:");
		labelSelect.setLayoutX(140);
		labelSelect.setLayoutY(120);

		comboUsers.setLayoutX(140);
		comboUsers.setLayoutY(145);
		comboUsers.setMinWidth(520);

		users = ModelOneTimePassword.getUserList();
		if (users != null)
			comboUsers.setItems(FXCollections.observableArrayList(users));
		
		comboUsers.getSelectionModel().selectFirst();

		labelTemp = new Label("Temporary password:");
		labelTemp.setLayoutX(140);
		labelTemp.setLayoutY(195);

		textTempPassword.setLayoutX(140);
		textTempPassword.setLayoutY(220);
		textTempPassword.setMinWidth(520);

		setupButtonUI(button_Generate, 300, Pos.CENTER, 250, 280);
		button_Generate.setOnAction((_) -> {
			String username = comboUsers.getSelectionModel().getSelectedItem();
			String temp = textTempPassword.getText();
			ControllerOneTimePassword.generateOTP(username, temp);
		});
		// Keep Return behavior (back to login) -- place at bottom like ListAllUsers
		setupButtonUI(button_Return, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {
			if (previousScene != null) {
				theStage.setScene(previousScene);
				theStage.show();
			} else {
				guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
			}
		});

		// Logout and Quit using controller handlers -- place at bottom like
		// ListAllUsers
		setupButtonUI(button_Logout, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {
			ControllerOneTimePassword.performLogout();
		});

		setupButtonUI(button_Quit, 210, Pos.CENTER, 580, 540);
		button_Quit.setOnAction((_) -> {
			ControllerOneTimePassword.performQuit();
		});
		
		// CSS Styling for the page
		String css = getClass().getResource("/application.css").toExternalForm();
		theScene.getStylesheets().add(css);
		
		label_PageTitle.getStyleClass().add("title");
		textTempPassword.getStyleClass().add("text-fields");
		
		DialogPane alert1 = ControllerOneTimePassword.a.getDialogPane();
		alert1.getStylesheets().add(css);

		theRoot.getChildren().addAll(label_PageTitle, label_UserDetails, button_UpdateThisUser,
				line_Separator1, labelSelect, comboUsers, labelTemp, textTempPassword, button_Generate,
				line_Separator2, button_Return, button_Logout, button_Quit);
		theStage.setScene(theScene);
		theStage.show();
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
