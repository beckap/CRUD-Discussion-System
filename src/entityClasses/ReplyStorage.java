package entityClasses;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import database.Database;


/*******
 * <p><b>Class:</b> ReplyStorage class.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 * Manages all Reply objects in the system. It is responsible for creating, retrieving, 
 * filtering, editing, and deleting replies. 
 * 
 * It serves as the connection between GUI Controller classes and the Database layer.
 * </p>
 * 
 * <p><b>Student User Stories Supported:</b></p>
 * <ul>
 *   <li>Create a reply to a post</li>
 *   <li>View replies for a specific post</li>
 *   <li>Filter replies by author or date</li>
 *   <li>Edit own replies</li>
 *   <li>Delete own replies (Staff can delete any reply)</li>
 * </ul>
 * 
 * <p><b>Design Notes:</b></p>
 * <ul>
 *   <li>Soft deletion is used to keep track of reply history.</li>
 *   <li>Validation is performed before database operations to maintain data integrity.</li>
 *   <li>Replies are linked to posts using postId rather than object references to simplify
 *       database relationships.</li>
 * </ul>
 * 
 * 
 * @author Becka Perez Guerrero
 * @version 1.0
 * 
 */
public class ReplyStorage {
	
	/**
	 * Complete list of all replies currently in memory.
	 */
	List<Reply> replies = new ArrayList<>();
	
	/**
	 * Filtered list used for general filtering operations based on user action.
	 */
	List<Reply> filteredReplies = new ArrayList<>();
	
	/**
	 * Stores replies associated with a specific post.
	 */
	List<Reply> postReplies = new ArrayList<>();
	
	/** 
	 * Reference for the in-memory database so this package has access.
	 * Required for persistence operations (create, update, delete).
	 */
	private Database theDatabase;
	
	/*****
	 * <p>
	 * Method: ReplyStorage(Database db)
	 * </p>
	 * 
	 * <p>
	 * Description: This constructor is used to establish replyStorage objects.
	 * 
	 * Initializes the object with a database reference.
	 * </p>
	 * 
	 * @param db	specifies the db
	 * 
	 */
	public ReplyStorage(Database db) {
		this.theDatabase = db;
	}
	/*****
	 * Creates a reply and stores it in both memory and the database
	 * 
	 * <p><b>Notes:</b></p>
	 * <p>
	 * - Content must not be empty
	 * </p>
	 * 
	 * @param content	content of post
	 * @param user		user making the post
	 * @param postId 	post ID that the reply is linked to
	 * 
	 * 
	 * @return string to update status and send error messages
	 */
	public String createReply(String content, User user, long postId) {
		
		if(content == null || content.isEmpty()) {
			return "The content cannot be empty";
		}
		
		Reply newReply = new Reply(0, postId, content, user.getUserName());
		
		try {
			theDatabase.registerReply(newReply);
			replies.add(newReply);
		} catch(SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		return "";
	}
	
	/*****
	 * <p> Method: hasHigherPrivilege(String authorUsername) </p>
	 * <p> Description: Checks if the current user has strictly higher privilege than the author. </p>
	 */
	private boolean hasHigherPrivilege(String authorUsername) {
		int currentPrivilege = theDatabase.getCurrentAdminRole() ? 2 : (theDatabase.getCurrentNewStaffRole() ? 1 : 0);
		int authorPrivilege = theDatabase.getUserPrivilegeLevel(authorUsername);
		return currentPrivilege > authorPrivilege;
	}

	/*****
	 * <p>
	 * Method: ArrayList getAllReplies()
	 * </p>
	 * * <p>
	 * Description: Retrieves all replies from the database.
	 * </p>
	 * */
	public void populateAllReplies() {
		replies = theDatabase.getRepliesList();
		
		// Filter out deleted replies for users without higher privilege
		replies.removeIf(reply -> reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername()));
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getRepliesByDate(LocalDateTime date)
	 * </p>
	 * * <p>
	 * Description: Filters replies by date.
	 * </p>
	 * * @param date	specifies date to search
	 * * @return filtered list with replies based on date.
	 */
	public List<Reply> getRepliesByDate(LocalDateTime date) {
		filteredReplies.clear();
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getDatePosted().toLocalDate().equals(date.toLocalDate())) {
				filteredReplies.add(reply);
			}
		}
		
