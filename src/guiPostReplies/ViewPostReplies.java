package guiPostReplies;

import entityClasses.Post;
import entityClasses.PostStorage;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*******
 * <p><b>Class: </b> ViewPostReplies
 * </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>This class represents the View component of the Post and replies system 
 * in the application. It is responsible for displaying a selected discussion
 * post and all associated replies in a graphical user interface using JavaFX.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * 	<li>Display the selected post and its contents.</li>
 * 	<li>Display all replies associated with the post</li>
 * 	<li>Allows the use to create new replies associated to the post</li>
 * 	<li>Provides a return button to the discussion system</li>
 * </ul>
 * 
 * <p><b>User Stories Supported:</b></p>
 * <ul>
 * 	<li>Students can view a selected post</li>
 * 	<li>Students can view replies to a post</li>
 * 	<li>Students can create replies</li>
 * </ul>
 * 
 * <p><b>Additional notes:</b> This class implements a singleton pattern 
 * to avoid multiple UI instances. It delegates all logic to the Controller 
 * and Model classes.</p>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-02-16 Initial version
 * 
 */
public class ViewPostReplies {

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/**
	 * Label used to display the title of the selected post and its ID number.
	 *
	 * <p><b>Behavior:</b>
	 * Provides the identification of the post.
	 * </p>
	 */
	protected static Label label_PageTitle = new Label();
	
	/**
	 * Label used for post author and date
	 */
	protected static Label labelPostAuthor = new Label();
	
	/**
	 * Label used for post author and date
	 */
	protected static Label labelPostDate = new Label();
	
	/**
	 * HBox to set the post layout
	 */
	protected static HBox postInfoLayout = new HBox(10);
	
	/**
	 * HBox to set the post layout
	 */
	protected static HBox postLayout = new HBox(20);
	
	/**
	 * Label used to display the full content of the selected post.
	 *
	 * <p><b>Behavior:</b>
	 * Provides the context for the replies by showing the full original post.
	 * </p>
	 */
	protected static Label postLabel = new Label();
	
	/**
	 * ScrollPane that contains the post content.
	 *
	 * <p><b>Rationale:</b>
	 * Ensures long posts can be viewed without breaking the layout.
	 * </p>
	 */
	protected static ScrollPane scrollPanePostContent = new ScrollPane();
	
	/**
	 * ListView used to display all replies associated with the selected post.
	 *
	 * <p><b>Rationale:</b>
	 * Each Reply object is rendered as a list item for easy readability.
	 * </p>
	 */
	protected static ListView<Reply> repliesList = new ListView<>();
	
	/**
	 * Button that allows the user to create a new reply.
	 *
	 * <p><b>Behavior:</b>
	 * Triggers the ControllerPostReplies.performNewReply() method to create a reply.
	 * </p>
	 *
	 * <p><b>Rationale:</b> 
	 * Supports the user story "Students can create replies".
	 * </p>
	 */
	protected static Button createReply = new Button("Create Reply");
	
	/**
	 * Button that allows the user to return to the previous page
	 */
	protected static Button button_Return = new Button("Return");

	private static ViewPostReplies theView;
	
	/**
	 * Main layout container organizing all UI elements vertically.
	 * Provides consistent spacing and alignment for the page.
	 * 
	 */
	protected static VBox layout = new VBox();
	
	/**
	 * Alert used to notify the user when replying is not allowed.
	 * 
	 * <p><b>Rationale:</b>
	 * Prevents the user for making invalid actions and improves user feedback.
	 * </p>
	 * 
	 */
	protected static Alert replyError = new Alert(AlertType.INFORMATION);
	
	/**
	 * Alert used to notify the user when deleting is not allowed.
	 * 
	 * <p><b>Rationale:</b>
	 * Prevents the user for making invalid actions and improves user feedback.
	 * </p>
	 * 
	 */
	protected static Alert deleteError = new Alert(AlertType.INFORMATION);
	
	/**
	 * Alert used to notify the user when editing is not allowed.
	 * 
	 * <p><b>Rationale:</b>
	 * Prevents the user for making invalid actions and improves user feedback.
	 * </p>
	 * 
	 */
	protected static Alert editError = new Alert(AlertType.INFORMATION);

	/**
	 * Alert used to notify the user when reporting is not allowed.
	 */
	protected static Alert reportError = new Alert(AlertType.INFORMATION);
	
	
	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current user of the application

	public static Scene thePostRepliesScene;

