package guiPostReplies;

import database.Database;
import entityClasses.Post;
import entityClasses.PostStorage;
import entityClasses.Reply;
import entityClasses.ReplyStorage;
import entityClasses.User;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/*******
 * <p><b>Class: </b>ControllerPostReplies Class.
 * </p>
 * 
 * <p><b>Purpose:</b></p>
 * <p>
 * This class represents the Controller component of the PostReplies system.
 * It handles all user interactions taken with the View and communicates between
 * the View, Model, and reply storage.
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * 	<li>Respond to user actions, such as button clicks</li>
 * 	<li>Validates user permissions to edit or delete a post</li>
 * 	<li>Calls Model logic to refresh replies list</li>
 * 	<li>Updates the View to display new states of the system</li>
 * </ul>
 * 
 * <p><b>Design Notes:</b></p>
 * <ul>
 *   <li>Methods are protected to restrict access</li>
 *   <li>Performs validation before calling Model operations</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.00 2026-02-16 Initial version
 * 
 */
public class ControllerPostReplies {
	/***
	 * Attributes
	 */
	
	/**
	 * The currently selected post being viewed.
	 *
	 *
	 * <p><b>Rationale:</b>
	 * Required to maintain context for reply operations. Used to determine which replies
	 * are displayed.
	 * </p>
	 * 
	 */
	protected static Post selected;
	
	/**
	 * Stores ID of current reply.
	 *
	 * <p><b>Rationale:</b>
	 * Needed to track replies for editing/deleting actions.
	 * </p>
	 */
	protected static long replyId;

	/**
	 * Reference to the database to initialize reply storage.
	 *
	 * <p><b>Rationale:</b>
	 * For data retrieval from the database.
	 * </p>
	 */
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * Storage system used to manage Post data.
	 *
	 * <p><b>Rationale:</b>
	 * Provides CRUD operations for posts.
	 * Separates data management with UI logic.
	 * </p>
	 */
	protected static PostStorage postStorage;
	
	/**
	 * Storage system used to manage Reply data.
	 *
	 * <p><b>Rationale:</b>
	 * Handles CRUD operations for replies and separates data management.
	 * 
	 */
	protected static ReplyStorage replyStorage = new ReplyStorage(theDatabase);
	
