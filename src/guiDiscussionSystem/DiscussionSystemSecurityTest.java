package guiDiscussionSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * <p><b>Class:</b> DiscussionSystemSecurityTest
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <p>
 * Covers critical authorization and boundary behavior for discussion posts
 * and replies, including invalid input handling and message ID limits.
 * </p>
 */
public class DiscussionSystemSecurityTest {

    /**
     * Test #1: Author can edit own post.
     */
    @Test
    @DisplayName("DS.1 Author can edit own post")
    void testAuthorCanEditOwnPost() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        PostStorage postStorage = new PostStorage(fakeDb);

        User author = makeUser("studentA", true, false, false);
        Post ownPost = new Post(1L, PostType.POST, "Original", PostCategory.GENERAL, "Body", "studentA");

        String result = postStorage.editPost(ownPost, author, "Changed", "Changed body");

        assertEquals("", result);
        assertTrue(fakeDb.updatePostCalled);
    }

    /**
     * Test #2: Student cannot edit another user's post.
     */
    @Test
    @DisplayName("DS.2 Student cannot edit another user's post")
    void testStudentCannotEditAnotherUsersPost() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        PostStorage postStorage = new PostStorage(fakeDb);

        User student = makeUser("studentA", true, false, false);
        Post otherPost = new Post(2L, PostType.POST, "Original", PostCategory.GENERAL, "Body", "studentB");

        String result = postStorage.editPost(otherPost, student, "Changed", "Changed body");

        assertEquals("You cannot edit someone else's post", result);
        assertFalse(fakeDb.updatePostCalled);
    }

    /**
     * Test #3: Null post is handled safely.
     */
    @Test
    @DisplayName("DS.3 Null post handled without crash")
    void testNullPostHandledGracefully() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        PostStorage postStorage = new PostStorage(fakeDb);

        User student = makeUser("studentA", true, false, false);
        String result = postStorage.editPost(null, student, "Changed", "Changed body");

        assertEquals("Post does not exist", result);
    }

    /**
     * Test #4: Boundary IDs outside valid range return no replies.
     */
    @Test
    @DisplayName("DS.4 Boundary post IDs handled safely")
    void testBoundaryPostIdsHandledSafely() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        ReplyStorage replyStorage = new ReplyStorage(fakeDb);

        Reply r1 = new Reply(1L, 100L, "A", "author");
        Reply r2 = new Reply(2L, 101L, "B", "author");
        fakeDb.replies = Arrays.asList(r1, r2);

        replyStorage.populateAllReplies();

        List<Reply> negative = replyStorage.getRepliesByPostId(-1L);
        List<Reply> huge = replyStorage.getRepliesByPostId(Long.MAX_VALUE);

        assertTrue(negative.isEmpty());
        assertTrue(huge.isEmpty());
    }

    /**
     * Test #5: Author can edit own reply.
     */
    @Test
    @DisplayName("DS.5 Author can edit own reply")
    void testAuthorCanEditOwnReply() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        ReplyStorage replyStorage = new ReplyStorage(fakeDb);

        User author = makeUser("studentA", true, false, false);
        Reply ownReply = new Reply(10L, 1L, "Original reply", "studentA");

        String result = replyStorage.editReply(ownReply, author, "Changed reply");

        assertEquals("", result);
        assertTrue(fakeDb.updateReplyCalled);
    }

    /**
     * Test #6: Student cannot edit another user's reply.
     */
    @Test
    @DisplayName("DS.6 Student cannot edit another user's reply")
    void testStudentCannotEditAnotherUsersReply() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        ReplyStorage replyStorage = new ReplyStorage(fakeDb);

        User student = makeUser("studentA", true, false, false);
        Reply otherReply = new Reply(10L, 1L, "Original reply", "studentB");

        String result = replyStorage.editReply(otherReply, student, "Changed reply");

        assertEquals("You cannot edit someone else's reply", result);
        assertFalse(fakeDb.updateReplyCalled);
    }

    /**
     * Test #7: Null reply is handled safely.
     */
    @Test
    @DisplayName("DS.7 Null reply handled without crash")
    void testNullReplyHandledGracefully() {
        FakeDiscussionDatabase fakeDb = new FakeDiscussionDatabase();
        ReplyStorage replyStorage = new ReplyStorage(fakeDb);

        User student = makeUser("studentA", true, false, false);
        String result = replyStorage.editReply(null, student, "Changed reply");

        assertEquals("Reply does not exist", result);
    }

    private static User makeUser(String username, boolean studentRole, boolean staffRole, boolean adminRole) {
        User user = new User();
        user.setUserName(username);
        user.setStudentRoleUser(studentRole);
        user.setStaffRoleUser(staffRole);
        user.setAdminRole(adminRole);
        return user;
    }

    private static class FakeDiscussionDatabase extends Database {
        private List<Reply> replies = Collections.emptyList();
        private boolean updatePostCalled = false;
        private boolean updateReplyCalled = false;

        @Override
        public List<Reply> getRepliesList() {
            return replies;
        }

        @Override
        public void updatePost(Long postId, String title, String content) {
            updatePostCalled = true;
        }

        @Override
        public void updateReply(Long replyId, String content) {
            updateReplyCalled = true;
        }
    }
}
