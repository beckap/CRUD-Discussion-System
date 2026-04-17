package guiDiscussionSystem;

import entityClasses.Post;
import entityClasses.PostCategory;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/*******
 * <p>
 * Title: ViewDiscussionSystem Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based page for the discussion system of the application.
 * </p>
 * 
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-02-14 Initial version
 * 
 */
public class ViewDiscussionSystem {
/********************************************************************************************
	
	Attributes
	
	*/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page, whose account
	// is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	
	protected static HBox filterHbox = new HBox(10);
	protected static HBox searchHbox = new HBox(10);
	protected static Label label_filter = new Label();
	protected static Label label_search = new Label();
	protected static Button filterBy = new Button("Filter");
	protected static TextField searchField = new TextField();
	protected static CheckBox discussion = new CheckBox("Post");
	protected static CheckBox question = new CheckBox("Question");
	protected static CheckBox general = new CheckBox("General");
	protected static CheckBox homework = new CheckBox("Homework");
	protected static CheckBox exams = new CheckBox("Exams");
	protected static CheckBox lectures = new CheckBox("Lectures");
	protected static CheckBox onlyMyPosts = new CheckBox("Only show my posts");

	// Scroll Pane to scroll through the posts
	protected static ScrollPane scrollPane_Posts = new ScrollPane();

	protected static ListView<Post> postsList = new ListView<>();
	
	protected static Button createPost = new Button("Create Post");
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator2 = new Line(20, 95, width - 20, 95);
	
	protected static Button button_Return = new Button("Return");

	// These attributes are used to configure the page and populate it with this
	// user's information
	private static ViewDiscussionSystem theView; // Used to determine if instantiation of the class
												// is needed
	
	protected static Alert postError = new Alert(AlertType.INFORMATION);

	protected static Stage theStage; // The Stage that JavaFX has established for us
	protected static Pane theRootPane; // The Pane that holds all the GUI widgets
	protected static User theUser; // The current user of the application

	public static Scene theDiscussionSystemScene;

	/*-*******************************************************************************************
	
	Constructors
	
	*/

