package guiGradingDashboard;

import java.sql.SQLException;

import database.Database;
import entityClasses.AnalyzerException;
import entityClasses.ReplyAnalyzer;
import entityClasses.ReplyStorage;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.util.Duration;

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
				updateStudentCurrentGrade(studentUsername);
			}
		});
		
		ViewGradingDashboard.comboGrades.setVisible(false);
		ViewGradingDashboard.feedbackArea.setVisible(false);
		ViewGradingDashboard.labelGrade.setVisible(false);
		ViewGradingDashboard.labelFeedback.setVisible(false);
		ViewGradingDashboard.labelStudentGrade.setVisible(false);
		ViewGradingDashboard.buttonUpdate.setVisible(false);
		ViewGradingDashboard.labelSuccess.setVisible(false);
		ViewGradingDashboard.labelPastFeedback.setVisible(false);	
		
		// Define the view to show the user
		ViewGradingDashboard.theRootPane.getChildren().addAll(ViewGradingDashboard.labelPageTitle,
				ViewGradingDashboard.labelSelect, ViewGradingDashboard.comboStudents, 
				ViewGradingDashboard.studentProgress, ViewGradingDashboard.labelGrade,
				ViewGradingDashboard.comboGrades, ViewGradingDashboard.feedbackArea,
				ViewGradingDashboard.labelFeedback, ViewGradingDashboard.labelStudentGrade,
				ViewGradingDashboard.buttonUpdate, ViewGradingDashboard.button_Return,
				ViewGradingDashboard.labelSuccess, ViewGradingDashboard.labelPastFeedback,
				ViewGradingDashboard.button_Logout, ViewGradingDashboard.button_Quit);

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
     * Configures and displays grading controls for the selected student.
     * 
     * <p>
     * This method updates the grading section of the UI. In addition, 
     * it calls updateGradeAndFeedback(student, grade, feedback) from the Model 
     * class to update data stored in the database.
     * Once this is done, it updates the student's current grade using the method
     * updateStudentCurrentGrade(student).
     * 
     * This assumes components have been added to the scene, it just updates their content 
     * and visibility.
     * </p>
     *
     * @param student	the username of the selected student whose grade will be changed
     */
	protected static void setStudentGrade(String student) {
		// Set grades combo box
		ViewGradingDashboard.labelGrade.setText("Grade:");
		ViewGradingDashboard.labelGrade.setStyle("-fx-font-size: 16px;");
		ViewGradingDashboard.labelGrade.setLayoutX(370);
		ViewGradingDashboard.labelGrade.setLayoutY(80);
		ViewGradingDashboard.labelGrade.setVisible(true);
		
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
		ViewGradingDashboard.labelFeedback.setLayoutY(150);
		ViewGradingDashboard.labelFeedback.setVisible(true);
		
		ViewGradingDashboard.feedbackArea.setLayoutX(380);
		ViewGradingDashboard.feedbackArea.setLayoutY(175);
		ViewGradingDashboard.feedbackArea.setPrefWidth(300);
		ViewGradingDashboard.feedbackArea.setPrefHeight(150);
		ViewGradingDashboard.feedbackArea.setWrapText(true);
		ViewGradingDashboard.feedbackArea.clear();
		ViewGradingDashboard.feedbackArea.setVisible(true);
		
		ViewGradingDashboard.buttonUpdate.setVisible(true);
		ViewGradingDashboard.buttonUpdate.setOnAction((_) -> {
			if (ViewGradingDashboard.comboGrades.getSelectionModel().getSelectedItem().equals("<Select a grade>")) {
				ViewGradingDashboard.error.setTitle("Error!");
				ViewGradingDashboard.error.setHeaderText(null);
				ViewGradingDashboard.error.setContentText("Please select a grade.");
				ViewGradingDashboard.error.showAndWait();
				return;
			}
			
			if (ViewGradingDashboard.feedbackArea.getText().length() > 320) {
				ViewGradingDashboard.error.setTitle("Error!");
				ViewGradingDashboard.error.setHeaderText(null);
				ViewGradingDashboard.error.setContentText("Feedback is too long! Try again.");
				ViewGradingDashboard.error.showAndWait();
				return;
			}
			
			try { 
				ModelGradingDashboard.updateGradeAndFeedback(student, 
					ViewGradingDashboard.comboGrades.getSelectionModel().getSelectedItem(),
					ViewGradingDashboard.feedbackArea.getText());
			} catch (SQLException e) {
				ViewGradingDashboard.error.setContentText(e.getMessage());
				ViewGradingDashboard.error.setHeaderText(null);
				ViewGradingDashboard.error.setTitle("Database storing error");
				ViewGradingDashboard.error.showAndWait();
			}
			
			updateStudentCurrentGrade(student);
			ViewGradingDashboard.feedbackArea.clear();
			
			ViewGradingDashboard.labelSuccess.setText("Grade Updated!");
			ViewGradingDashboard.labelSuccess.setStyle("-fx-text-fill: #808388;-fx-text-size:16px;");
			ViewGradingDashboard.labelSuccess.setVisible(true);
			
			// Set up transition so labelSuccess displays only for a few seconds
			PauseTransition pause = new PauseTransition(Duration.seconds(2));
		    pause.setOnFinished((_) -> ViewGradingDashboard.labelSuccess.setVisible(false)); // Hide after delay
		    pause.play();
		});
	}
	
	/**
	 * Displays the current grade of the selected student.
	 * 
	 * <p>
	 * This method updates the label responsible for showing the student’s
	 * existing grade. If no grade is available, a default value
	 * is displayed. 
	 * 
	 * Calls getStudentGrade(student) and getStudentFeeback(student) methods
	 * from the Model class to aid the process.
	 * </p>
	 *
	 *
	 * @param student the username of the student whose current grade is to be displayed
	 */
	protected static void updateStudentCurrentGrade(String student) {
		try {
			String grade = ModelGradingDashboard.getStudentGrade(student);
			String feedback = ModelGradingDashboard.getStudentFeedback(student);
			
			if (grade == null) {
				ViewGradingDashboard.labelStudentGrade.setText("Student's current grade: N/A");
			} else {
				ViewGradingDashboard.labelStudentGrade.setText("Student's current grade: " + grade);
			}
			
			if (feedback == null) {
				ViewGradingDashboard.labelPastFeedback.setText("Previous feedback: N/A");
			} else if(feedback.isEmpty()) {
				ViewGradingDashboard.labelPastFeedback.setText("Previous feedback: No previous feedback.");
			} else {
				ViewGradingDashboard.labelPastFeedback.setText("Previous feedback: " + feedback);
			}
		} catch (SQLException e) {
			ViewGradingDashboard.error.setContentText(e.getMessage());
			ViewGradingDashboard.error.setHeaderText(null);
			ViewGradingDashboard.error.setTitle("Database retrieval error");
			ViewGradingDashboard.error.showAndWait();
		}
		
		ViewGradingDashboard.labelStudentGrade.setLayoutX(370);
		ViewGradingDashboard.labelStudentGrade.setLayoutY(370);
		ViewGradingDashboard.labelStudentGrade.setVisible(true);
		ViewGradingDashboard.labelPastFeedback.setWrapText(true);
		ViewGradingDashboard.labelPastFeedback.setMaxWidth(400);
		ViewGradingDashboard.labelPastFeedback.setMaxHeight(160);
		ViewGradingDashboard.labelPastFeedback.setVisible(true);
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
