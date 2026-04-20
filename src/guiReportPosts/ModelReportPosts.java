package guiReportPosts;

import java.util.List;

import entityClasses.PostReport;

/**
 * Provides data loading support for the Reported Posts view.
 *
 * <p>This model refreshes the report list displayed in the moderation screen
 * using the current report records from storage.</p>
 *
 * @author Diogo Moscato
 * @version 1.0
 * @since 17 April 2026
 */
public class ModelReportPosts {
    /**
     * Utility class constructor kept private to prevent instantiation.
     */
    private ModelReportPosts() {
    }

    /**
     * Reloads the list of reports shown in the view from persistent storage.
     */
    protected static void refreshReportsList() {
        ViewReportPosts.reportsList.getItems().clear();
        List<PostReport> reports = ControllerReportPosts.postStorage.getAllPostReports();
        ViewReportPosts.reportsList.getItems().addAll(reports);
    }
}
