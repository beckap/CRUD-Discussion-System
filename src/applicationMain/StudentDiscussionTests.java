package applicationMain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import database.Database;
import entityClasses.Post;
import entityClasses.PostCategory;
import entityClasses.PostStorage;
import entityClasses.PostType;
import entityClasses.Reply;
import entityClasses.ReplyStorage;
import entityClasses.User;

/**
 * <p><b>Class:</b> StudentDiscussionTests (JUnit)
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 *   Implements unit and integration tests using JUnit,
 *   including full test coverage of user stories for the Student role.
 * </p>
 * 
 * <p><b>Student user stories tested:</b></p>
 * <ul>
 *   <li>Create posts and receive replies</li>
 *   <li>See a list of others' posts</li>
 *   <li>Create replies to posts</li>
 *   <li>See how many replies each post has received</li>
 *   <li>See a list of my own posts and the number of unread replies</li>
 *   <li>Filter the list of posts by category and keyword</li>
 *   <li>Edit and delete my own posts and replies</li>
 *   <li>Cannot edit or delete others' posts and replies</li>
 * </ul>
 * 
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>JUnit tests implemented using \@Test method decorator.</li>
 *   <li>Reproducible test environment and database implemented using Mockito.</li>
 *   <li>Object validation implemented using junit.Assert methods</li>
 *   <li>Database I/O validation implemented using mockito.Mockito.verify()</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.0 Created
 * 
 * @author Hannah Henderson
 * @version 1.1 Added documentation
 */
class StudentDiscussionTests {
	
	/**
	 * Test #1: No error for OK post
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 1.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCreatingGoodPost() throws SQLException {
		
		// Create mock database
	    Database mockDB = mock(Database.class);
	    PostStorage mockpostStorage = new PostStorage(mockDB);
	   
	    // Define expected error message (none)
	    String supposedErrorMsg = "";
	    User user = new User();
	    user.setUserName("rperezg4");

	    // Generate actual error message for a good title and content
	    String actualErrorMsg = mockpostStorage.createPost("Good Title", "Non empty content",
	            user, PostCategory.GENERAL, PostType.POST);

	    // If they are equal (no error) then the test passes
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #2: Database updated correctly for OK post
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 1.2 (Saves post to database)")
	void testCreatePostCallsDatabaseRegisterWhenCreatingGoodPost() throws SQLException {
	    Database mockDB = mock(Database.class);
	    PostStorage mockPostStorage = new PostStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    mockPostStorage.createPost("Good Title", "Non empty content", user, PostCategory.GENERAL, PostType.POST);
	    verify(mockDB, times(1)).registerPost(any());
	}
	
	/**
	 * Test #3: Correct error for bad (no title) post
	 */
	@Test
	@DisplayName("Test case 2.1 (Error \'The title cannot be empty\')")
	void testErrorMessageWhenCreatingEmptyTitlePost() {
		Database mockDB = mock(Database.class);
	    PostStorage mockPostStorage = new PostStorage(mockDB);
	    
	    String supposedErrorMsg = "The title cannot be empty";
	    User user = new User();
	    user.setUserName("rperezg4");

	    String actualErrorMsg = mockPostStorage.createPost("", "Non empty content",
	            user, PostCategory.GENERAL, PostType.POST);

	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #4: Database not updated for bad (no title) post
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 2.2 (Does not save post in the database)")
	void testEmptyTitlePostCreationDoesNotSavePost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		
		mockPostStorage.createPost("", "Non Empty content", user, PostCategory.GENERAL, PostType.POST);
		verify(mockDB, never()).registerPost(any());
	}
	
