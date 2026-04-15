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
 * <p><b>Class:</b> DefaultPost (JUnit)
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <p>
 *   Reduced set of unit tests covering the core student user stories
 *   for post and reply creation, editing, and deletion.
 * </p>
 *
 * @author Becka Perez Guerrero
 * @version 1.0 Created
 *
 * @author Hannah Henderson
 * @version 1.1 Added documentation
 */
class DefaultPost {

	// ── Post creation ────────────────────────────────────────────────────────

	@Test
	@DisplayName("Test case 1.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCreatingGoodPost() throws SQLException {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");

		String actualErrorMsg = mockPostStorage.createPost("Good Title", "Non empty content",
				user, PostCategory.GENERAL, PostType.POST);

		assertEquals("", actualErrorMsg);
	}

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

	@Test
	@DisplayName("Test case 2.1 (Error 'The title cannot be empty')")
	void testErrorMessageWhenCreatingEmptyTitlePost() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");

		String actualErrorMsg = mockPostStorage.createPost("", "Non empty content",
				user, PostCategory.GENERAL, PostType.POST);

		assertEquals("The title cannot be empty", actualErrorMsg);
	}

	// ── Post editing ─────────────────────────────────────────────────────────

	@Test
	@DisplayName("Test case 4.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCorrectPostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());

		assertEquals("", mockPostStorage.editPost(post, user, "new title", "new content"));
	}

	@Test
	@DisplayName("Test case 5.1 (Error 'Cannot edit a deleted post')")
	void testErrorMessageWhenDeletedPostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());
		post.deletePost();

		assertEquals("Cannot edit a deleted post", mockPostStorage.editPost(post, user, "", "new content"));
	}

	@Test
	@DisplayName("Test case 6.1 (Error 'Title cannot be empty')")
	void testErrorMessageWhenEmptyTitlePostEdit() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "old title", PostCategory.GENERAL, "old content", user.getUserName());

		assertEquals("Title cannot be empty", mockPostStorage.editPost(post, user, "", "new content"));
	}

	// ── Reply creation ───────────────────────────────────────────────────────

	@Test
	@DisplayName("Test case 8.1 (Return empty error message)")
	void testEmptyErrorMessageWhenCreatingGoodReply() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");

		assertEquals("", mockReplyStorage.createReply("Non empty content", user, 1));
	}

	@Test
	@DisplayName("Test case 9.1 (Error 'The content cannot be empty')")
	void testErrorMessageWhenCreatingEmptyContentReply() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");

		assertEquals("The content cannot be empty", mockReplyStorage.createReply("", user, 1));
	}

	// ── Post deletion ────────────────────────────────────────────────────────

	@Test
	@DisplayName("Test case 13.1 (Return empty error message)")
	void testEmptyErrorMessageWhenValidPostDelete() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());

		assertEquals("", mockPostStorage.deletePost(post, user));
	}

	@Test
	@DisplayName("Test case 15.1 (Error 'This post has already been deleted')")
	void testErrorMessageWhenDeletedPostDeletion() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User user = new User();
		user.setUserName("rperezg4");
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", user.getUserName());
		post.deletePost();

		assertEquals("This post has already been deleted", mockPostStorage.deletePost(post, user));
	}

	@Test
	@DisplayName("Test case 17 (Error 'Students cannot delete someone else's reply')")
	void testErrorMessageWhenDeletingReplyFromSomeoneElse() {
		Database mockDB = mock(Database.class);
		ReplyStorage mockReplyStorage = new ReplyStorage(mockDB);
		User author = new User();
		author.setUserName("rperezg4");
		author.setStudentRoleUser(true);
		User user = new User();
		user.setUserName("gharmon1");
		user.setStudentRoleUser(true);
		Reply reply = new Reply(1, 1, "I think you have to base your code from Phase 1 code", author.getUserName());

		assertEquals("Students cannot delete someone else's reply", mockReplyStorage.deleteReply(reply, user));
	}

	@Test
	@DisplayName("Test case 18 (Error 'Students cannot delete someone else's post')")
	void testErrorMessageWhenDeletingPostFromSomeoneElse() {
		Database mockDB = mock(Database.class);
		PostStorage mockPostStorage = new PostStorage(mockDB);
		User author = new User();
		author.setUserName("rperezg4");
		author.setStudentRoleUser(true);
		User user = new User();
		user.setUserName("gharmon1");
		user.setStudentRoleUser(true);
		Post post = new Post(1, PostType.POST, "Help Hw 2", PostCategory.GENERAL, "I don't get hw2 task 7.1", author.getUserName());

		assertEquals("Students cannot delete someone else's post", mockPostStorage.deletePost(post, user));
	}
}
