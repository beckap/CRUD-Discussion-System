package entityClasses;

import java.time.LocalDateTime;

/**
 * Represents a report submitted for a discussion post.
 */
public class PostReport {
    private final long reportId;
    private final long postId;
    private final String reason;
    private final String reporterUsername;
    private final LocalDateTime createdAt;

    public PostReport(long reportId, long postId, String reason, String reporterUsername, LocalDateTime createdAt) {
        this.reportId = reportId;
        this.postId = postId;
        this.reason = reason;
        this.reporterUsername = reporterUsername;
        this.createdAt = createdAt;
    }

    public long getReportId() {
        return reportId;
    }

    public long getPostId() {
        return postId;
    }

    public String getReason() {
        return reason;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
