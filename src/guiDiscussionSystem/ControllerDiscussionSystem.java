package guiDiscussionSystem;

import java.util.ArrayList;
import java.util.List;
import database.Database;
import entityClasses.Post;
import entityClasses.PostCategory;
import entityClasses.PostStorage;
import entityClasses.PostType;
import entityClasses.ReplyStorage;
import entityClasses.User;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*******
 * <p>
 * Title: ControllerDiscussionSystem Class.
 * </p>
 * 
 * <p>
 * Description:This class provides the controller actions basic on 
 * the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * The class has been written assuming that the View or the Model are the only
 * class methods that can invoke these methods. This is why each has been
 * declared at "protected".
 * </p>
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-02-14 Initial version
 * 
 */
public class ControllerDiscussionSystem {
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	// Access layers used for creating and querying posts and replies.
	protected static PostStorage postStorage = new PostStorage(theDatabase);
	protected static ReplyStorage replyStorage = new ReplyStorage(theDatabase);
	// Post currently selected in the list view.
	protected static Post selected = null;
	// Base list used by search (all posts or the latest filtered result).
	protected static List<Post> basePosts = new ArrayList<>();

	
	/**********
	 * <p>
	 * Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper
	 * set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDiscussionSystem.theRootPane.getChildren().clear();
		
		replyStorage.populateAllReplies();
		ModelDiscussionSystem.refreshPostList();
		// Define the view to show the user
		ViewDiscussionSystem.theRootPane.getChildren().addAll(ViewDiscussionSystem.label_PageTitle,
				ViewDiscussionSystem.filterHbox, ViewDiscussionSystem.searchHbox,
				ViewDiscussionSystem.scrollPane_Posts, ViewDiscussionSystem.createPost, 
				ViewDiscussionSystem.button_Return);

		// Set the title for the window
		ViewDiscussionSystem.theStage.setTitle("Discussion System");
		ViewDiscussionSystem.theStage.setScene(ViewDiscussionSystem.theDiscussionSystemScene);
		ViewDiscussionSystem.theStage.show();
	}
	
	/**********
	 * <p>
	 * Method: performNewPost() </p>
	 * 
	 * <p> Description: This method opens a dialog window so the user can create a new post. 
	 * 
	 * The method calls the postStorage create method to validate inputs and shows a specific 
	 * error message if the user did not enter something correctly. 
	 * 
	 * To create a post, the user needs to select a type and category, and they have to
	 * enter a title and the content.
	 * </p>
	 * 
	 */
	protected static void performNewPost() {
		Dialog<String> postDialog = new Dialog<>();
		final ToggleGroup typeToggle = new ToggleGroup();
		RadioButton discussionButton = new RadioButton("Post");
		RadioButton questionButton = new RadioButton("Question");
		final ToggleGroup categoryToggle = new ToggleGroup();
		RadioButton generalButton = new RadioButton("General");
		RadioButton homeworkButton = new RadioButton("Homework");
		RadioButton examsButton = new RadioButton("Exams");
		RadioButton lecturesButton = new RadioButton("Lectures");
		
		ButtonType createPost = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
		postDialog.getDialogPane().getButtonTypes().addAll(createPost, ButtonType.CANCEL);
		
		discussionButton.setUserData(PostType.POST);
		discussionButton.setToggleGroup(typeToggle);
		questionButton.setUserData(PostType.QUESTION);
		questionButton.setToggleGroup(typeToggle);
		
		generalButton.setUserData(PostCategory.GENERAL);
		generalButton.setToggleGroup(categoryToggle);
		homeworkButton.setUserData(PostCategory.HOMEWORK);
		homeworkButton.setToggleGroup(categoryToggle);
		examsButton.setUserData(PostCategory.EXAMS);
		examsButton.setToggleGroup(categoryToggle);
		lecturesButton.setUserData(PostCategory.LECTURES);
		lecturesButton.setToggleGroup(categoryToggle);
		
		VBox layout = new VBox(10);
		HBox typeLayout = new HBox(10);
		HBox categoryLayout = new HBox(10);
		
		TextField title = new TextField();
		title.setMinWidth(450);
		TextArea content = new TextArea();
		content.setWrapText(true);
		content.setMinWidth(450);
		content.setPrefRowCount(8);
		
		// character count label with default value of 0
		Label label_characterCount = new Label("Characters: 0 / 10");
		label_characterCount.setStyle("-fx-text-fill: white;");
		
		// real-time update feature of the character count
		int maxAllowedChars = 10;
		content.textProperty().addListener((obsText, oldText, newText) -> {
			
			int charLength = newText.length();
			
			// stops the user from typing if max char limit reached
			if(charLength > maxAllowedChars) {
				content.setText(oldText);
				return;
			} 
			
			label_characterCount.setText("Characters: " + charLength + " / " + maxAllowedChars);
			
			// turns the char counter text red if max limit reached, then back to white if under limit
			if(charLength == maxAllowedChars) {
				label_characterCount.setStyle("-fx-text-fill: red;");
			} else {
				label_characterCount.setStyle("-fx-text-fill: white;");
			}
		});
		
		typeLayout.getChildren().addAll(discussionButton,questionButton);
		categoryLayout.getChildren().addAll(generalButton,homeworkButton,
				examsButton,lecturesButton);
		
		Label typeLabel = new Label("Type: ");
		Label titleLabel = new Label("Title: ");
		Label categoryLabel = new Label("Category: ");
		Label contentLabel = new Label("Content: ");
		
		categoryToggle.selectToggle(generalButton);
		
		layout.getChildren().addAll(typeLabel,typeLayout, 
				titleLabel, title, categoryLabel, categoryLayout, contentLabel, content, label_characterCount);
		
		postDialog.getDialogPane().setContent(layout);
		postDialog.getDialogPane().setPrefHeight(500);
		postDialog.getDialogPane().setPrefWidth(500);
		
		Button button = (Button) postDialog.getDialogPane().lookupButton(createPost);
		
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if(typeToggle.getSelectedToggle() == null ||
	                categoryToggle.getSelectedToggle() == null) {
				ViewDiscussionSystem.postError.setHeaderText(null);
                ViewDiscussionSystem.postError.setContentText("Please select type and category.");
                ViewDiscussionSystem.postError.showAndWait();
                event.consume();
                return;
            }
			
			PostType type = (PostType) typeToggle.getSelectedToggle().getUserData();
			
			PostCategory category = (PostCategory) categoryToggle.getSelectedToggle().getUserData();
			
			String errorMessage = postStorage.createPost(title.getText(), content.getText(),
					ViewDiscussionSystem.theUser, category, type);
			
			if (!errorMessage.isEmpty()) {
				ViewDiscussionSystem.postError.setTitle("Post Error");
				ViewDiscussionSystem.postError.setHeaderText(null);
				ViewDiscussionSystem.postError.setContentText(errorMessage);
				ViewDiscussionSystem.postError.showAndWait();
				event.consume();
				return;
			}
		});
		
		String css = ViewDiscussionSystem.class.getResource("/application.css").toExternalForm();
		DialogPane newPostDialog = postDialog.getDialogPane();
		newPostDialog.getStylesheets().add(css);
		
		postDialog.showAndWait();
		repaintTheWindow();
		
		title.clear();
		content.clear();
		typeToggle.selectToggle(null);
		categoryToggle.selectToggle(null);
	}

	/**********
	 * <p>
	 * Method: setupPostSelection() </p>
	 *
	 * <p>
	 * Description: Registers a click handler for the posts list. When the user
	 * selects a post, the replies page is opened for that specific post.
	 * </p>
	 */
	protected static void setupPostSelection() {
		ViewDiscussionSystem.postsList.setOnMouseClicked(_ -> {
			selected = ViewDiscussionSystem.postsList.getSelectionModel().getSelectedItem();
			if (selected != null) {
				replyStorage.markRepliesAsRead(selected, ViewDiscussionSystem.theUser);
				ViewDiscussionSystem.postsList.refresh();
				guiPostReplies.ViewPostReplies.displayPostReplies(ViewDiscussionSystem.theStage,
						ViewDiscussionSystem.theUser, selected, postStorage);
			}
		});
	}
	
	/**********
	 * <p> Method: hasHigherPrivilege(User currentUser, String authorUsername) </p>
	 * <p> Description: Checks if the current user has strictly higher privilege than the author. </p>
	 */
	protected static boolean hasHigherPrivilege(User currentUser, String authorUsername) {
		int yourPrivilege = currentUser.getAdminRole() ? 2 : (currentUser.getNewStaffRole() ? 1 : 0);
		int authorPrivilege = theDatabase.getUserPrivilegeLevel(authorUsername);
		return yourPrivilege > authorPrivilege;
	}

	/**********
	 * <p> Method: canHidePost(User currentUser, String authorUsername) </p>
	 * <p> Description: Checks your privilege level compared to the author.
	 * Staff+ can hide posts of lesser-privileged users. </p>
	 */
	protected static boolean canHidePost(User currentUser, String authorUsername) {
		
		// Students can't hide
		if (currentUser.getNewStudentRole()) return false;
		
		// Can users hide their own posts? Yes, they can!
		if (currentUser.getUserName().equals(authorUsername)) return true;
		
		return hasHigherPrivilege(currentUser, authorUsername);
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user home page. </p>
	 * 
	 */
	protected static void performReturn() {
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewDiscussionSystem.theStage, ViewDiscussionSystem.theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(ViewDiscussionSystem.theStage, ViewDiscussionSystem.theUser);
			break;
		case 3:
			guiStudent.ViewStudentHome.displayStudentHome(ViewDiscussionSystem.theStage, ViewDiscussionSystem.theUser);
			break;
		default:
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal
	 * login page where existing users can log in or potential new users with a
	 * invitation code can start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDiscussionSystem.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program. It leaves
	 * the database in a state where the normal login page will be displayed when
	 * the application is restarted. </p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}

	/**********
	 * <p> Method: togglePostVisibility(Post post, boolean hide) </p>
	 * <p> Description: Toggles post visibility between 0 and the user's privilege level </p>
	 */
	protected static void togglePostVisibility(Post post, boolean shouldHide) {
		// Check your privilege
		int yourPrivilege = ViewDiscussionSystem.theUser.getAdminRole() ? 2 : (ViewDiscussionSystem.theUser.getNewStaffRole() ? 1 : 0);
		
		// If hiding, set to the user's level. If unhiding, set to 0.
		int newVisibilityLevel = shouldHide ? yourPrivilege : 0;
		
		// Update the database
		postStorage.updatePostVisibility(post.getPostId(), newVisibilityLevel);
		
		// Update the local object so the UI stays in sync without a full DB refresh
		post.setVisibilityLevel(newVisibilityLevel);
	}
}