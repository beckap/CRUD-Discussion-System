package guiGradingDashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/***
 * <p>
 * Title: ModelGradingDashboard Class.
 * </p>
 * 
 * <p><b> Description: 
 * </b></p>
 * 
 * <p>
 * Provides data access methods for the UI. This class is the bridge between
 * the Controller/View classes and the database. It retrieves a list of students from storage.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Retrieves lists of student usernames</li>
 *   <li>Provides predefined grade options</li>
 *   <li>Updates grades in the database</li>
 *   <li>Retrieves grades from the database</li>
 * </ul>
 * </p>
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-04-17 Initial version
 */
public class ModelGradingDashboard {
	
	/**
     * Retrieves a list of all student usernames available in the database.
     * 
     * <p>This method makes a request to the database, delegating 
     * the retrieval of the student list to getStudentUserList().
     * 
     * The database method ensures that only students are added to the list.
     * </p>
     * 
     * @return a list containing student usernames stored in database
     */
	protected static List<String> getUserList() {
		return ControllerGradingDashboard.theDatabase.getStudentUserList();
	}
	
	/**
	 * Provides a predefined list of letter grades.
	 * <p>
	 * The method includes a default placeholder option followed by
	 * standard academic letter grades.
	 * </p>
	 *
	 * @return a list containing predefined grade options
	 */
	protected static List<String> getGradesList() {
		List<String> letterGrades = new ArrayList<>(List.of("<Select a grade>", "A", 
				"A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"));
		
		return letterGrades;
	}
	
	/**
	 * Inserts or updates a student's grade and associated feedback in the database.
	 * 
	 * <p>
	 * This method checks whether a grade record already exists for the specified
	 * student. If no record is found, a new grade entry is inserted. Otherwise,
	 * the existing record is updated with the provided grade and feedback.
	 * </p>
	 *
	 * <p>
	 * This method performs only data access logic and does not handle any UI-related
	 * concerns. Any database errors are delegated to the Controller class.
	 * </p>
	 *
	 * @param username	the username of the selected student
	 * @param grade		the letter grade to assign to the student
	 * @param feedback	the feedback associated with the grade
	 * @throws SQLException if a database access error occurs during retrieval,
	 *         insertion, or update operations
	 */
	protected static void updateGradeAndFeedback(String username, String grade, String feedback) throws SQLException {
		String gradeRetrieved = ControllerGradingDashboard.theDatabase.searchStudentGrade(username);
		if (gradeRetrieved == null) {
			ControllerGradingDashboard.theDatabase.insertGrade(username, grade, feedback);
		} else {
			ControllerGradingDashboard.theDatabase.updateGrade(username, grade, feedback);
		}
	}

	/**
	 * Retrieves the current grade for a specified student.
	 * 
	 * <p>
	 * This method requests the database for a student's grade and returns it
	 * to the Controller. If no grade exists for the student, a null is returned.
	 * </p>
	 *
	 * <p>
	 * This method does not perform any UI updates and delegates error handling
	 * to the Controller class.
	 * </p>
	 *
	 * @param student	the username of the selected student
	 * @return the student's grade, or null if no grade is recorded
	 * @throws SQLException if a database access error occurs during retrieval
	 */
	protected static String getStudentGrade(String student) throws SQLException {
		return ControllerGradingDashboard.theDatabase.searchStudentGrade(student);
	}
	
	/**
	 * Retrieves the current feedback on the database for a specified student.
	 * 
	 * <p>
	 * This method requests the database for a student's feedback from the staff
	 * member and returns it to the Controller. If no feedback exists for the student, 
	 * a null is returned.
	 * </p>
	 *
	 * <p>
	 * This method does not perform any UI updates and delegates error handling
	 * to the Controller class.
	 * </p>
	 *
	 * @param student	the username of the selected student
	 * @return the student's previous feedback from the staff, or null if no feedback is recorded
	 * @throws SQLException if a database access error occurs during retrieval
	 */
	protected static String getStudentFeedback(String student) throws SQLException {
		return ControllerGradingDashboard.theDatabase.searchStudentFeedback(student);
	}
}
