package guiDiscussionSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.Post;
import entityClasses.PostCategory;
import entityClasses.PostStorage;
import entityClasses.PostType;
import entityClasses.User;

/**
 * <p><b>Class:</b> ControllerDiscussionSystemDefaultTypeTest</p>
 *
 * <p><b>Purpose:</b></p>
 * <p>
 * Verifies the default post-type behavior and the high-risk override path
 * where a user changes the type before submission.
 * </p>
 *
 * <p><b>TP3 Mapping:</b></p>
 * <p>
 * Functional requirement 4.1 coverage for:
 * - submit without explicit type change uses default;
 * - submit with explicit type change saves the new value.
 * </p>
 */
class ControllerDiscussionSystemDefaultTypeTest {

	/**
	 * Test 1: When the user explicitly changes the type, the changed value
	 * must be persisted instead of the default.
	 */
	@Test
	@DisplayName("Test 1: Changed post type is persisted instead of default")
	void testChangedPostTypeIsSaved() {
		// Arrange a fake database and storage layer used by post creation.
		FakeDatabase fakeDb = new FakeDatabase();
		PostStorage postStorage = new PostStorage(fakeDb);
		User author = new User();
		author.setUserName("studentA");

		// Simulate default selection plus a user override before submit.
		PostType resolvedType = PostStorage.resolvePostTypeForSubmission(PostType.POST,
				PostType.QUESTION);

		// Act by creating a post with the resolved type.
		String error = postStorage.createPost("Title", "Body", author, PostCategory.GENERAL, resolvedType);

		// Assert create succeeded and the fake database received QUESTION.
		assertEquals("", error);
		assertEquals(PostType.QUESTION, fakeDb.lastRegisteredPost.getTypeOfPost());
	}

	/**
	 * Test 2: If no explicit user choice exists, the default type is used.
	 */
	@Test
	@DisplayName("Test 2: Default post type is used when no explicit choice exists")
	void testDefaultPostTypeUsedWhenNoSelection() {
		// No user selection should safely fall back to the configured default.
		PostType resolvedType = PostStorage.resolvePostTypeForSubmission(PostType.POST, null);

		assertEquals(PostType.POST, resolvedType);
	}

	/**
	 * Simple fake database used to capture posts that would be persisted.
	 */
	private static class FakeDatabase extends Database {
		private Post lastRegisteredPost;

		@Override
		public void registerPost(Post post) {
			// Capture the last saved post so tests can validate persisted values.
			this.lastRegisteredPost = post;
		}
	}
}