	/**********
	 * <p>
	 * Method: displayDiscussionSystem(Stage ps, User user)
	 * </p>
	 * 
	 * <p>
	 * Description: This method is the single entry point from outside this package
	 * to cause the DiscussionSystem page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup. If not, it instantiates the
	 * class, initializes all the static aspects of the GUI widgets (e.g., location
	 * on the page, font, size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change
	 * based on the user and the system's current state. It then sets the Scene onto
	 * the stage, and makes it visible to the user.
	 * 
	 * @param ps   specifies the JavaFX Stage to be used for this GUI and it's
	 *             methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayDiscussionSystem(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null)
			theView = new ViewDiscussionSystem();
		
		ControllerDiscussionSystem.repaintTheWindow();
	}

	
	/**********
	 * <p> Method: ViewDiscussionSystem()
	 * </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayDiscussionSystem method.
	 * </p>
	 * 
	 */
	public ViewDiscussionSystem() {
		theRootPane = new Pane();
		theDiscussionSystemScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		// Set up page title
		label_PageTitle.setText("Discussion posts");
		setupLabelUI(label_PageTitle, width, Pos.CENTER, 0, 5);
		setupButtonUI(button_Return, 150, Pos.CENTER, 15, 15);
		button_Return.setOnAction((_) -> {
			ControllerDiscussionSystem.performReturn();
		});
		label_filter.setText("Filter by: ");
		label_filter.setStyle("-fx-font-size: 18px");
		filterBy.setMaxWidth(110);
		filterBy.setOnAction(_ -> {
			ModelDiscussionSystem.refreshFilteredPosts();
		});
		label_search.setText("Search:");
		label_search.setStyle("-fx-font-size: 18px");
		searchField.setPromptText("Title, content, or author");
		searchField.getStyleClass().add("text-fields");
		searchField.setMinWidth(220);
		searchField.setMaxWidth(220);
		searchField.setPrefWidth(220);
		searchField.textProperty().addListener((_, _, _) -> {
			ModelDiscussionSystem.refreshSearchedPosts();
		});
		// Top bar combines post filters and live keyword search.
		filterHbox.getChildren().addAll(label_filter, discussion, question,
				general, homework, exams, lectures, filterBy);
		filterHbox.setSpacing(15);
		filterHbox.setLayoutX(10);
		filterHbox.setLayoutY(70);
		searchHbox.getChildren().addAll(label_search, searchField, onlyMyPosts);
		searchHbox.setSpacing(10);
		searchHbox.setLayoutX(100);
		searchHbox.setLayoutY(106);
		
		// Set up GUI Area 2
		postsList.setBackground(Background.EMPTY);
		// Render each post with a compact preview: title, category, author, and contextual controls.
				ViewDiscussionSystem.postsList.setCellFactory(_ -> new ListCell<Post>() {
					@Override
					protected void updateItem(Post item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
							setGraphic(null);
							setBackground(Background.EMPTY);
						} else {
							PostCategory category = item.getCategory();
							String stringCategory = switch (category) {
								case LECTURES -> "Lectures\t-\t";
								case EXAMS -> "Exams\t\t-\t";
								case HOMEWORK -> "Homework\t-\t";
								default -> "General\t-\t";
							};

							int totalReplies = ControllerDiscussionSystem.replyStorage.getNumRepliesByPostId(item.getPostId());
							int unreadReplies = ControllerDiscussionSystem.replyStorage.getNumUnreadRepliesByPostId(item.getPostId());
							
							String replyLabel = totalReplies + " replies";
							if (unreadReplies > 0 && item.getAuthorUsername().equals(theUser.getUserName())) {
								replyLabel += " (" + unreadReplies + " unread)";
							}

							String postTextContent = item.getTitle() + "\n" + stringCategory +
									item.getAuthorUsername() + "  |  " + replyLabel + "\n";

							// Create the main container for the cell
							HBox cellLayout = new HBox(10);
							cellLayout.setAlignment(Pos.CENTER_LEFT);

							Label textLabel = new Label(postTextContent);

							// Spacer to push controls to the far right
							Region spacer = new Region();
							HBox.setHgrow(spacer, Priority.ALWAYS);

							cellLayout.getChildren().addAll(textLabel, spacer);

							// Check if the current user has the privilege to hide this specific post
							if (ControllerDiscussionSystem.canHidePost(theUser, item.getAuthorUsername())) {
								HBox hideContainer = new HBox(5);
								hideContainer.setAlignment(Pos.CENTER);

								CheckBox hideCheckBox = new CheckBox("Hide");
								
								// If visibilityLevel > 0, the post is currently hidden
								hideCheckBox.setSelected(item.getVisibilityLevel() > 0);
								
								// Fire an event to the Controller when checked/unchecked
								hideCheckBox.setOnAction(_ -> {
									ControllerDiscussionSystem.togglePostVisibility(item, hideCheckBox.isSelected());
								});
								
								hideContainer.getChildren().addAll(hideCheckBox);
								cellLayout.getChildren().add(hideContainer);
							}

							setGraphic(cellLayout);
							setText(null); // Clear default text
							setBackground(Background.EMPTY);
						}
					}
				});
		scrollPane_Posts.setLayoutX(10);
		scrollPane_Posts.setLayoutY(140);
		scrollPane_Posts.setPrefHeight(370);
		scrollPane_Posts.setFitToWidth(true);
		scrollPane_Posts.setPrefWidth(700);
		scrollPane_Posts.setContent(postsList);
		scrollPane_Posts.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane_Posts.setStyle("-fx-background-color: transparent; "
				+ "-fx-background-insets: 0; -fx-padding: 0;");
		
		
		setupButtonUI(createPost, 150, Pos.CENTER, 330, 550);
		createPost.setOnAction((_) -> {
			ControllerDiscussionSystem.performNewPost();
		});
		
		String css = getClass().getResource("/application.css").toExternalForm();
		theDiscussionSystemScene.getStylesheets().add(css);
		label_PageTitle.getStyleClass().add("title");
		
		DialogPane error1 = postError.getDialogPane();
		error1.getStylesheets().add(css);
		
		theRootPane.getChildren().addAll(label_PageTitle, filterHbox, searchHbox, scrollPane_Posts, createPost, button_Return);
		theStage.setScene(theDiscussionSystemScene);
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