	/**********
	 * <p>Method: displayPostReplies(Stage ps, User user, Post post, PostStorage storage)
	 * </p>
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * The single entry point from outside this package to display a selected post and its associated
	 * replies.
	 * 
	 * It checks to see if the page has been set up. If not, it instantiates the
	 * view, and then sets up the aspects of the GUI widgets. The code populates the elements based on
	 * the selected post and the system's current user by calling the repaintTheWindow() method from 
	 * the Controller class.
	 * </p>
	 * 
	 *<p><b>Behavior:</b></p>
	 * <ul>
	 *   <li>Stores shared references to stage, user, and selected post</li>
	 *   <li>Initializes the view if not already created (singleton pattern)</li>
	 *   <li>Delegates UI updates to the controller</li>
	 * </ul>
	 * 
	 * 
	 * @param ps      JavaFX stage used to display the GUI
	 * @param user    currently logged-in user
	 * @param post    selected post whose replies are displayed
	 * @param storage storage system used to retrieve post data
	 *
	 */
	public static void displayPostReplies(Stage ps, User user, Post post, PostStorage storage) {
		theStage = ps;
		theUser = user;
		ControllerPostReplies.selected = post;
		ControllerPostReplies.postStorage = storage;

		if (theView == null)
			theView = new ViewPostReplies();
		
		ControllerPostReplies.repaintTheWindow();
		
	}

	
	/**********
	 * <p>Constructor: ViewPostReplies()</p>
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * This method initializes all the elements of the graphical user interface.
	 * </p>
	 * 
	 * <p><b>Responsibilities:</b></p>
	 * <ul>
	 *   <li>Creates and positions all UI components</li>
	 *   <li>Configures layout containers (VBox, HBox, ScrollPane)</li>
	 *   <li>Attaches event handlers to buttons</li>
	 * </ul>
	 *
	 * <p><b>Design Notes:</b></p>
	 * <ul>
	 *   <li>This constructor is called only once (singleton pattern)</li>
	 *   <li>Dynamic data is populated via controller methods</li>
	 * </ul>
	 * 
	 */
	public ViewPostReplies() {
		theRootPane = new Pane();
		thePostRepliesScene = new Scene(theRootPane, width, height);

		// Populates the title with the title and post ID
		// Ensures all posts have consistent formatting throughout the system.
		label_PageTitle.setText(ControllerPostReplies.selected.getTitle() + "	#" + 
				ControllerPostReplies.selected.getPostId());
		label_PageTitle.setStyle("-fx-font-size: 27px;");
		setupLabelUI(label_PageTitle, width, Pos.BASELINE_LEFT, 0, 5); 
		
		labelPostAuthor.setText(ControllerPostReplies.selected.getAuthorUsername());
		labelPostAuthor.setStyle("-fx-font-size: 14px;");
		labelPostDate.setText(ControllerPostReplies.selected.getDate().toLocalDate().toString());
		labelPostDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #808388;");
		
		postInfoLayout.getChildren().addAll(labelPostAuthor, labelPostDate);
		postInfoLayout.setLayoutX(0);
		postInfoLayout.setLayoutY(15);
		// Populates the label with formatted post content retrieved from storage.
		postLabel.setText(ControllerPostReplies.postStorage.displayPost(ControllerPostReplies.selected));
		postLabel.setStyle("-fx-font-size: 14px");
		postLabel.setWrapText(true);
		postLabel.setPrefWidth(500);
		
		// Sets the scrollPane layout and content.
		scrollPanePostContent.setContent(postLayout);
		scrollPanePostContent.setPrefHeight(150);
		scrollPanePostContent.setMaxHeight(150);
		scrollPanePostContent.setFitToWidth(true);
		scrollPanePostContent.setPrefWidth(750);
		
		repliesList.setBackground(Background.EMPTY);
		repliesList.setPrefHeight(330);
		repliesList.setMaxWidth(750);

		// Prevents user from replying to a deleted post.
		// This enforces systems rules and avoids invalid data.
		createReply.setOnAction((_) -> {
			if (ControllerPostReplies.selected.isDeleted()) {
				ViewPostReplies.replyError.setTitle("Reply Error");
				ViewPostReplies.replyError.setHeaderText(null);
				ViewPostReplies.replyError.setContentText("Cannot reply to this post anymore");
				ViewPostReplies.replyError.showAndWait();
				return;
			}
			ControllerPostReplies.performNewReply();
		});
		
		button_Return.setOnAction((_) -> {
			ControllerPostReplies.performReturn();
		});
		
		// Layout container to organize both buttons horizontally.
		HBox buttonLayout = new HBox(60);
		buttonLayout.getChildren().addAll(button_Return, createReply);
		
		label_PageTitle.setMaxWidth(Double.MAX_VALUE);
		button_Return.setMaxWidth(150);
		createReply.setMaxWidth(150);

		layout.getChildren().addAll(label_PageTitle, postInfoLayout, scrollPanePostContent, 
				repliesList, buttonLayout);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.TOP_CENTER);
		layout.setLayoutX(0);
		layout.setLayoutY(0);
		
		// Sets the style sheets to ensure proper design styling for each UI component.
		String css = getClass().getResource("/application.css").toExternalForm();
		thePostRepliesScene.getStylesheets().add(css);
		label_PageTitle.getStyleClass().add("title");
		
		// Sets the style sheets to Alert dialogs
		DialogPane error1 = replyError.getDialogPane();
		error1.getStylesheets().add(css);
		DialogPane error2 = deleteError.getDialogPane();
		error2.getStylesheets().add(css);
		DialogPane error3 = editError.getDialogPane();
		error3.getStylesheets().add(css);
		DialogPane error4 = reportError.getDialogPane();
		error4.getStylesheets().add(css);
		
		// Adds the main layout to the Stage so it is displayed to user.
		theRootPane.getChildren().addAll(layout);
		theStage.setScene(thePostRepliesScene);
		theStage.show();
		
	}

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l  The Label object to be initialized
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, double w, Pos p, double x, double y) {
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b  The Button object to be initialized
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, double w, Pos p, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
