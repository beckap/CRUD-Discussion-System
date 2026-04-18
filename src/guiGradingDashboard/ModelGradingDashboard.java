package guiGradingDashboard;

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
     * This method ensures that only students are added to the list.
     * </p>
     * 
     * @return a list containing student usernames
     */
	public static List<String> getUserList() {
		return ControllerGradingDashboard.theDatabase.getStudentUserList();
	}
	
	/**
	 * 
	 * @return a list containing letter grades
	 */
	public static List<String> getGradesList() {
		List<String> letterGrades = new ArrayList<>(List.of("<Select a grade>", "A", 
				"A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"));
		
		return letterGrades;
	}
}