		return filteredReplies;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getRepliesByAuthor(String author)
	 * </p>
	 * * <p>
	 * Description: Filters replies by author.
	 * </p>
	 * * @param author	specifies author to search replies
	 * * @return filtered list with replies based on author.
	 */
	public List<Reply> getRepliesByAuthor(String author) {
		filteredReplies.clear();
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getAuthorUsername().equalsIgnoreCase(author)) {
				filteredReplies.add(reply);
			}
		}
		
		return filteredReplies;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getRepliesByPostId(int postId)
	 * </p>
	 * * <p>
	 * Description: Filters replies by post ID.
	 * </p>
	 * * @param postId	specifies post id the replies are linked to
	 * * @return filtered list with replies based on post.
	 */
	public List<Reply> getRepliesByPostId(long postId) {
		postReplies.clear();
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getPostId() == postId) {
				postReplies.add(reply);
			}
		}
		
		return postReplies;
	}

	/*****
	 * <p>
	 * Method: ArrayList getNumRepliesByPostId(int postId)
	 * </p>
	 * * <p>
	 * Description: Returns the number of non-deleted replies of the
	 * post with the given ID.
	 * </p>
	 * * @param postId Specifies post ID the replies are linked to.
	 * * @return Number of replies.
	 */
	public int getNumRepliesByPostId(long postId) {
		int numReplies = 0;
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getPostId() == postId) {
				numReplies++;
			}
		}
		
		return numReplies;
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getNumUnreadRepliesByPostId(int postId)
	 * </p>
	 * * <p>
	 * Description: Returns the number of non-deleted unread replies of the
	 * post with the given ID.
	 * </p>
	 * * @param postId Specifies post ID the replies are linked to.
	 * * @return Number of unread replies.
	 */
	public int getNumUnreadRepliesByPostId(long postId) {
		int numUnreadReplies = 0;
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getPostId() == postId && !reply.isReadByPostAuthor()) {
				numUnreadReplies++;
			}
		}
		
		return numUnreadReplies;
	}

	/*****
	 * <p>
	 * Method: markRepliesAsRead(Post post, User currentUser)
	 * </p>
	 * * <p>
	 * Description: Marks replies as read if user is the post author
	 * </p>
	 * * @param post The post object
	 * @param currentUser The current user
	 * * @return void
	 */
	public void markRepliesAsRead(Post post, User currentUser) {
		//Nullcheck
		if (post == null || currentUser == null) return;
		
		// Only the original author marks things as read
		if (!post.getAuthorUsername().equals(currentUser.getUserName())) return;
		
		boolean requiresDbUpdate = false;
		for (Reply reply : replies) {
			if (!(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) && 
				reply.getPostId() == post.getPostId() && !reply.isReadByPostAuthor()) {
				reply.markRead();
				requiresDbUpdate = true;
			}
		}
		
		if (requiresDbUpdate) {
			try {
				theDatabase.markRepliesAsRead(post.getPostId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*****
	 * <p>
	 * Method: ArrayList getRepliesByKeyword(String keyword)
	 * </p>
	 * * <p>
	 * Description: Filters replies by keyword.
	 * </p>
	 * * @param keyword	specifies keyword to search
	 * * @return filtered list with replies based on a keyword
	 */
	public List<Reply> getRepliesByKeyword(String keyword) {
		filteredReplies.clear();
		
		if (keyword == null || keyword.isEmpty()) {
			return filteredReplies;
		}
		
		String keywordInLower = keyword.toLowerCase();
		
		for (Reply reply: replies) {
			if(reply.isDeleted() && !hasHigherPrivilege(reply.getAuthorUsername())) {
				continue;
			}
			
			if(reply.getContent().toLowerCase().contains(keywordInLower)) {
				filteredReplies.add(reply);
			}
		}
		
		return filteredReplies;
	}
	
	/*****
	 * This soft deletes a reply in the list.
	 * * <p><b>Notes:</b></p>
	 * <p>
	 * Rules:
	 * - Only the author can delete their post
	 * - Post is not removed, only marked deleted
	 * </p>
	 * * @param reply		reply to be deleted
	 * @param user		user using the system
	 * * @return string to update status and send error messages
	 */
	public String deleteReply(Reply reply, User user) {
		
		if (reply == null) {
			return "Reply does not exist";
		}
		
		if (reply.isDeleted()) {
			return "This reply has already been deleted";
		}
		
		if (!reply.getAuthorUsername().equals(user.getUserName())) {
			return "You cannot delete someone else's reply";
		}
		
		try {
			theDatabase.deleteReply(reply.getReplyId(), "");
			reply.deleteReply();
			// Do NOT replace the content with deleteMessage, so higher privileges can still read it.
		} catch(SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		return "";
	}
	
	/*****
	 * <p>
	 * Method: String editReply(int replyId, User user, String content)
	 * </p>
	 * * <p>
	 * Description: This edits a post.
	 * </p>
	 * * @param reply		reply to be edited
	 * @param user		user using the system
	 * @param content	new content
	 * * @return string to update status and send error messages
	 */
	public String editReply(Reply reply, User user, String content) {
		
		if (reply == null) {
			return "Reply does not exist";
		}
		
		if (reply.isDeleted()) {
			return "You cannot edit a deleted reply";
		}
		if (content == null || content.isEmpty()) {
			return "Content cannot be empty";
		}
		
		if (!reply.getAuthorUsername().equals(user.getUserName())) {
			return "You cannot edit someone else's reply";
		}
		
		try {
			theDatabase.updateReply(reply.getReplyId(), content);
			reply.editReply(content);
		} catch(SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		
		return "";
	}
	
	/*****
	 * <p>
	 * Method: String displayReply(Reply reply)
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the proper string used to display a Reply.
	 * </p>
	 * 
	 * @param reply		reply to be displayed
	 * 
	 * @return content displayed in UI
	 */
	public String displayReply(Reply reply) {
		String author = reply.getAuthorUsername();
		String content = reply.getContent();
		return   author + "\t\t\t" + reply.getDatePosted().toLocalDate() + "\n" + content + "\n\n";
	}
	
	/*****
	 * <p>
	 * Method: Reply findReply(int replyId)
	 * </p>
	 * 
	 * <p>
	 * Description: This finds a reply in the list.
	 * </p>
	 * 
	 * @param replyId	id of reply
	 * 
	 * @return reply if found, null if not
	 */
	@SuppressWarnings("unused")
	private Reply findReply(int replyId) {
		for(Reply reply : replies) {
			if (reply.getReplyId() == replyId) {
				return reply;
			}
		}
		return null;
	}
	
	/*******
	 * <p> Method: updateReplyVisibility(long postId, int visibilityLevel) </p>
	 * <p> Description: Updates the visibility level of a specific reply. </p>
	 */
	public void updateReplyVisibility(long replyId, int visibilityLevel) {
		try {
			theDatabase.updateReplyVisibility(replyId, visibilityLevel);
		} catch (Exception e) {
			System.out.println("Error updating post visibility: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
