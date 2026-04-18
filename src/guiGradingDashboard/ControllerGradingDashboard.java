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
 * <p><b>
 * Description:
 * </b></p>
 * 
 * <p>
 * Controller class for the Grading dashboard.
 * 
 * This class is responsible for handling user interactions from the user interface
 * displayed by the View class. It coordinates updates between the View and the Model classes.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *  <li>Responding to user input events</li>
 *  <li>Updating progress indicators and grade display</li>
 *  <li>Managing communication with data</li>
 * </ul>
 * 
 * <p><b>User stories supported:</b></p>
 * <ul>
 * 	<li>Staff can review student participation progress</li>
 *  <li>Staff can assign grades based on participation requirements</li>
 *  <li>Staff can provide extra feedback to the students</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-04-16 Initial version
 * 
 */
public class ControllerGradingDashboard {
	
	/***
	 * Provides access to data used by the application
	 */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * Storage handler for retrieving student replies data
	 */
	protected static ReplyStorage replyStorage = new ReplyStorage(theDatabase);
	
	/***
	 * Default Constructor 
	 * 
	 * <p>
	 * This is never used since all members and methods are static.
	 * </p>
	 */
	public ControllerGradingDashboard() {
	}
	
	
	/**********
	 * Rebuilds and refreshes the grading dashboard UI.
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
		
		// Set Action when selecting a student
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
				addProgress(studentUsername);
				setStudentGrade(studentUsername);
				setStudentCurrentGrade(studentUsername);
			}
		});
		
		ViewGradingDashboard.comboGrades.setVisible(false);
		ViewGradingDashboard.feedbackArea.setVisible(false);
		
		// Define the view to show the user
		ViewGradingDashboard.theRootPane.getChildren().addAll(ViewGradingDashboard.labelPageTitle,
				ViewGradingDashboard.labelSelect, ViewGradingDashboard.comboStudents, 
				ViewGradingDashboard.studentProgress, ViewGradingDashboard.labelGrade,
				ViewGradingDashboard.comboGrades, ViewGradingDashboard.feedbackArea,
				ViewGradingDashboard.labelFeedback, ViewGradingDashboard.labelStudentGrade,
				ViewGradingDashboard.button_Return, ViewGradingDashboard.button_Logout, ViewGradingDashboard.button_Quit);

		// Set the title for the window
		ViewGradingDashboard.theStage.setTitle("Grading Dashboard");
		ViewGradingDashboard.theStage.setScene(ViewGradingDashboard.theGradingDashboardScene);
		ViewGradingDashboard.theStage.show();
	}
	
	/**
	 * Calculates and updates the participation progress for a student.
	 * 
	 * <p>Method: addProgress(Sting student)</p>
	 * 
     * <p>
     * This method uses the ReplyAnalyzer to compute the student's
     * participation progress and updates the progress indicator in the view.
     * </p>
	 * 
	 * @param student	username of selected student
	 */
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
	
	/**
     * Prepares the grade selection UI for the selected student.
     * <p>
     * This method updates the grade UI.
     * </p>
     *
     * @param student	the username of the selected student
     */
	protected static void setStudentGrade(String student) {
		// Set grades combo box
		ViewGradingDashboard.labelGrade.setText("Grade:");
		ViewGradingDashboard.labelGrade.setStyle("-fx-font-size: 16px;");
		ViewGradingDashboard.labelGrade.setLayoutX(370);
		ViewGradingDashboard.labelGrade.setLayoutY(80);
		
		ViewGradingDashboard.grades = ModelGradingDashboard.getGradesList();
		if (ViewGradingDashboard.grades != null) {
			ViewGradingDashboard.comboGrades.setItems(FXCollections.observableArrayList(
					ViewGradingDashboard.grades));
		}
		ViewGradingDashboard.comboGrades.setLayoutX(400);
		ViewGradingDashboard.comboGrades.setLayoutY(110);
		ViewGradingDashboard.comboGrades.getSelectionModel().selectFirst();
		ViewGradingDashboard.comboGrades.setVisible(true);
		
		// Set feedback text area
		ViewGradingDashboard.labelFeedback.setText("Enter feedback:");
		ViewGradingDashboard.labelFeedback.setStyle("-fx-font-size: 16px;");
		ViewGradingDashboard.labelFeedback.setLayoutX(370);
		ViewGradingDashboard.labelFeedback.setLayoutY(160);
		
		ViewGradingDashboard.feedbackArea.setLayoutX(380);
		ViewGradingDashboard.feedbackArea.setLayoutY(190);
		ViewGradingDashboard.feedbackArea.setPrefWidth(300);
		ViewGradingDashboard.feedbackArea.setPrefHeight(150);
		ViewGradingDashboard.feedbackArea.setWrapText(true);
		ViewGradingDashboard.feedbackArea.clear();
		ViewGradingDashboard.feedbackArea.setVisible(true);
	}
	
	protected static void setStudentCurrentGrade(String student) {
		ViewGradingDashboard.labelStudentGrade.setText("Student's current grade: N/A");
		ViewGradingDashboard.labelStudentGrade.setStyle("-fx-font-size: 16px;");
		ViewGradingDashboard.labelStudentGrade.setLayoutX(80);
		ViewGradingDashboard.labelStudentGrade.setLayoutY(410);
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