	/**
	 * Test #5: Correct error for bad (no body) post
	 */
	@Test
	@DisplayName("Test case 3.1 (Error \'The content cannot be empty\')")
	void testErrorMessageWhenCreatingEmptyContentPost() {
		Database mockDB = mock(Database.class);
	    PostStorage mockPostStorage = new PostStorage(mockDB);
	    
	    String supposedErrorMsg = "The content cannot be empty";
	    User user = new User();
	    user.setUserName("rperezg4");

	    String actualErrorMsg = mockPostStorage.createPost("Non empty title", "",
	            user, PostCategory.GENERAL, PostType.POST);

	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #6: Database not updated for bad (no body) post
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 3.2 (Does not save post in the database)")
	void testEmptyContentPostCreationDoesNotSavePost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		
		mockPostStorage.createPost("Non empty title", "", user, PostCategory.GENERAL, PostType.POST);
		verify(mockDB, never()).registerPost(any());
	}

	/**
	 * Test #7: No error for OK post edit
	 */
	@Test
	@DisplayName("Test case 4.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCorrectPostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "";
		User user = new User();
		user.setUserName("rperezg4");
		
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		String actualErrorMsg = mockPostStorage.editPost(post, user, "new title", "new content");
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #8: Database updated correctly for OK post edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 4.2 (Updates post in database)")
	void testEditPostCallsDatabaseUpdateWhenCorrectUpdate() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		mockPostStorage.editPost(post, user, "new title", "new content");
		verify(mockDB, times(1)).updatePost(post.getPostId(), "new title", "new content");
	}
	
	/**
	 * Test #9: Correct error for bad (deleted) post edit
	 */
	@Test
	@DisplayName("Test case 5.1 (Error \'Cannot edit a deleted post\')")
	void testErrorMessageWhenDeletedPostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "Cannot edit a deleted post";
		User user = new User();
		user.setUserName("rperezg4");
		
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		post.deletePost();
		
		String actualErrorMsg = mockPostStorage.editPost(post, user, "", "new content");
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #10: Database not updated for bad (deleted) post edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 5.2 (Doesn't update post in database)")
	void testDeletedPostEditDoesNotUpdatePost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		post.deletePost();
		
