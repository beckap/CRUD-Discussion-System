package guiManageInvitations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import entityClasses.Invitation;
import guiManageInvitations.ViewManageInvitations.InvitationRow;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * MVC controller component of the invitation management page.
 * Handles GUI logic and button listener targets.
 * @author Hannah Henderson
 * @version 1.1 [3 Feb. 2026]
 */
public class ControllerManageInvitations {
	
	// This defines the date format for displaying timestamps
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	/**
	 * Populates the invitation list.
	 * Fetches data from the Model, which fetches from the database.
	 */
	protected static void populateInvitationList() {
		
		// Reset state
		ViewManageInvitations.vBox_InvitationList.getChildren().clear();
		ViewManageInvitations.invitationRows.clear();
		ViewManageInvitations.button_Revoke.setDisable(true);
		
		// Create column header row and labels
		HBox headerRow = new HBox(10);
		headerRow.setAlignment(Pos.CENTER_LEFT);
		Label lblHeaderSpacer = createStyledLabel("", 50, true); // Spacer for missing checkbox
		Label lblHeaderSender = createStyledLabel("Sender", 100, true);
		Label lblHeaderEmail = createStyledLabel("Recipient Email", 180, true);
		Label lblHeaderCode = createStyledLabel("Code", 70, true);
		Label lblHeaderRole = createStyledLabel("Role", 70, true);
		Label lblHeaderSent = createStyledLabel("Date Sent", 130, true);
		Label lblHeaderRecipient = createStyledLabel("Recipient User", 160, true);
		Label lblHeaderAccepted = createStyledLabel("Date Accepted", 130, true);
		
		// Bold the header labels
		String boldStyle = "-fx-font-weight: bold;";
		lblHeaderSender.setStyle(boldStyle);
		lblHeaderEmail.setStyle(boldStyle);
		lblHeaderCode.setStyle(boldStyle);
		lblHeaderRole.setStyle(boldStyle);
		lblHeaderSent.setStyle(boldStyle);
		lblHeaderRecipient.setStyle(boldStyle);
		lblHeaderAccepted.setStyle(boldStyle);
		
		// Add labels to row
		headerRow.getChildren().addAll(
			lblHeaderSpacer,
			lblHeaderSender,
			lblHeaderEmail,
			lblHeaderCode,
			lblHeaderRole,
			lblHeaderSent,
			lblHeaderRecipient,
			lblHeaderAccepted
		);
		
		// Add header row to VBox (first item)
		ViewManageInvitations.vBox_InvitationList.getChildren().add(headerRow);
		
		// Fetch invitation list from Model (and ultimately the database)
		List<Invitation> invitations = ModelManageInvitations.getInvitationList();
		if (invitations == null) {
			return;
		}
		
		// We built this table, row-by-row...
		for (Invitation inv : invitations) {
			
			// Create row with 10 columns
			HBox row = new HBox(10);
			row.setAlignment(Pos.CENTER_LEFT);
			
			// Get status (case-insensitive)
			String status = inv.getStatus();
			boolean isOutstanding = "Outstanding".equalsIgnoreCase(status);
			boolean isAccepted = "Accepted".equalsIgnoreCase(status);
			boolean isRevoked = "Revoked".equalsIgnoreCase(status);
			
			// Add checkbox
			CheckBox cb = new CheckBox();
			cb.setMinWidth(50);
			cb.setPrefWidth(50);
			
			// Show status (or recipient username if accepted)
			String recipientText;
			if (isAccepted) {
				recipientText = inv.getRecipient();
			} else if (isRevoked) {
				recipientText = "(Revoked)";
			} else {
				recipientText = "(Outstanding)";
			}
			
			// Format and display date of acceptance (if accepted)
			String dateAcceptedText = "";
			if (isAccepted && inv.getDateAccepted() > 0) {
				dateAcceptedText = sdf.format(new Date(inv.getDateAccepted()));
			}
			
			// Create labels (widths should match header row)
			Label lblSender = createStyledLabel(inv.getSender(), 100, isOutstanding);
			Label lblEmail = createStyledLabel(inv.getEmailAddress(), 180, isOutstanding);
			Label lblCode = createStyledLabel(inv.getCode(), 70, isOutstanding);
			Label lblRole = createStyledLabel(inv.getRole(), 70, isOutstanding);
			Label lblSent = createStyledLabel(sdf.format(new Date(inv.getDateSent())), 130, isOutstanding);
			Label lblRecipient = createStyledLabel(recipientText, 160, isOutstanding);
			Label lblAccepted = createStyledLabel(dateAcceptedText, 130, isOutstanding);
			
			// Register checkbox listener (if enabled)
			if (!isOutstanding) {
				cb.setDisable(true);
				row.setStyle("-fx-background-color: transparent;");
				Label lblNoCheck = createStyledLabel("", 50, true); // Spacer for missing checkbox
				row.getChildren().addAll(lblNoCheck);
			} else {
				cb.selectedProperty().addListener((_, _, _) -> checkRevokeButtonState());
				row.getChildren().addAll(cb);
			}
			
			// Add widgets to row
			row.getChildren().addAll(
				lblSender,
				lblEmail,
				lblCode,
				lblRole,
				lblSent,
				lblRecipient,
				lblAccepted
			);
			
			// Add row to VBox
			ViewManageInvitations.vBox_InvitationList.getChildren().add(row);
			
			// Add row to view's list
			ViewManageInvitations.invitationRows.add(new InvitationRow(cb, inv));
		}
	}
	
	/**
	 * Handles enabling/disabling of the revoke button.
	 * Should only be enabled if there are invitations selected.
	 */
	private static void checkRevokeButtonState() {
		boolean anySelected = ViewManageInvitations.invitationRows.stream().anyMatch(row -> row.checkBox.isSelected());
		ViewManageInvitations.button_Revoke.setDisable(!anySelected);
	}
	
	/**
	 * Revokes all selected invitations.
	 */
	protected static void performRevoke() {
		for (InvitationRow row : ViewManageInvitations.invitationRows) {
			if (row.checkBox.isSelected()) {
				ModelManageInvitations.revokeInvitation(row.invitation.getCode());
			}
		}
		// Refresh list to show updates
		populateInvitationList();
	}
	
	/**
	 * Helper to create labels with consistent styling.
	 */
	private static Label createStyledLabel(String text, double width, boolean isActive) {
		Label l = new Label(text);
		l.setMinWidth(width);
		l.setPrefWidth(width);
		l.setMaxWidth(width); // Ensure alignment
		l.setFont(Font.font("Arial", 14));
		if (!isActive) {
			l.setTextFill(Color.GRAY);
		}
		return l;
	}
	
	/**
	 * Navigates back to the admin home page.
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewManageInvitations.theStage, ViewManageInvitations.theUser);
	}
	
	/**
	 * Navigates back to the login page.
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageInvitations.theStage);
	}
	
	/**
	 * Terminates the application.
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}