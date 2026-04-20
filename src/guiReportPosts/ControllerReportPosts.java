package guiReportPosts;

import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.PostReport;
import entityClasses.PostStorage;
import entityClasses.User;

/**
 * Controller actions for the Reported Posts moderation page.
 */
public class ControllerReportPosts {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static PostStorage postStorage = new PostStorage(theDatabase);

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

    protected static boolean canViewReports(User user) {
        return user != null && (user.getAdminRole() || user.getNewStaffRole());
    }

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