		mockPostStorage.editPost(post, user, "new title", "new content");
		verify(mockDB, never()).updatePost(post.getPostId(), "new title", "new content");
	}
	
	/**
	 * Test #11: Correct error for bad (no title) post edit
	 */
	@Test
	@DisplayName("Test case 6.1 (Error \'Title cannot be empty\')")
	void testErrorMessageWhenEmptyTitlePostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "Title cannot be empty";
		User user = new User();
		user.setUserName("rperezg4");
		
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		String actualErrorMsg = mockPostStorage.editPost(post, user, "", "new content");
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #12: Database not updated for bad (no title) post edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 6.2 (Doesn't update post in database)")
	void testEmptyTitleEditDoesNotUpdatePost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		mockPostStorage.editPost(post, user, "", "new content");
		verify(mockDB, never()).updatePost(post.getPostId(), "", "new content");
	}
	
	/**
	 * Test #13: Correct error for bad (no body) post edit
	 */
	@Test
	@DisplayName("Test case 7.1 (Error \'Content cannot be empty\')")
	void testErrorMessageWhenEmptyContentPostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "Content cannot be empty";
		User user = new User();
		user.setUserName("rperezg4");
		
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		String actualErrorMsg = mockPostStorage.editPost(post, user, "new title", "");
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #14: Database not updated for bad (no body) post edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 7.2 (Doesn't update post in database)")
	void testEmptyContentEditDoesNotUpdatePost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		
		mockPostStorage.editPost(post, user, "new title", "");
		verify(mockDB, never()).updatePost(post.getPostId(), "new title", "");
	}
	
	/**
	 * Test #15: No error for OK reply
	 */
	@Test
	@DisplayName("Test case 8.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCreatingGoodReply() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    String supposedErrorMsg = "";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    String actualErrorMsg = mockReplyStorage.createReply("Non empty content", user, 1);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #16: Database updated correctly for OK reply
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 8.2 (Saves reply in database")
	void testCreateReplyCallsDatabaseRegisterWhenCreatingGoodPost() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    mockReplyStorage.createReply("Non empty content", user, 1);
	    verify(mockDB, times(1)).registerReply(any());
	}
	
	/**
	 * Test #17: Correct error for bad (no body) reply
	 */
	@Test
	@DisplayName("Test case 9.1 (Error \'The content cannot be empty\')")
	void testErrorMessageWhenCreatingEmptyContentReply() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    String supposedErrorMsg = "The content cannot be empty";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    String actualErrorMsg = mockReplyStorage.createReply("", user, 1);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #18: Database not updated for bad (no body) reply
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 9.2 (Does not save reply)")
	void testEmptyContentReplyCreationDoesNotSaveReply() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    mockReplyStorage.createReply("", user, 1);
	    verify(mockDB, never()).registerReply(any());
	}
	
	/**
	 * Test #19: No error for OK reply edit
	 */
	@Test
	@DisplayName("Test case 10.1 (Return empty error message)")
	void testErrorMessageWhenCorrectReplyEdit() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    String supposedErrorMsg = "";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "old content", user.getUserName());
	    
	    String actualErrorMsg = mockReplyStorage.editReply(reply, user, "new content");
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #20: Database updated correctly for OK reply edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 10.2 (Updates reply in the database)")
	void testEditReplyCallsDatabaseUpdateWhenCorrectEditing() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    Reply reply = new Reply(1, 1, "old content", user.getUserName());
	    
	    mockReplyStorage.editReply(reply, user, "new content");
	    verify(mockDB, times(1)).updateReply(reply.getReplyId(), "new content");;
	}
	
	/**
	 * Test #21: Correct error for bad (no body) reply edit
	 */
	@Test
	@DisplayName("Test case 11.1 (Error \'Content cannot be empty\'")
	void testErrorMessageWhenEmptyContentReplyEdit() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    String supposedErrorMsg = "Content cannot be empty";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    
	    String actualErrorMsg = mockReplyStorage.editReply(reply, user, "");
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #22: Database not updated for bad (no body) reply edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 11.2 (Does not update the reply in database)")
	void testEmptyContentReplyEditDoesNotUpdateDatabase() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    
	    mockReplyStorage.editReply(reply, user, "");
	    verify(mockDB, never()).updateReply(reply.getReplyId(), "");
	}
	
	/**
	 * Test #23: Correct error for bad (deleted) reply edit
	 */
	@Test
	@DisplayName("Test case 12.1 (Error \'You cannot edit a deleted reply\'")
	void testErrorMessageWhenDeletedReplyEditing() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    String supposedErrorMsg = "You cannot edit a deleted reply";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    reply.deleteReply();
	    
	    String actualErrorMsg = mockReplyStorage.editReply(reply, user, "You could look at the Java documentation to fix the error.");
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #24: Database not updated for bad (deleted) reply edit
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 12.2 (Does not update reply in database")
	void testDeletedReplyEditDoesNotUpdateDatabase() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    reply.deleteReply();
	    
	    mockReplyStorage.editReply(reply, user, "You could look at the Java documentation to fix the error.");
	    verify(mockDB, never()).updateReply(reply.getReplyId(), "You could look at the Java documentation to fix the error.");
	}
	
	/**
	 * Test #25: No error for OK post delete
	 */
	@Test
	@DisplayName("Test case 13.1 (Return empty error message)")
	void testEmptyErrorMessageWhenValidPostDelete() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "";
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());
		
		String actualErrorMsg = mockPostStorage.deletePost(post, user);
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #26: Database updated correctly for OK post delete
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 13.2 (Updates the database)")
	void testDeletePostCallsDatabaseDelete() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());
		
		mockPostStorage.deletePost(post, user);
		verify(mockDB, times(1)).deletePost(post.getPostId(), "<Post has been deleted>");
	}
	
	/**
	 * Test #27: No error for OK reply delete
	 */
	@Test
	@DisplayName("Test case 14.1 (Return empty error message)")
	void testEmptyErrorMessageWhenValidDeleteReply() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		String supposedErrorMsg = "";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    
	    String actualErrorMsg = mockReplyStorage.deleteReply(reply, user);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	    
	}
	
	/**
	 * Test #28: Database updated correctly for OK reply delete
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 14.2 (Updates the database)")
	void testDeleteReplyCallsDatabaseDelete() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "You can try to inject dependencies to help with the error", user.getUserName());
	    
	    mockReplyStorage.deleteReply(reply, user);
	    verify(mockDB, times(1)).deleteReply(reply.getReplyId(), "<Reply has been deleted>");
	}
	
	/**
	 * Test #29: Correct error for bad (deleted) post delete
	 */
	@Test
	@DisplayName("Test case 15.1 (Error \'This post has already been deleted\')")
	void testErrorMessageWhenDeletedPostDeletion() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "This post has already been deleted";
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());
		post.deletePost();
		
		String actualErrorMsg = mockPostStorage.deletePost(post, user);
		assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #30: Database not updated for bad (deleted) post delete
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 15.2 (Does not update database)")
	void testDeletedPostDeletionDoesNotCallDatabase() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());
		post.deletePost();
		
		mockPostStorage.deletePost(post, user);
		verify(mockDB, never()).deletePost(post.getPostId(), "<Post has been deleted>");
	}
	
	/**
	 * Test #31: Correct error for bad (deleted) reply delete
	 */
	@Test
	@DisplayName("Test case 16.1 (Error \'This reply has already been deleted\')")
	void testErrorMessageWhenDeletedReplyDeletion() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		String supposedErrorMsg = "This reply has already been deleted";
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "I think you have to base your code from Phase 1 code", user.getUserName());
	    reply.deleteReply();
	    
	    String actualErrorMsg = mockReplyStorage.deleteReply(reply, user);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	    
	}
	
	/**
	 * Test #32: Database not updated for bad (deleted) reply delete
	 * @throws SQLException
	 */
	@Test
	@DisplayName("Test case 16.2 (Does not update database)")
	void testDeletedReplyDeletionDoesNotCallDatabase() throws SQLException {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
	    User user = new User();
	    user.setUserName("rperezg4");
	    
	    Reply reply = new Reply(1, 1, "I think you have to base your code from Phase 1 code", user.getUserName());
	    reply.deleteReply();
	    
	    mockReplyStorage.deleteReply(reply, user);
	    verify(mockDB, never()).deleteReply(reply.getReplyId(), "<Reply has been deleted>");
	}
	
	/**
	 * Test #33: Correct error for bad (forbidden) reply delete
	 */
	@Test
	@DisplayName("Test case 17 (Error \'Students cannot delete someone else's reply\'")
	void testErrorMessageWhenDeletingReplyFromSomeoneElse() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		String supposedErrorMsg = "Students cannot delete someone else's reply";
	    User author = new User();
	    author.setUserName("rperezg4");
	    author.setStudentRoleUser(true);
	    
	    User user = new User();
	    user.setUserName("gharmon1");
	    user.setStudentRoleUser(true);
	    
	    Reply reply = new Reply(1, 1, "I think you have to base your code from Phase 1 code", author.getUserName());
	    
	    String actualErrorMsg = mockReplyStorage.deleteReply(reply, user);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
	
	/**
	 * Test #34: Correct error for bad (forbidden) post delete
	 */
	@Test
	@DisplayName("Test case 18 (Error \'Students cannot delete someone else's post\'")
	void testErrorMessageWhenDeletingPostFromSomeoneElse() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		String supposedErrorMsg = "Students cannot delete someone else's post";
	    User author = new User();
	    author.setUserName("rperezg4");
	    author.setStudentRoleUser(true);
	    
	    User user = new User();
	    user.setUserName("gharmon1");
	    user.setStudentRoleUser(true);
	    
	    Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", author.getUserName());
	    
	    String actualErrorMsg = mockPostStorage.deletePost(post, user);
	    assertEquals(supposedErrorMsg, actualErrorMsg);
	}
}
