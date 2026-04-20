package guiReportPosts;

import entityClasses.PostReport;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * JavaFX view for moderation of reported posts.
 *
 * <p>This view builds the moderation interface, enforces access restrictions at
 * entry point level, and delegates actions to the controller.</p>
 *
 * @author Diogo Moscato
 * @version 1.0
 * @since 17 April 2026
 */
public class ViewReportPosts {
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label();
    protected static ListView<PostReport> reportsList = new ListView<>();
    protected static Button button_DeletePost = new Button("Delete Selected Post");
    protected static Button button_Return = new Button("Return");
    protected static Alert reportActionError = new Alert(AlertType.INFORMATION);

    private static ViewReportPosts theView;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    public static Scene theReportPostsScene;

    /**
     * Displays the reported-posts moderation page for the provided user and stage.
     *
     * @param ps stage where this scene will be shown
     * @param user currently authenticated user
     */
    public static void displayReportPosts(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (!ControllerReportPosts.canViewReports(user)) {
            reportActionError.setTitle("Access denied");
            reportActionError.setHeaderText(null);
            reportActionError.setContentText("Only Staff or Admin can view reported posts");
            reportActionError.showAndWait();

            ControllerReportPosts.performReturn();
            return;
        }

        if (theView == null) {
            theView = new ViewReportPosts();
        }

        ControllerReportPosts.repaintTheWindow();
    }

    /**
     * Initializes static UI widgets and event handlers for the reported-posts page.
     */
    private ViewReportPosts() {
        theRootPane = new Pane();
        theReportPostsScene = new Scene(theRootPane, width, height);

        label_PageTitle.setText("Reported Posts");
        setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 5);

        reportsList.setLayoutX(20);
        reportsList.setLayoutY(70);
        reportsList.setPrefWidth(760);
        reportsList.setPrefHeight(440);
        reportsList.setBackground(Background.EMPTY);

        reportsList.setCellFactory(_ -> new ListCell<PostReport>() {
            @Override
            protected void updateItem(PostReport item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label contentLabel = new Label("Post #" + item.getPostId() + " | Reason: " + item.getReason());
                contentLabel.setWrapText(true);
                contentLabel.setMaxWidth(680);

                Label metaLabel = new Label("Reported by " + item.getReporterUsername() + " on "
                        + item.getCreatedAt().toLocalDate());

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getChildren().addAll(contentLabel, spacer, metaLabel);

                setGraphic(row);
                setText(null);
            }
        });

        setupButtonUI(button_DeletePost, 220, Pos.CENTER, 130, 530);
        button_DeletePost.setOnAction(_ -> ControllerReportPosts.performDeleteSelectedPost());

        setupButtonUI(button_Return, 220, Pos.CENTER, 450, 530);
        button_Return.setOnAction(_ -> ControllerReportPosts.performReturn());

        String css = getClass().getResource("/application.css").toExternalForm();
        theReportPostsScene.getStylesheets().add(css);
        label_PageTitle.getStyleClass().add("title");

        DialogPane errorPane = reportActionError.getDialogPane();
        errorPane.getStylesheets().add(css);

        theRootPane.getChildren().addAll(label_PageTitle, reportsList, button_DeletePost, button_Return);
        theStage.setScene(theReportPostsScene);
        theStage.show();
    }

    /**
     * Applies common label layout configuration.
     *
     * @param l label to configure
     * @param w minimum width
     * @param p alignment value
     * @param x horizontal layout position
     * @param y vertical layout position
     */
    private static void setupLabelUI(Label l, double w, Pos p, double x, double y) {
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * Applies common button layout configuration.
     *
     * @param b button to configure
     * @param w minimum width
     * @param p alignment value
     * @param x horizontal layout position
     * @param y vertical layout position
     */
    private static void setupButtonUI(Button b, double w, Pos p, double x, double y) {
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
