package guiPostReplies;

import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/*********
 * <p><b>Class: </b>ModelPostReplies</p>
 * * <p><b>Purpose:</b></p>
 * <p>
 * This class represents the Model component to manage reply data in the PostReplies system.
 * It is responsible of retrieving and formatting data so it can be displayed in the View.
 * </p>
 * * <p><b>Responsibilities:</b></p>
 * <ul>
 * <li>Retrieve replies associated with a specific post</li>
 * <li>Populate the ListView with reply data</li>
 * <li>Define how replies are rendered in the UI</li>
 * </ul>
 * * <p><b>Design notes:</b></p>
 * <p>
 * This class interacts with the View as a simplified design choice for this project.
 * </p>
 * */
public class ModelPostReplies {
	
	/**********
	 * Default constructor for the class.
	 * * <p><b>Notes:</b></p>
	 * <p>
	 * This constructor is never used, hence its private status.
	 * </p>
	 * */
	private ModelPostReplies() {
	}
	
	/**********
	 * <p>Method: refreshRepliesList()
	 * </p>
	 * * Refreshes the replies list displayed in the View.
	 * * <p><b>Purpose:</b></p>
	 * <p>
	 * Retrieves data from the replies associated with the selected post. Instantly updates the View to
	 * display them.
	 * </p>
	 * * <p><b>Behavior:</b></p>
	 * <ul>
	 * <li>Clears the existing replies from the ListView</li>
	 * <li>Fetches replies from the reply storage using the post ID</li>
	 * <li>Populates the ListView with updated reply data</li>
	 * <li>Defines a custom cell factory for rendering replies</li>
	 * </ul>
	 * * <p><b>UI Notes:</b></p>
	 * <ul>
	 * <li>Displays reply text using a label</li>
	 * <li>Provides edit/delete options for replies owned by the current user or staff</li>
	 * </ul>
	 * * @param post	post whose replies are being displayed
	 */
	protected static void refreshRepliesList(Post post) {
		// Clear existing items to avoid duplicate entries when refreshing the list
		ViewPostReplies.repliesList.getItems().clear();
		
		long postId = post.getPostId();
		
		// Retrieve all replies associated with the given post ID from storage
		List<Reply> postReplies = ControllerPostReplies.replyStorage.getRepliesByPostId(postId);
		
		// Adds each reply to the display
		for (Reply reply : postReplies) {
			ControllerPostReplies.replyId = reply.getReplyId();
			ViewPostReplies.repliesList.getItems().add(reply);
		}
		
		// Custom cell factory to control how each reply is displayed in the ListView.
		// This allows combining text content with interactive controls.
		ViewPostReplies.repliesList.setCellFactory(_ -> new ListCell<Reply>() {
        HBox container = new HBox();
        Label text = new Label();
        MenuButton threeDots = new MenuButton("...");

        {
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");
            threeDots.getItems().addAll(editItem, deleteItem);

            editItem.setOnAction(_ -> {
            	Reply currentReply = getItem();
            	if(currentReply != null) {
            		
            		// Ensure only the author of the reply can edit it
            		// Display error if user attempts to edit a reply they do not own
            		if(!currentReply.getAuthorUsername().equals(ViewPostReplies.theUser.getUserName())) {
            			ViewPostReplies.editError.setTitle("Edit Error");
            			ViewPostReplies.editError.setHeaderText(null);
            			ViewPostReplies.editError.setContentText("You cannot edit someone else's post");
            			ViewPostReplies.editError.showAndWait();
                        return;
        			}
            		ControllerPostReplies.performEditReply(currentReply);
            		refreshRepliesList(ControllerPostReplies.selected);
            	}
            });
            
            deleteItem.setOnAction(_ -> {
            	Reply currentReply = getItem();
                if (currentReply != null) {
                	
                	// Attempt to delete the reply using the storage system
                	// Returns an error message if deletion is not allowed
                    String errorMessage =
                        ControllerPostReplies.replyStorage.deleteReply(currentReply, ViewPostReplies.theUser);

                    if (!errorMessage.isEmpty()) {
                    	ViewPostReplies.deleteError.setTitle("Deletion Error");
                    	ViewPostReplies.deleteError.setHeaderText(null);
                    	ViewPostReplies.deleteError.setContentText(errorMessage);
                    	ViewPostReplies.deleteError.showAndWait();
                    }
                    
                    refreshRepliesList(ControllerPostReplies.selected);
                }
            });
            
        }
        
        @Override
        protected void updateItem(Reply item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
            	
            	// Clear previous UI elements to prevent duplication when cells are reused
            	container.getChildren().clear();
            	container.setSpacing(10);
            	container.setAlignment(Pos.CENTER_LEFT);
            	
            	// Format reply text for display using storage helper method
                text.setText(ControllerPostReplies.replyStorage.displayReply(item));
                text.setStyle("-fx-font-size: 14px");
                text.setWrapText(true);
                text.setMaxWidth(600);
                
                // Add the text label first
                container.getChildren().add(text);
                
                // Spacer to push controls to the far right side
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                container.getChildren().add(spacer);
            	
            	// Only shows menu for reply owners or staff.
            	if (item.getAuthorUsername().equals(ViewPostReplies.theUser.getUserName()) ||
            			ViewPostReplies.theUser.getNewStaffRole()) {
            		container.getChildren().add(threeDots);
            	}
            	
            	// Check if the current user has the privilege to hide this specific reply
            	if (ControllerPostReplies.canHideReply(ViewPostReplies.theUser, item.getAuthorUsername())) {
            		HBox hideContainer = new HBox(5);
            		hideContainer.setAlignment(Pos.CENTER);
            		
            		CheckBox hideCheckBox = new CheckBox("Hide");
            		hideCheckBox.setSelected(item.getVisibilityLevel() > 0);
            		
            		hideCheckBox.setOnAction(_ -> {
            			ControllerPostReplies.toggleReplyVisibility(item, hideCheckBox.isSelected());
            		});
            		
            		hideContainer.getChildren().add(hideCheckBox);
            		
            		// Append the hide container to the right of the dropdown menu
            		container.getChildren().add(hideContainer);
            	}
            	
                setGraphic(container);
            }
        }
   	});
	}
}