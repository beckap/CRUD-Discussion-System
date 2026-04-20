package guiReportPosts;

import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.PostReport;
import entityClasses.PostStorage;
import entityClasses.User;

/**
 * Coordinates interactions for the Reported Posts moderation page.
 *
 * <p>This controller validates access to moderation actions, refreshes the page state,
 * handles post deletion from selected reports, and routes users back to their active
 * home screen.</p>
 *
 * @author Diogo Moscato
 * @version 1.0
 * @since 17 April 2026
 */
public class ControllerReportPosts {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static PostStorage postStorage = new PostStorage(theDatabase);

    /**
     * Rebuilds and displays the moderation window based on the current user permissions.
     */
    protected static void repaintTheWindow() {
        ViewReportPosts.theRootPane.getChildren().clear();
        ModelReportPosts.refreshReportsList();

        boolean canModerate = canViewReports(ViewReportPosts.theUser);
        ViewReportPosts.button_DeletePost.setVisible(canModerate);
        ViewReportPosts.button_DeletePost.setManaged(canModerate);

        if (canModerate) {
            ViewReportPosts.theRootPane.getChildren().addAll(
                ViewReportPosts.label_PageTitle,
                ViewReportPosts.reportsList,
                ViewReportPosts.button_DeletePost,
                ViewReportPosts.button_Return
            );
        } else {
            ViewReportPosts.theRootPane.getChildren().addAll(
                ViewReportPosts.label_PageTitle,
                ViewReportPosts.reportsList,
                ViewReportPosts.button_Return
            );
        }

        ViewReportPosts.theStage.setTitle("Reported Posts");
        ViewReportPosts.theStage.setScene(ViewReportPosts.theReportPostsScene);
        ViewReportPosts.theStage.show();
    }

    /**
     * Determines whether a user has permission to view and moderate reported posts.
     *
     * @param user user whose roles are evaluated
     * @return {@code true} when user is Admin or Staff; otherwise {@code false}
     */
    protected static boolean canViewReports(User user) {
        return user != null && (user.getAdminRole() || user.getNewStaffRole());
    }

    /**
     * Deletes the post associated with the currently selected report, when permitted.
     *
     * <p>If access is denied, no report is selected, the target post cannot be found,
     * or deletion fails, the method shows an appropriate alert and leaves state unchanged.</p>
     */
    protected static void performDeleteSelectedPost() {
        if (!canViewReports(ViewReportPosts.theUser)) {
            ViewReportPosts.reportActionError.setTitle("Access denied");
            ViewReportPosts.reportActionError.setHeaderText(null);
            ViewReportPosts.reportActionError.setContentText("Only Staff or Admin can delete reported posts");
            ViewReportPosts.reportActionError.showAndWait();
            return;
        }

        PostReport selectedReport = ViewReportPosts.reportsList.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            ViewReportPosts.reportActionError.setTitle("No selection");
            ViewReportPosts.reportActionError.setHeaderText(null);
            ViewReportPosts.reportActionError.setContentText("Please select a report first");
            ViewReportPosts.reportActionError.showAndWait();
            return;
        }

        Post targetPost = findPostById(selectedReport.getPostId());
        if (targetPost == null) {
            ViewReportPosts.reportActionError.setTitle("Post not found");
            ViewReportPosts.reportActionError.setHeaderText(null);
            ViewReportPosts.reportActionError.setContentText(
                "The reported post could not be found in the current post list"
            );
            ViewReportPosts.reportActionError.showAndWait();
            return;
        }

        String deleteResult = postStorage.deletePost(targetPost, ViewReportPosts.theUser);
        if (!deleteResult.isEmpty()) {
            ViewReportPosts.reportActionError.setTitle("Delete failed");
            ViewReportPosts.reportActionError.setHeaderText(null);
            ViewReportPosts.reportActionError.setContentText(deleteResult);
            ViewReportPosts.reportActionError.showAndWait();
            return;
        }

        ViewReportPosts.reportsList.getItems().remove(selectedReport);
        ViewReportPosts.reportActionError.setTitle("Post deleted");
        ViewReportPosts.reportActionError.setHeaderText(null);
        ViewReportPosts.reportActionError.setContentText("The selected post was deleted successfully");
        ViewReportPosts.reportActionError.showAndWait();
    }

    /**
     * Returns the user to the correct previous screen based on the active home page index.
     */
    protected static void performReturn() {
        int activeHomePage = applicationMain.FoundationsMain.activeHomePage;

        switch (activeHomePage) {
            case 1:
                guiAdminHome.ViewAdminHome.displayAdminHome(ViewReportPosts.theStage, ViewReportPosts.theUser);
                break;
            case 2:
                guiStaff.ViewStaffHome.displayStaffHome(ViewReportPosts.theStage, ViewReportPosts.theUser);
                break;
            case 3:
                guiStudent.ViewStudentHome.displayStudentHome(ViewReportPosts.theStage, ViewReportPosts.theUser);
                break;
            default:
                guiDiscussionSystem.ViewDiscussionSystem.displayDiscussionSystem(
                    ViewReportPosts.theStage,
                    ViewReportPosts.theUser
                );
                break;
        }
    }

    /**
     * Locates a post by its unique identifier in the current post collection.
     *
     * @param postId unique post id referenced by a report
     * @return matching post, or {@code null} when no post has that id
     */
    private static Post findPostById(long postId) {
        List<Post> allPosts = postStorage.getAllPosts();
        for (Post post : allPosts) {
            if (post.getPostId() == postId) {
                return post;
            }
        }
        return null;
    }
}