	/**********
	 * Default constructor for the class.
	 * 
	 * <p><b>Notes:</b></p>
	 * <p>
	 * This constructor is never used, hence its private status.
	 * </p>
	 * 
	 */
	private ControllerPostReplies() {
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
	protected static boolean canHideReply(User currentUser, String authorUsername) {
		
		// Students can't hide
		if (currentUser.getNewStudentRole()) return false;
		
		// Can users hide their own posts? Yes, they can!
		if (currentUser.getUserName().equals(authorUsername)) return true;
		
		return hasHigherPrivilege(currentUser, authorUsername);
	}
	
	/**********
	 * Paints the window with proper UI elements for the user to view.
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper
	 * content.
	 * </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Prevent duplicates
		ViewPostReplies.postLayout.getChildren().clear();
		
		// Update the displayed title with post title and ID
		ViewPostReplies.label_PageTitle.setText(selected.getTitle() + "	#" + 
				selected.getPostId());
		
		// Add post label
		ViewPostReplies.postLayout.getChildren().add(ViewPostReplies.postLabel);

		// Either show [DELETED] or build the normal controls
		if (selected.isDeleted()) {
			Label deletedLabel = new Label("[DELETED]");
			ViewPostReplies.postLayout.getChildren().add(deletedLabel);
		} else {
			MenuItem editPost = new MenuItem("Edit");
			MenuItem deletePost = new MenuItem("Delete");
			
			// Actually hook up actions so clicking delete does something!!!!
			editPost.setOnAction(_ -> performEditPost(selected));
			deletePost.setOnAction(_ -> {
				String errorMessage = postStorage.deletePost(selected, ViewPostReplies.theUser);
				if (errorMessage != null && !errorMessage.isEmpty()) {
					ViewPostReplies.deleteError.setTitle("Deletion Error");
					ViewPostReplies.deleteError.setHeaderText(null);
					ViewPostReplies.deleteError.setContentText(errorMessage);
					ViewPostReplies.deleteError.showAndWait();
				} else {
					performReturn(); // Return to the discussion board once deleted
				}
			});

			MenuButton threeDotsPost = new MenuButton("...", null, editPost, deletePost);

			// Only show edit/delete to the actual author
			if(selected.getAuthorUsername().equals(ViewPostReplies.theUser.getUserName())) {
				ViewPostReplies.postLayout.getChildren().add(threeDotsPost);
			}
			
			// Check if the current user has the privilege to hide this specific post
			if (canHideReply(ViewPostReplies.theUser, selected.getAuthorUsername())) {
				CheckBox hideCheckBox = new CheckBox("Hide");
				hideCheckBox.setSelected(selected.getVisibilityLevel() > 0);
				hideCheckBox.setOnAction(_ -> {
					int yourPrivilege = ViewPostReplies.theUser.getAdminRole() ? 2 : (ViewPostReplies.theUser.getNewStaffRole() ? 1 : 0);
					int newVisibilityLevel = hideCheckBox.isSelected() ? yourPrivilege : 0;
					postStorage.updatePostVisibility(selected.getPostId(), newVisibilityLevel);
					selected.setVisibilityLevel(newVisibilityLevel);
				});
				
				ViewPostReplies.postLayout.getChildren().add(hideCheckBox);
			}
		}
		
		replyStorage.populateAllReplies();
		
		// Refresh replies to reflect latest data after any changes
		ModelPostReplies.refreshRepliesList(selected);
		
		ViewPostReplies.theStage.setTitle("Post and Replies");
		ViewPostReplies.theStage.setScene(ViewPostReplies.thePostRepliesScene);
		ViewPostReplies.theStage.show();
	}
	
	/**********
	 * <p>Method: performNewReply()
	 * </p>
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p> 
	 * This method opens a dialog window so the user can create a new reply. 
	 * 
	 * This method calls CRUD methods from the storage to delegate data management. 
	 * </p>
	 * 
	 * <p><b>Behavior:</b></p>
	 * <ul>
	 *   <li>Displays a dialog for user input</li>
	 *   <li>Validates that the post is not deleted</li>
	 *   <li>Displays error messages if validation fails</li>
	 *   <li>Refreshes the replies list upon success</li>
	 * </ul>
	 *
	 * <p><b>Validation:</b></p>
	 * <ul>
	 *   <li>Post must not be deleted</li>
	 *   <li>Reply content must be valid</li>
	 * </ul>
	 * 
	 */
	public static void performNewReply() {
		Dialog<String> replyDialog = new Dialog<>();
		ButtonType createReply = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
		replyDialog.getDialogPane().getButtonTypes().addAll(createReply, ButtonType.CANCEL);
		
		VBox layout = new VBox(10);

		TextArea content = new TextArea();
		content.setWrapText(true);
		content.setMinWidth(450);
		content.setPrefRowCount(8);
		
		Label contentLabel = new Label("Content: ");
		
		// character count label with default value of 0
		Label label_characterCount = new Label("Characters: 0 / 1000");
		label_characterCount.setStyle("-fx-text-fill: white;");
		
		// real-time update feature of the character count
		int maxAllowedChars = 1000;
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
		
		layout.getChildren().addAll(contentLabel, content, label_characterCount);
		
		replyDialog.getDialogPane().setContent(layout);
		replyDialog.getDialogPane().setPrefHeight(300);
		replyDialog.getDialogPane().setPrefWidth(400);
		
		Button button = (Button) replyDialog.getDialogPane().lookupButton(createReply);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			if (selected.isDeleted()) {
				ViewPostReplies.replyError.setTitle("Reply Error");
				ViewPostReplies.replyError.setHeaderText(null);
				ViewPostReplies.replyError.setContentText("Cannot reply to this post anymore");
				ViewPostReplies.replyError.showAndWait();
				event.consume();
				return;
			}
			
			String errorMessage = replyStorage.createReply(content.getText(),
					ViewPostReplies.theUser, selected.getPostId());
			
			if (!errorMessage.isEmpty()) {
				ViewPostReplies.replyError.setTitle("Reply Error");
				ViewPostReplies.replyError.setHeaderText(null);
				ViewPostReplies.replyError.setContentText(errorMessage);
				ViewPostReplies.replyError.showAndWait();
				event.consume();
				return;
			}
		});
		String css = ViewPostReplies.class.getResource("/application.css").toExternalForm();
		DialogPane newreplyDialog = replyDialog.getDialogPane();
		newreplyDialog.getStylesheets().add(css);
		
		replyDialog.showAndWait();
		ModelPostReplies.refreshRepliesList(selected);
		
