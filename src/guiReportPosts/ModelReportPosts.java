package guiReportPosts;

import java.util.List;

import entityClasses.PostReport;

/**
 * Model for mapping report data into the list view.
 */
public class ModelReportPosts {
    private ModelReportPosts() {
    }

    protected static void refreshReportsList() {
        ViewReportPosts.reportsList.getItems().clear();
        List<PostReport> reports = ControllerReportPosts.postStorage.getAllPostReports();
        ViewReportPosts.reportsList.getItems().addAll(reports);
    }
}
