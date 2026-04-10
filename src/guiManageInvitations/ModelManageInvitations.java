package guiManageInvitations;

import java.util.List;
import database.Database;
import entityClasses.Invitation;

/**
 * MVC model component of the invitation management page.
 * Handles all relevant database interop, for example
 * fetching invitation data and revocation of outstanding invitations.
 * @author Hannah Henderson
 * @version 1.1 [3 Feb. 2026]
 */
public class ModelManageInvitations {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * Fetches the list of all invitations from the database.
	 * @return List of Invitation objects
	 */
	public static List<Invitation> getInvitationList() {
		return theDatabase.getInvitationList();
	}
	
	/**
	 * Revokes a specific outstanding invitation (as identified by its code).
	 * @param code The invitation code to revoke
	 */
	public static void revokeInvitation(String code) {
		theDatabase.revokeInvitation(code);
	}
}