		content.clear();
	}
	
	
	/**********
	 * <p>Method: performEditReply()</p>
	 * 
	 * <p><b>Purpose:</b></p>
	 * <p>
	 * Opens a dialog window so the user can edit an existing reply. 
	 * 
	 * This method calls CRUD functionalities from the storage to delegate data management.
	 * The text is filled with the current content saved in the storage. The user needs to 
	 * enter proper content for it to be accepted.
	 * </p>
	 * 
	 * <p><b>Behavior:</b></p>
	 * <ul>
	 *   <li>Pre-fills dialog with existing reply content</li>
	 *   <li>Validates user input</li>
	 *   <li>Displays error messages if validation fails</li>
	 *   <li>Refreshes replies after editing</li>
	 * </ul>
	 * 
	 * <p><b>Requirements:</b></p>
	 * <ul>
	 *   <li>User must be the author</li>
	 * </ul>
	 *
	 * @param reply the reply to be edited
	 * 
	 */
	public static void performEditReply(Reply reply) {
		
		Dialog<String> replyDialog = new Dialog<>();
		
		ButtonType editReply = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
		replyDialog.getDialogPane().getButtonTypes().addAll(editReply, ButtonType.CANCEL);
		
		VBox layout = new VBox(10);

		TextArea content = new TextArea();
		content.setText(reply.getContent());
		content.setMinWidth(450);
		content.setPrefRowCount(8);
		
		Label contentLabel = new Label("Content: ");
		
		layout.getChildren().addAll(contentLabel, content);
		
		replyDialog.getDialogPane().setContent(layout);
		replyDialog.getDialogPane().setPrefHeight(300);
		replyDialog.getDialogPane().setPrefWidth(400);
		
		Button button = (Button) replyDialog.getDialogPane().lookupButton(editReply);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			String errorMessage = replyStorage.editReply(reply, ViewPostReplies.theUser,
					content.getText());
			
			if (!errorMessage.isEmpty()) {
				ViewPostReplies.editError.setTitle("Editing Error");
				ViewPostReplies.editError.setHeaderText(null);
				ViewPostReplies.editError.setContentText(errorMessage);
				ViewPostReplies.editError.showAndWait();
				event.consume();
				return;
			}
		});
		String css = ViewPostReplies.class.getResource("/application.css").toExternalForm();
		DialogPane newreplyDialog = replyDialog.getDialogPane();
		newreplyDialog.getStylesheets().add(css);
		
		replyDialog.showAndWait();
		ModelPostReplies.refreshRepliesList(selected);
		
		content.clear();
	}
	
	/**********
	 * <p>Method: performEditPost()
	 * </p>
	 * 
	 * <p><b>Purpose:</b></p> 
	 * <p>
	 * Opens a dialog window so the user can edit an existing post. 
	 * 
	 * This method calls CRUD methods from the storage to delegate data management. 
	 * The text is filled with the current title and content saved in the storage. 
	 * To edit a post, the user needs to enter a proper title and content.
	 * 
	 * </p>
	 * 
	 * <p><b>Behavior:</b></p>
	 * <ul>
	 *   <li>Pre-fills fields with current post data</li>
	 *   <li>Validates user input</li>
	 *   <li>Displays error messages if validation fails</li>
	 * </ul>
	 *
	 * <p><b>Requirements:</b></p>
	 * <ul>
	 *   <li>User must be the author</li>
	 *   <li>Post must not be deleted</li>
	 * </ul>
	 *
	 * @param post the post to be edited
	 * 
	 */
	public static void performEditPost(Post post) {
		Dialog<String> postDialog = new Dialog<>();
		
		ButtonType editPost = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
		postDialog.getDialogPane().getButtonTypes().addAll(editPost, ButtonType.CANCEL);
		
		VBox layout = new VBox(10);

		// Pre-fill fields with current post data
		TextField title = new TextField();
		title.setText(post.getTitle());
		title.setMinWidth(450);
		TextArea content = new TextArea();
		content.setText(post.getContent());
		content.setMinWidth(450);
		content.setPrefRowCount(8);
		
		Label titleLabel = new Label("Title: ");
		Label contentLabel = new Label("Content: ");
		
		layout.getChildren().addAll(titleLabel, title, contentLabel, content);
		
		postDialog.getDialogPane().setContent(layout);
		postDialog.getDialogPane().setPrefHeight(300);
		postDialog.getDialogPane().setPrefWidth(400);
		
		Button button = (Button) postDialog.getDialogPane().lookupButton(editPost);
		button.addEventFilter(ActionEvent.ACTION, event -> {
			// Attempts to update post, returns an error if validation fails
			String errorMessage = postStorage.editPost(post, ViewPostReplies.theUser,
					title.getText(), content.getText());
			
			if (!errorMessage.isEmpty()) {
				ViewPostReplies.editError.setTitle("Editing Error");
				ViewPostReplies.editError.setHeaderText(null);
				ViewPostReplies.editError.setContentText(errorMessage);
				ViewPostReplies.editError.showAndWait();
				event.consume();
				return;
			}
		});
		String css = ViewPostReplies.class.getResource("/application.css").toExternalForm();
		DialogPane newreplyDialog = postDialog.getDialogPane();
		newreplyDialog.getStylesheets().add(css);
		
		postDialog.showAndWait();
		
		content.clear();
		title.clear();
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user home page. </p>
	 * 
	 */
	protected static void performReturn() {

		guiDiscussionSystem.ViewDiscussionSystem.displayDiscussionSystem(ViewPostReplies.theStage, ViewPostReplies.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewPostReplies.theStage);
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
	 * <p> Method: toggleReplyVisibility(Reply reply, boolean hide) </p>
	 * <p> Description: Toggles post visibility between 0 and the user's privilege level </p>
	 */
	protected static void toggleReplyVisibility(Reply reply, boolean shouldHide) {
		// Check your privilege
		int yourPrivilege = ViewPostReplies.theUser.getAdminRole() ? 2 : (ViewPostReplies.theUser.getNewStaffRole() ? 1 : 0);
		
		// If hiding, set to the user's level. If unhiding, set to 0.
		int newVisibilityLevel = shouldHide ? yourPrivilege : 0;
		
		// Update the database
		replyStorage.updateReplyVisibility(reply.getReplyId(), newVisibilityLevel);
		
		// Update the local object so the UI stays in sync without a full DB refresh
		reply.setVisibilityLevel(newVisibilityLevel);
	}
}
