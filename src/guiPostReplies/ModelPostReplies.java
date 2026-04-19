package guiPostReplies;

import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
        MenuButton threeDots = new MenuButton("...");

        {
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");
            threeDots.getItems().addAll(editItem, deleteItem);
            threeDots.setStyle("-fx-font-size: 14px;");

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
            	container.getChildren().clear();
            	container.setSpacing(10);
            	container.setAlignment(Pos.CENTER_LEFT);
            	container.setPadding(Insets.EMPTY);

            	Label authorLabel = new Label(item.getAuthorUsername());
            	authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            	Label dateLabel = new Label(item.getDatePosted().toLocalDate().toString());
            	dateLabel.setStyle("-fx-text-fill: #808388; -fx-font-size: 14px;");

            	HBox header = new HBox(10, authorLabel, dateLabel);
            	header.setAlignment(Pos.CENTER_LEFT);

            	Label contentLabel = new Label(item.getContent());
            	contentLabel.setStyle("-fx-font-size: 14px;");
            	contentLabel.setWrapText(true);
            	contentLabel.setLineSpacing(2);
            	contentLabel.setMaxWidth(Double.MAX_VALUE);

            	VBox replyBox = new VBox(5, header, contentLabel);
            	replyBox.setFillWidth(true);
            	replyBox.setPadding(new Insets(5, 10, 5, 0));
            	replyBox.setMaxWidth(Double.MAX_VALUE);

            	container.getChildren().add(replyBox);

            	Region spacer = new Region();
            	HBox.setHgrow(spacer, Priority.ALWAYS);
            	container.getChildren().add(spacer);

                if (item.isDeleted()) {
                    Label deletedLabel = new Label("[DELETED]");
                    container.getChildren().add(deletedLabel);
                } else {

                    if (item.getAuthorUsername().equals(ViewPostReplies.theUser.getUserName())) {
                        container.getChildren().add(threeDots);
                    }

                    if (ControllerPostReplies.canHideReply(ViewPostReplies.theUser, item.getAuthorUsername())) {
                        HBox hideContainer = new HBox(5);
                        hideContainer.setAlignment(Pos.CENTER);

                        CheckBox hideCheckBox = new CheckBox("Hide");
                        hideCheckBox.setStyle("-fx-font-size: 14px;");
                        hideCheckBox.setSelected(item.getVisibilityLevel() > 0);

                        hideCheckBox.setOnAction(_ -> {
                            ControllerPostReplies.toggleReplyVisibility(item, hideCheckBox.isSelected());
                        });

                        hideContainer.getChildren().add(hideCheckBox);
                        container.getChildren().add(hideContainer);
                    }
                }

                setGraphic(container);
            }
        }
   	});
	}
}