package guiGradingDashboard;

import database.Database;
import entityClasses.AnalyzerException;
import entityClasses.ReplyAnalyzer;
import entityClasses.ReplyStorage;
import javafx.collections.FXCollections;

/*******
 * <p>
 * Title: ControllerGradingDashboard Class.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-04-16 Initial version
 * 
 */
public class ControllerGradingDashboard {
	
	/***
	 * Reference to database so package has access to methods
	 */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static ReplyStorage replyStorage = new ReplyStorage(theDatabase);
	
	/***
	 * Default Constructor never used in this system.
	 */
	public ControllerGradingDashboard() {
	}
	
	
	/**********
	 * <p>
	 * Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then
	 * establishes the components in the Pane to display the proper current data. 
	 * </p>
	 * 
	 */
	@SuppressWarnings("null")
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewGradingDashboard.theRootPane.getChildren().clear();
		
		// Reset ComboBox list
		ViewGradingDashboard.students = ModelGradingDashboard.getUserList();
		if (ViewGradingDashboard.students != null)
			ViewGradingDashboard.comboStudents.setItems(FXCollections.observableArrayList(ViewGradingDashboard.students));
		ViewGradingDashboard.comboStudents.getSelectionModel().selectFirst();
		ViewGradingDashboard.studentProgress.setProgress(0);
		
		ViewGradingDashboard.comboStudents.setOnAction((_) -> {
			String studentUsername = ViewGradingDashboard.comboStudents.getSelectionModel().getSelectedItem();
			
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
		
		// Define the view to show the user
		ViewGradingDashboard.theRootPane.getChildren().addAll(ViewGradingDashboard.labelPageTitle,
				ViewGradingDashboard.labelSelect, ViewGradingDashboard.comboStudents, 
				ViewGradingDashboard.studentProgress, ViewGradingDashboard.button_Return, ViewGradingDashboard.button_Logout);

		// Set the title for the window
		ViewGradingDashboard.theStage.setTitle("Grading Dashboard");
		ViewGradingDashboard.theStage.setScene(ViewGradingDashboard.theGradingDashboardScene);
		ViewGradingDashboard.theStage.show();
	}
	
	protected static void addProgress(String student) {
		ReplyAnalyzer analyzer = null;
		try {
			analyzer = new ReplyAnalyzer(replyStorage, student);
			double progressPercent = analyzer.getParticipationProgress();
			ViewGradingDashboard.studentProgress.setProgress(progressPercent);
			ViewGradingDashboard.studentProgress.requestLayout();
		} catch (AnalyzerException e) {
			e.printStackTrace();
			ViewGradingDashboard.error.setContentText("Error analyzing student participation.");
			ViewGradingDashboard.error.setHeaderText(null);
			ViewGradingDashboard.error.setTitle("Analyzing Error");
			ViewGradingDashboard.error.show();
		}
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user home page. </p>
	 * 
	 */
	protected static void performReturn() {
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		case 3:
			guiStudent.ViewStudentHome.displayStudentHome(ViewGradingDashboard.theStage, ViewGradingDashboard.theUser);
			break;
		default:
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal
	 * login page where existing users can log in or potential new users with a
	 * invitation code can start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewGradingDashboard.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted. </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
