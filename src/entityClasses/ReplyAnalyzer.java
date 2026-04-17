package entityClasses;

import java.util.HashMap;
import java.util.List;

/***
 * <p><b>Class: </b> ReplyAnalyzer
 * </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>This class provides logic used to evaluate student participation
 * in the discussion system. It retrieves reply data associated with a 
 * specific student and determines if the requirements has been met or not.
 * In addition, it counts all replies for that specific student to help with
 * the evaluation.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * 	<li>Retrieve replies associated with a student username</li>
 * 	<li>Count the total number of replies created by a student</li>
 * 	<li>Count number of replies made to different posts (one per post)</li>
 * 	<li>Determine whether a student meets the participation requirement</li>
 * </ul>
 * 
 * <p><b>Staff Stories Supported:</b></p>
 * <ul>
 * 	<li>Staff can evaluate a student's participation</li>
 * 	<li>Staff can verify if a student has replied to at least 3 different posts</li>
 * </ul>
 * 
 * <p><b>Notes:</b></p>
 * <ul>
 * 	<li>Reply is retrieved from ReplyStorage</li>
 * 	<li>Unique replies are identified based on Post IDs</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-04-04 Initial version
 * 
 * 
 * 
 */
public class ReplyAnalyzer {
	/***
	 * Object that manages reply storage and data management
	 */
	private ReplyStorage storage;
	/***
	 * Student to be evaluated
	 */
	private String studentUsername;
	/****
	 * Replies list only of the specific student
	 */
	List<Reply> studentReplies;
	/***
	 * Participation progress made by the specific student
	 */
	private int participationProgress;
	
	/***
	 * Constructs a ReplyAnalyzer for a specific student
	 * 
	 * 
	 * @param storage	the ReplyStorage object used to retrieve data
	 * @param username	the username of the student being analyzed
	 * 
	 * 
	 * @throws AnalyzerException
	 * if the username is null or empty
	 */
	public ReplyAnalyzer(ReplyStorage storage, String username) throws AnalyzerException {
		// Validation checks for null or empty usernames
		if (username == null) {
			throw new AnalyzerException("Cannot analyze a null username.");
		} else if (username.isEmpty()) {
			throw new AnalyzerException("Cannot analyze an empty username.");
		}
		this.storage = storage;
		studentUsername = username;
	}
	
	/***
	 * Counts the total number of replies created by the student
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * This method retrieves all replies from the student being analyzed
	 * and returns the total count
	 * </p>
	 * 
	 * @return the number of replies created by the student
	 */
	public int countStudentReplies() {
		storage.populateAllReplies();
		studentReplies = storage.getRepliesByAuthor(studentUsername);
		int count = studentReplies.size();
		if (count > 0) {
			return count;
		}
		return 0;
	}

	/***
	 * Counts the number of unique replies made by the student
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * Unique replies are determined based on distinct posts. If a student replies
	 * multiple times to the same post, only one reply is counted.
	 * </p>
	 * 
	 * 
	 * @return the number of unique posts the student has replied to
	 */
	public int countUniqueReplies() {
		// Hashmap used to ensure each PostID is counted only once
		// when counting unique replies
		HashMap<Long, Character> uniqueReplies = new HashMap<>();
		
		countStudentReplies();
		for (Reply reply: studentReplies) {
			if(uniqueReplies.containsKey(reply.getPostId())) {
				continue;
			}
			uniqueReplies.put(reply.getPostId(), 'Y');
		}
		
		return uniqueReplies.size();
	}
	
	/***
	 * Evaluates whether the student meets the participation requirement or not.
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * The participation requirement is satisfied if the specific student has replied
	 * to at least three different posts
	 * </p>
	 * 
	 * @return true if the student satisfies the requirements, false otherwise
	 */
	public boolean evaluateStudentParticipation() {
		participationProgress = countUniqueReplies();
		
		if (participationProgress >= 3) {
			return true;
		}
		return false;
	}
	
	/***
	 * Evaluates the student participation percentage.
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * The participation requirement is satisfied if the specific student has a 1.0 or more
	 * participation progress. This is used to show the percentage of progress.
	 * </p>
	 * 
	 * @return double value describing progress of student participation
	 */
	public double getParticipationProgress() {
		participationProgress = countUniqueReplies();
		double progress = participationProgress / 3.0;
		
		// If progress is less than 0, set to 0
		if (progress < 0) 
			progress = 0;
		
		// If progress is more than one, so above 3 replies, set to 1
	    if (progress > 1) 
	    	progress = 1;
	    
		return progress;
	}
}
