package database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import entityClasses.Invitation;
import entityClasses.Post;
import entityClasses.PostReport;
import entityClasses.Reply;
import entityClasses.User;

/*******
 * <p>
 * Title: Database Class.
 * </p>
 * 
 * <p>
 * Description: This is an in-memory database built on H2. Detailed
 * documentation of H2 can be found at https://www.h2database.com/html/main.html
 * (Click on "PDF (2MP) for a PDF of 438 pages on the H2 main page.) This class
 * leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00 2025-04-29 Updated and expanded from the version produce by on
 *          a previous version by Pravalika Mukkiri and Ishwarya Hidkimath
 *          Basavaraj
 * @version 2.01 2025-12-17 Minor updates for Spring 2026
 * @version 2.02 2026-2-1 Changes to fit password update by Diogo Moscato
 */

/*
 * The Database class is responsible for establishing and managing the
 * connection to the database, and performing operations such as user
 * registration, login validation, handling invitation codes, and numerous other
 * database related functions.
 */
public class Database {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	// Shared variables used within this class
	private Connection connection = null; // Singleton to access the database
	private Statement statement = null; // The H2 Statement is used to construct queries

	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewStaffRole;
	private boolean currentNewStudentRole;

	/*******
	 * <p>
	 * Method: Database
	 * </p>
	 * 
	 * <p>
	 * Description: The default constructor used to establish this singleton object.
	 * </p>
	 * 
	 */
	public Database() {

	}

	/*******
	 * <p>
	 * Method: connectToDatabase
	 * </p>
	 * 
	 * <p>
	 * Description: Used to establish the in-memory instance of the H2 database from
	 * secondary storage.
	 * </p>
	 *
	 * @throws SQLException when the DriverManager is unable to establish a
	 *                      connection
	 * 
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/*******
	 * <p>
	 * Method: createTables
	 * </p>
	 * 
	 * <p>
	 * Description: Used to create new instances of the database tables used by
	 * this class.
	 * </p>
	 * 
	 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARBINARY(255), " + "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), " + "lastName VARCHAR (255), " + "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), " + "adminRole BOOL DEFAULT FALSE, " + "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);

		// Create the invitation codes table
		// Hannah:
		// Added extra data.
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), " + "role VARCHAR(10), " + "sender VARCHAR(255), " + "dateSent BIGINT, "
				+ "recipient VARCHAR(255), " + "dateAccepted BIGINT, " + "status VARCHAR(20))"; // Status: Outstanding,
																								// Accepted, Revoked
		statement.execute(invitationCodesTable);

		// Create the one-time password table
		String oneTimePasswordTable = "CREATE TABLE IF NOT EXISTS OneTimePasswords ("
				+ "userName VARCHAR(255) PRIMARY KEY, " + "tempPassword VARBINARY(255), "
				+ "originalPassword VARBINARY(255))";
		statement.execute(oneTimePasswordTable);
		
		String postsTable = "CREATE TABLE IF NOT EXISTS Posts (postID BIGINT AUTO_INCREMENT PRIMARY KEY, creationDate VARCHAR(255),"
				+ " postType VARCHAR(255), title VARCHAR(255), postCategory VARCHAR(255), content VARCHAR(MAX)," +
				" authorUsername VARCHAR(255), isEdited BOOL DEFAULT FALSE, isDeleted BOOL DEFAULT FALSE," +
				" visibilityLevel INT DEFAULT 0, publishTime BIGINT DEFAULT 0)";
		statement.execute(postsTable);

		String reportsTable = "CREATE TABLE IF NOT EXISTS PostReports ("
				+ "reportID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				+ "postID BIGINT NOT NULL, "
				+ "reason VARCHAR(MAX) NOT NULL, "
				+ "reporterUsername VARCHAR(255) NOT NULL, "
				+ "createdAt VARCHAR(255) NOT NULL)";
		statement.execute(reportsTable);
		
		String repliesTable = "CREATE TABLE IF NOT EXISTS Replies (replyID BIGINT AUTO_INCREMENT PRIMARY KEY, postID BIGINT, "
				+ "creationDate VARCHAR(255)," + " content VARCHAR(MAX), authorUsername VARCHAR(255), " +
				"isEdited BOOL DEFAULT FALSE, isDeleted BOOL DEFAULT FALSE, isReadByPostAuthor BOOL DEFAULT FALSE," +
				" visibilityLevel INT DEFAULT 0, publishTime BIGINT DEFAULT 0)";
		statement.execute(repliesTable);
		
		String gradeTable = "CREATE TABLE IF NOT EXISTS Grades (studentUsername VARCHAR(255), studentGrade CHAR(2),"
				+ " staffNotes VARCHAR(MAX))";
		statement.execute(gradeTable);
	}

	/*******
	 * <p>
	 * Method: isDatabaseEmpty
	 * </p>
	 * 
	 * <p>
	 * Description: If the user database has no rows, true is returned, else false.
	 * </p>
	 * 
	 * @return true if the database is empty, else it returns false
	 * 
	 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/*******
	 * <p>
	 * Method: getNumberOfUsers
	 * </p>
	 * 
	 * <p>
	 * Description: Returns an integer .of the number of users currently in the user
	 * database.
	 * </p>
	 * 
	 * @return the number of user records in the database.
	 * 
	 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

	/*******
	 * <p>
	 * Method: register(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a new row in the database using the user parameter.
	 * </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or
	 *                      executing it.
	 * 
	 * @param user specifies a user object to be added to the database.
	 * 
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, HASH('SHA-256', CAST(? AS VARBINARY), 1024), ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);

			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);

			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);

			currentMiddleName = user.getMiddleName();
			pstmt.setString(4, currentMiddleName);

			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);

			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);

			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);

			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);

			currentNewStaffRole = user.getNewStaffRole();
			pstmt.setBoolean(9, currentNewStaffRole);

			currentNewStudentRole = user.getNewStudentRole();
			pstmt.setBoolean(10, currentNewStudentRole);

			pstmt.executeUpdate();
		}

	}

	/*******
	 * <p>
	 * Method: List getUserList()
	 * </p>
	 * 
	 * <P>
	 * Description: Generate an List of Strings, one for each user in the database,
	 * starting with "<Select User>" at the start of the list.
	 * </p>
	 * 
	 * @param includeAdmin	this tells the method whether to include the admins or not.
	 * 
	 * 
	 * @return a list of userNames found in the database.
	 */
	public List<String> getUserList(boolean includeAdmin) {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		if (!includeAdmin) {
			query += " WHERE adminRole = FALSE";
		}
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
//		System.out.println(userList);
		return userList;
	}
	
	/*******
	 * <p>
	 * Method: List getStudentUserList()
	 * </p>
	 * 
	 * <p>
	 * Description: Generate a List of Strings, one for each student in the database,
	 * starting with "<Select User>" at the start of the list. This list contains the 
	 * usernames of the students, not an actual User object.
	 * </p>
	 * 
	 * @return a list of the student usernames found in the database.
	 */
	public List<String> getStudentUserList() {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB WHERE adminRole = FALSE AND newRole1 = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
		return userList;
	}

	/**
	 * <p>
	 * Method: getInvitationList
	 * </p>
	 * <p>
	 * Description: Retrieves all invitation records to display history and status.
	 * </p>
	 * 
	 * @return A List of Invitation objects containing all table data.
	 */
	public List<Invitation> getInvitationList() {
		List<Invitation> list = new ArrayList<>();
		String query = "SELECT * FROM InvitationCodes";

		// try-with-resources ensures the statement and result set close automatically
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			// Loop through every row returned by the database
			while (rs.next()) {
				// Extract data from the current row using the column names
				String code = rs.getString("code");
				String email = rs.getString("emailAddress");
				String role = rs.getString("role");
				String sender = rs.getString("sender");
				long dateSent = rs.getLong("dateSent");
				String recipient = rs.getString("recipient");
				long dateAccepted = rs.getLong("dateAccepted");
				String status = rs.getString("status");

				// Create the object and add it to the list
				list.add(new Invitation(code, email, role, sender, dateSent, recipient, dateAccepted, status));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * <p>
	 * Method: revokeInvitation
	 * </p>
	 * <p>
	 * Description: Marks an invitation as 'Revoked' in the database.
	 * </p>
	 * 
	 * @param code The unique invitation code to revoke.
	 */
	public void revokeInvitation(String code) {
		String query = "UPDATE InvitationCodes SET status = 'Revoked' WHERE code = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			// Replace the first '?' with the actual code string
			pstmt.setString(1, code);

			// Execute the update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: boolean authenticateUser(String username, String plaintextPassword)
	 * </p>
	 * 
	 * <p>
	 * Description: Check if a user with the specified username and password exists in the database.
	 * </p>
	 * 
	 * @param username The username as typed in by the user.
	 * @param plaintextPassword The password as typed in by the user.
	 * 
	 * @return True if the specified username and password are a valid combination.
	 */
	public boolean authenticateUser(String username, String plaintextPassword) {
	    String query = "SELECT * FROM userDB WHERE userName = ? AND password = HASH('SHA-256', CAST(? AS VARBINARY), 1024)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, plaintextPassword);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next(); // Return true if hash matches
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	/*******
	 * <p>
	 * Method: boolean doesUserExist(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Check to see that a user with the specified username is in the
	 * table.
	 * </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it
	 *                 is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	/*******
	 * <p>
	 * Method: int getNumberOfRoles(User user)
	 * </p>
	 * 
	 * <p>
	 * Description: Determine the number of roles a specified user plays.
	 * </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in
	 *             the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */
	// Get the number of roles that this user plays
	public int getNumberOfRoles(User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole())
			numberOfRoles++;
		if (user.getNewStaffRole())
			numberOfRoles++;
		if (user.getNewStudentRole())
			numberOfRoles++;
		return numberOfRoles;
	}

	/*******
	 * <p>
	 * Method: String generateInvitationCode(String emailAddress, String role)
	 * </p>
	 * 
	 * <p>
	 * Description: Given an email address and a roles, this method establishes and
	 * invitation code and adds a record to the InvitationCodes table. When the
	 * invitation code is used, the stored email address is used to establish the
	 * new user and the record is removed from the table.
	 * </p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role         specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely
	 *         setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	// Hannah:
	// Update method signature to accept "sender"
	public String generateInvitationCode(String emailAddress, String role, String sender) {
		String code = UUID.randomUUID().toString().substring(0, 6);

		// Added 'sender' to the insert columns and values
		String query = "INSERT INTO InvitationCodes (code, emailaddress, role, sender, dateSent, status) "
				+ "VALUES (?, ?, ?, ?, ?, 'Outstanding')";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);
			pstmt.setString(4, sender); // Save the sender
			pstmt.setLong(5, System.currentTimeMillis());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	/*******
	 * <p>
	 * Method: int getNumberOfInvitations()
	 * </p>
	 * 
	 * <p>
	 * Description: Determine the number of outstanding invitations in the table.
	 * </p>
	 * 
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE status = 'Outstanding'";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p>
	 * Method: createOneTimePassword(String username, String tempPassword)
	 * </p>
	 *
	 * <p>
	 * Description: Create a one-time password for a user by storing the original
	 * password and updating the user's password to the temporary value. If an OTP
	 * already exists it is replaced.
	 * </p>
	 */
	public void createOneTimePassword(String username, String tempPassword) {
		String query = "SELECT password FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String original = rs.getString("password");
				// Upsert into OneTimePasswords
				String upsert = "MERGE INTO OneTimePasswords (userName, tempPassword, originalPassword) KEY(userName) VALUES (?, ?, ?)";
				try (PreparedStatement pstmt2 = connection.prepareStatement(upsert)) {
					pstmt2.setString(1, username);
					pstmt2.setString(2, tempPassword);
					pstmt2.setString(3, original);
					pstmt2.executeUpdate();
				}
				// Update the user's password to the temporary password
				String update = "UPDATE userDB SET password = HASH('SHA-256', CAST(? AS VARBINARY), 1024) WHERE userName = ?";
				try (PreparedStatement pstmt3 = connection.prepareStatement(update)) {
					pstmt3.setString(1, tempPassword);
					pstmt3.setString(2, username);
					pstmt3.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: revertOneTimePasswordIfMatch(String username, String usedPassword)
	 * </p>
	 *
	 * <p>
	 * Description: If an OTP is active for the specified user and the used password
	 * matches the temporary password, restore the original password and remove the
	 * OTP record.
	 * </p>
	 *
	 * @return true if a revert was performed, else false
	 */
	public boolean revertOneTimePasswordIfMatch(String username, String usedPassword) {
		String query = "SELECT tempPassword, originalPassword FROM OneTimePasswords WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String temp = rs.getString("tempPassword");
				String original = rs.getString("originalPassword");
				if (temp != null && temp.equals(usedPassword)) {
					// Restore the original password
					String update = "UPDATE userDB SET password = ? WHERE userName = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(update)) {
						pstmt2.setString(1, original);
						pstmt2.setString(2, username);
						pstmt2.executeUpdate();
					}
					// Delete the OTP record
					String delete = "DELETE FROM OneTimePasswords WHERE userName = ?";
					try (PreparedStatement pstmt3 = connection.prepareStatement(delete)) {
						pstmt3.setString(1, username);
						pstmt3.executeUpdate();
					}
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: boolean emailaddressHasBeenUsed(String emailAddress)
	 * </p>
	 * <p>
	 * Description: Checks if the email address is associated with an active user in
	 * the userDB table. This ensures that if a user was deleted, the email is freed
	 * up to be invited again.
	 * </p>
	 * 
	 * @param emailAddress is a string that (should) uniquely identify a user.
	 * @return true if the email address is found in userDB, else return false.
	 */
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		// If the user was deleted, they won't be in userDB, so we return false.
		// This allows you to re-invite someone who was previously deleted.
		String query = "SELECT COUNT(*) AS count FROM userDB WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * <p>
	 * Method: boolean invitationExists(String email, String role)
	 * </p>
	 * 
	 * <p>
	 * Description: Checks if an invitation exists based on a specified
	 * email and role
	 * </p>
	 * 
	 * @param email	email to check
	 * @param role	role of invited user
	 * @return true if the invitation exists, otherwise false
	 */
	public boolean invitationExists(String email, String role) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ? AND role = ? AND status = 'Outstanding'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, role);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p>
	 * Method: String getRoleGivenAnInvitationCode(String code)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the role associated with an invitation code.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p>
	 * Method: String getEmailFromInvitationCode(String code)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the email addressed associated with an invitation code.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the email address for the code or an empty string.
	 * 
	 */
	public String getEmailFromInvitationCode(String code) {
		String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/*******
	 * <p>
	 * Method: String getUsernameFromInvitationCode(String code)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the username associated with an invitation code.
	 * </p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the username for the code or an empty string.
	 * 
	 */
	public String getUsernameFromInvitationCode(String code) {
		String query = "SELECT recipient FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("recipient");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p>
	 * Method: void markInvitationAsUsed(String code)
	 * </p>
	 * *
	 * <p>
	 * Description: Updates an invitation record (and any other invitations with the
	 * same email address) to 'Accepted' status.
	 * 
	 * No longer remove used invitations. Mark them as used instead. </p>
	 * * @param code is the 6 character String invitation code used to find the
	 * email
	 */
	public void markInvitationAsUsed(String code, String username) {
		// 1. Find the email address for this code
		String email = getEmailFromInvitationCode(code);

		if (email == null || email.isEmpty())
			return;

		// 2. Update ALL invitations for this email address to Accepted
		// This ensures if bob@test.com is invited for Staff AND Student,
		// both are marked accepted when he creates his account.
		String query = "UPDATE InvitationCodes SET status = 'Accepted', dateAccepted = ?, recipient = ? WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setLong(1, System.currentTimeMillis());
			pstmt.setString(2, username);
			pstmt.setString(3, email);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getFirstName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the first name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username
	 * 
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("firstName"); // Return the first name if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateFirstName(String username, String firstName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the first name of a user given that user's username and
	 * the new first name.
	 * </p>
	 * 
	 * @param username  is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 * 
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
		String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentFirstName = firstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getMiddleName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the middle name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username
	 * 
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("middleName"); // Return the middle name if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateMiddleName(String username, String middleName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the middle name of a user given that user's username and
	 * the new middle name.
	 * </p>
	 * 
	 * @param username   is the username of the user
	 * 
	 * @param middleName is the new middle name for the user
	 * 
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
		String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, middleName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentMiddleName = middleName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getLastName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the last name of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username
	 * 
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("lastName"); // Return last name role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateLastName(String username, String lastName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the middle name of a user given that user's username and
	 * the new middle name.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param lastName is the new last name for the user
	 * 
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
		String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentLastName = lastName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getPreferredFirstName(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the preferred first name of a user given that user's
	 * username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username
	 * 
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("firstName"); // Return the preferred first name if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updatePreferredFirstName(String username, String
	 * preferredFirstName)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the preferred first name of a user given that user's
	 * username and the new preferred first name.
	 * </p>
	 * 
	 * @param username           is the username of the user
	 * 
	 * @param preferredFirstName is the new preferred first name for the user
	 * 
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
		String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPreferredFirstName = preferredFirstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: String getEmailAddress(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get the email address of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username
	 * 
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("emailAddress"); // Return the email address if user exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p>
	 * Method: void updateEmailAddress(String username, String emailAddress)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the email address name of a user given that user's
	 * username and the new email address.
	 * </p>
	 * 
	 * @param username     is the username of the user
	 * 
	 * @param emailAddress is the new preferred first name for the user
	 * 
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
		String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentEmailAddress = emailAddress;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: void updatePassword(String username, String password)
	 * </p>
	 * 
	 * <p>
	 * Description: Update the password of a user given that user's username and
	 * the new password.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param password is the new password for the user
	 * 
	 */
	// update the password
	public void updatePassword(String username, String password) {
		String query = "UPDATE userDB SET password = HASH('SHA-256', CAST(? AS VARBINARY), 1024) WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPassword = password;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p>
	 * Method: boolean getUserAccountDetails(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Get all the attributes of a user given that user's username.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 * 
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			currentUsername = rs.getString(2);
			currentPassword = rs.getString(3);
			currentFirstName = rs.getString(4);
			currentMiddleName = rs.getString(5);
			currentLastName = rs.getString(6);
			currentPreferredFirstName = rs.getString(7);
			currentEmailAddress = rs.getString(8);
			currentAdminRole = rs.getBoolean(9);
			currentNewStaffRole = rs.getBoolean(10);
			currentNewStudentRole = rs.getBoolean(11);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * <p>
	 * Method: boolean updateUserRole(String username, String role, String value)
	 * </p>
	 * 
	 * <p>
	 * Description: Update a specified role for a specified user's and set and
	 * update all the current user attributes.
	 * </p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param role     is string that specifies the role to update
	 * 
	 * @param value    is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 * 
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStaffRole = true;
				else
					currentNewStaffRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStudentRole = true;
				else
					currentNewStudentRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}

	// Attribute getters for the current user
	/*******
	 * <p>
	 * Method: String getCurrentUsername()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's username.
	 * </p>
	 * 
	 * @return the username value is returned
	 * 
	 */
	public String getCurrentUsername() {
		return currentUsername;
	};

	/*******
	 * <p>
	 * Method: String getCurrentPassword()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's password.
	 * </p>
	 * 
	 * @return the password value is returned
	 * 
	 */
	public String getCurrentPassword() {
		return currentPassword;
	};

	/*******
	 * <p>
	 * Method: String getCurrentFirstName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's first name.
	 * </p>
	 * 
	 * @return the first name value is returned
	 * 
	 */
	public String getCurrentFirstName() {
		return currentFirstName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentMiddleName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's middle name.
	 * </p>
	 * 
	 * @return the middle name value is returned
	 * 
	 */
	public String getCurrentMiddleName() {
		return currentMiddleName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentLastName()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's last name.
	 * </p>
	 * 
	 * @return the last name value is returned
	 * 
	 */
	public String getCurrentLastName() {
		return currentLastName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentPreferredFirstName(
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's preferred first name.
	 * </p>
	 * 
	 * @return the preferred first name value is returned
	 * 
	 */
	public String getCurrentPreferredFirstName() {
		return currentPreferredFirstName;
	};

	/*******
	 * <p>
	 * Method: String getCurrentEmailAddress()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's email address name.
	 * </p>
	 * 
	 * @return the email address value is returned
	 * 
	 */
	public String getCurrentEmailAddress() {
		return currentEmailAddress;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentAdminRole()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Admin role attribute.
	 * </p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 * 
	 */
	public boolean getCurrentAdminRole() {
		return currentAdminRole;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentNewStaffRole()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Staff role attribute.
	 * </p>
	 * 
	 * @return true if this user plays a Staff role, else false
	 * 
	 */
	public boolean getCurrentNewStaffRole() {
		return currentNewStaffRole;
	};

	/*******
	 * <p>
	 * Method: boolean getCurrentNewStudentRole()
	 * </p>
	 * 
	 * <p>
	 * Description: Get the current user's Student role attribute.
	 * </p>
	 * 
	 * @return true if this user plays a Student role, else false
	 * 
	 */
	public boolean getCurrentNewStudentRole() {
		return currentNewStudentRole;
	};

	/*******
	 * <p>
	 * Method: boolean deleteUser()
	 * </p>
	 * 
	 * <p>
	 * Description: Delete the user row from the database by username.
	 * </p>
	 * 
	 * @return true if this non-admin user is deleted, else false
	 * 
	 */
	public boolean deleteUser(String userName) {
		String deleteRow = "DELETE FROM userDB WHERE userName = ? AND adminRole = FALSE"; // finds username and checks
																							// their role

		try {
			PreparedStatement pstmt = connection.prepareStatement(deleteRow);
			pstmt.setString(1, userName);

			int rowsDeleted = pstmt.executeUpdate(); // stores number of rows that actually got deleted
			// free memory
			pstmt.close();

			// return true if number of rows deleted in the database is > 0
			return rowsDeleted > 0;
		} catch (SQLException e) {
			System.out.println("Error deleting user " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p>
	 * Debugging method
	 * </p>
	 * 
	 * <p>
	 * Description: Debugging method that dumps the database of the console.
	 * </p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
			for (int i = 0; i < meta.getColumnCount(); i++) {
				System.out.println(meta.getColumnLabel(i + 1) + ": " + resultSet.getString(i + 1));
			}
			System.out.println();
		}
		resultSet.close();
	}

	/*******
	 * <p>
	 * Method: void closeConnection()
	 * </p>
	 * 
	 * <p>
	 * Description: Closes the database statement and connection.
	 * </p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/*******
	 * <p>
	 * Method: registerPost(Post post)
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a new row in the database using the post parameter.
	 * </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or
	 *                      executing it.
	 * 
	 * @param post specifies a post object to be added to the database.
	 * 
	 */
	public void registerPost(Post post) throws SQLException {
		String insertPost = "INSERT INTO Posts (creationDate, postType, title, postCategory, "
				+ "content, authorUsername, visibilityLevel, publishTime) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(insertPost);
		
		pstmt.setString(1, post.getDate().toString());
		pstmt.setString(2, post.getTypeOfPost().name());
		pstmt.setString(3, post.getTitle());
		pstmt.setString(4, post.getCategory().name());
		pstmt.setString(5, post.getContent());
		pstmt.setString(6, post.getAuthorUsername());
		pstmt.setInt(7, post.getVisibilityLevel());
		pstmt.setLong(8, post.getPublishTime());
		
		pstmt.executeUpdate();
		
		ResultSet rs = pstmt.getGeneratedKeys();
		if (rs.next()) {
			post.setPostId(rs.getLong(1));
		}
	}
	
	/*******
	 * <p>
	 * Method: List getPostsList()
	 * </p>
	 * 
	 * <P>
	 * Description: Generate an List of Posts, one for each post in the database.
	 * </p>
	 * 
	 * @return a list of posts found in the database.
	 */
	public List<Post> getPostsList() {
		List<Post> postsList = new ArrayList<Post>();
		
		// Determine the privilege level of the currently logged-in user
		int userPrivilege = currentAdminRole ? 2 : (currentNewStaffRole ? 1 : 0);
		long currentTime = System.currentTimeMillis(); // UNIX time in milliseconds

		// Filter based on visibility level and publish time
		String query = "SELECT * FROM Posts WHERE visibilityLevel <= ? AND (publishTime <= ? OR authorUsername = ? OR ? >= 1)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userPrivilege);
			pstmt.setLong(2, currentTime);
			pstmt.setString(3, currentUsername);
			pstmt.setInt(4, userPrivilege);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				boolean isDeleted = rs.getBoolean("isDeleted");
				String author = rs.getString("authorUsername");
				
				// If deleted, only return the post if the current user has a STRICTLY HIGHER privilege level than the author.
				if (isDeleted) {
					int authorPrivilege = getUserPrivilegeLevel(author);
					if (userPrivilege <= authorPrivilege) {
						continue; // Skip adding this post for the author and lower-privileged users
					}
				}
				
				Post newPost = new Post(rs.getLong("postID"), rs.getString("creationDate"), rs.getString("postType"),
						rs.getString("title"), rs.getString("postCategory"), rs.getString("content"), 
						author, rs.getBoolean("isEdited"), isDeleted,
						rs.getInt("visibilityLevel"), rs.getLong("publishTime"));
				postsList.add(newPost);
			}
		} catch (SQLException e) {
			return null;
		}
		
		return postsList;
	}
	
	/*******
	 * <p>
	 * Method: void updatePost(Long postId, String title, String content)
	 * </p>
	 * <p>
	 * Description: Duplicates the old state of the post and all its replies behind the scenes, 
	 * marks them as deleted, and updates the post with the new edits.
	 * </p>
	 * @throws SQLException
	 * @param postId	id of post
	 * @param title		new title
	 * @param content	new content
	 */
	public void updatePost(Long postId, String title, String content) throws SQLException {
		String fetchPost = "SELECT * FROM Posts WHERE postID = ?";
		long historyPostId = -1;
		
		// Create duplicate of post and all replies and mark as deleted
		try (PreparedStatement pstmt = connection.prepareStatement(fetchPost)) {
			pstmt.setLong(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String insertOldPost = "INSERT INTO Posts (creationDate, postType, title, postCategory, content, authorUsername, isEdited, isDeleted, visibilityLevel, publishTime) VALUES (?, ?, ?, ?, ?, ?, ?, TRUE, ?, ?)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertOldPost, Statement.RETURN_GENERATED_KEYS)) {
					insertStmt.setString(1, rs.getString("creationDate"));
					insertStmt.setString(2, rs.getString("postType"));
					insertStmt.setString(3, rs.getString("title"));
					insertStmt.setString(4, rs.getString("postCategory"));
					insertStmt.setString(5, rs.getString("content"));
					insertStmt.setString(6, rs.getString("authorUsername"));
					insertStmt.setBoolean(7, rs.getBoolean("isEdited"));
					insertStmt.setInt(8, rs.getInt("visibilityLevel"));
					insertStmt.setLong(9, rs.getLong("publishTime"));
					insertStmt.executeUpdate();
					
					ResultSet keys = insertStmt.getGeneratedKeys();
					if (keys.next()) {
						historyPostId = keys.getLong(1);
					}
				}
			}
		}

		// Duplicate all replies and link them to the deleted clone
		if (historyPostId != -1) {
			String fetchReplies = "SELECT * FROM Replies WHERE postID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(fetchReplies)) {
				pstmt.setLong(1, postId);
				ResultSet rs = pstmt.executeQuery();
				
				String insertOldReply = "INSERT INTO Replies (postID, creationDate, content, authorUsername, isEdited, isDeleted, isReadByPostAuthor, visibilityLevel, publishTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertOldReply)) {
					while (rs.next()) {
						insertStmt.setLong(1, historyPostId);
						insertStmt.setString(2, rs.getString("creationDate"));
						insertStmt.setString(3, rs.getString("content"));
						insertStmt.setString(4, rs.getString("authorUsername"));
						insertStmt.setBoolean(5, rs.getBoolean("isEdited"));
						insertStmt.setBoolean(6, rs.getBoolean("isDeleted"));
						insertStmt.setBoolean(7, rs.getBoolean("isReadByPostAuthor"));
						insertStmt.setInt(8, rs.getInt("visibilityLevel"));
						insertStmt.setLong(9, rs.getLong("publishTime"));
						insertStmt.executeUpdate();
					}
				}
			}
		}

		// Update the post with the new edited content as mark as isEdited
		String updateActivePost = "UPDATE Posts SET title = ?, content = ?, isEdited = TRUE WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateActivePost)) {
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setLong(3, postId);
			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p>
	 * Method: void deletePost(long postId, String deleteMessage)
	 * </p>
	 * 
	 * <p>
	 * Description: Soft deletes a post.
	 * </p>
	 * 
	 * @throws SQLExecption
	 * @param postId			id of post
	 * @param deleteMessage		deletion message
	 * 
	 */
	public void deletePost(long postId, String deleteMessage) throws SQLException {
		String query = "UPDATE Posts SET isDeleted = TRUE WHERE postID = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, postId);
			pstmt.executeUpdate();
	}

	/*******
	 * <p>
	 * Method: registerPostReport(long postId, String reason, String reporterUsername)
	 * </p>
	 *
	 * <p>
	 * Description: Persists a report reason for a specific post.
	 * </p>
	 *
	 * @throws SQLException
	 */
	public void registerPostReport(long postId, String reason, String reporterUsername) throws SQLException {
		String query = "INSERT INTO PostReports (postID, reason, reporterUsername, createdAt) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setLong(1, postId);
			pstmt.setString(2, reason);
			pstmt.setString(3, reporterUsername);
			pstmt.setString(4, LocalDateTime.now().toString());
			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p>
	 * Method: getPostReportsList()
	 * </p>
	 *
	 * <p>
	 * Description: Returns all post reports ordered from newest to oldest.
	 * </p>
	 *
	 * @return list of reported posts with reason and reporter metadata
	 */
	public List<PostReport> getPostReportsList() {
		List<PostReport> reportsList = new ArrayList<>();
		String query = "SELECT reportID, postID, reason, reporterUsername, createdAt FROM PostReports ORDER BY reportID DESC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				reportsList.add(new PostReport(
					rs.getLong("reportID"),
					rs.getLong("postID"),
					rs.getString("reason"),
					rs.getString("reporterUsername"),
					LocalDateTime.parse(rs.getString("createdAt"))
				));
			}
		} catch (SQLException e) {
			return new ArrayList<>();
		}

		return reportsList;
	}
	
	/*******
	 * <p>
	 * Method: registerReply(Reply reply)
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a new row in the database using the reply parameter.
	 * </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or
	 *                      executing it.
	 * 
	 * @param reply specifies a reply object to be added to the database.
	 * 
	 */
	public void registerReply(Reply reply) throws SQLException {
		String insertReply = "INSERT INTO Replies (postID, creationDate, "
				+ "content, authorUsername, visibilityLevel, publishTime) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(insertReply);
		
		pstmt.setLong(1, reply.getPostId());
		pstmt.setString(2, reply.getDatePosted().toString());
		pstmt.setString(3, reply.getContent());
		pstmt.setString(4, reply.getAuthorUsername());
		pstmt.setInt(5, reply.getVisibilityLevel());
		pstmt.setLong(6, reply.getPublishTime());
		
		pstmt.executeUpdate();
		
		ResultSet rs = pstmt.getGeneratedKeys();
		if (rs.next()) {
			reply.setReplyId(rs.getLong(1));
		}
	}
	
	/*******
	 * <p>
	 * Method: List getRepliesList()
	 * </p>
	 * 
	 * <P>
	 * Description: Generate an List of Replies, one for each reply in the database.
	 * </p>
	 * 
	 * @return a list of replies found in the database.
	 */
	public List<Reply> getRepliesList() {
		List<Reply> repliesList = new ArrayList<Reply>();
		
		// Determine the privilege level of the currently logged-in user
		int userPrivilege = currentAdminRole ? 2 : (currentNewStaffRole ? 1 : 0);
		long currentTime = System.currentTimeMillis(); // UNIX time in milliseconds

		// Filter based on visibility level and publish time
		String query = "SELECT * FROM Replies WHERE visibilityLevel <= ? AND (publishTime <= ? OR authorUsername = ? OR ? >= 1)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userPrivilege);
			pstmt.setLong(2, currentTime);
			pstmt.setString(3, currentUsername);
			pstmt.setInt(4, userPrivilege);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				boolean isDeleted = rs.getBoolean("isDeleted");
				String author = rs.getString("authorUsername");
				
				// If deleted, only return the reply if the current user has a STRICTLY HIGHER privilege level than the author.
				if (isDeleted) {
					int authorPrivilege = getUserPrivilegeLevel(author);
					if (userPrivilege <= authorPrivilege) {
						continue; // Skip adding this reply for the author and lower-privileged users
					}
				}

				Reply newReply = new Reply(rs.getLong("replyID"), rs.getLong("postID"), rs.getString("creationDate"), 
						rs.getString("content"), author, rs.getBoolean("isEdited"), isDeleted,
						rs.getBoolean("isReadByPostAuthor"), rs.getInt("visibilityLevel"), rs.getLong("publishTime"));
				repliesList.add(newReply);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return repliesList;
	}
	
	/*******
	 * <p>
	 * Method: void updateReply(Long replyId, String content)
	 * </p>
	 * <p>
	 * Description: Duplicates the old state of the reply behind the scenes, marks it as deleted, 
	 * and updates the active reply with the new edits.
	 * </p>
	 * @throws SQLException
	 * @param replyId	id of reply
	 * @param content	new content
	 */
	public void updateReply(Long replyId, String content) throws SQLException {
		String fetchReply = "SELECT * FROM Replies WHERE replyID = ?";
		
		// Create duplicate reply and mark it as deleted
		try (PreparedStatement pstmt = connection.prepareStatement(fetchReply)) {
			pstmt.setLong(1, replyId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String insertOldReply = "INSERT INTO Replies (postID, creationDate, content, authorUsername, isEdited, isDeleted, isReadByPostAuthor, visibilityLevel, publishTime) VALUES (?, ?, ?, ?, ?, TRUE, ?, ?, ?)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertOldReply)) {
					insertStmt.setLong(1, rs.getLong("postID"));
					insertStmt.setString(2, rs.getString("creationDate"));
					insertStmt.setString(3, rs.getString("content"));
					insertStmt.setString(4, rs.getString("authorUsername"));
					insertStmt.setBoolean(5, rs.getBoolean("isEdited"));
					insertStmt.setBoolean(6, rs.getBoolean("isReadByPostAuthor"));
					insertStmt.setInt(7, rs.getInt("visibilityLevel"));
					insertStmt.setLong(8, rs.getLong("publishTime"));
					insertStmt.executeUpdate();
				}
			}
		}

		// Update the reply with the new edited content as mark as isEdited
		String updateActiveReply = "UPDATE Replies SET content = ?, isEdited = TRUE, isReadByPostAuthor = FALSE WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateActiveReply)) {
			pstmt.setString(1, content);
			pstmt.setLong(2, replyId);
			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p>
	 * Method: void markRepliesAsRead(Long replyId, String content)
	 * </p>
	 * 
	 * <p>
	 * Description: Mark all replies of a post as read by the original author
	 * </p>
	 * 
	 * @throws SQLExecption
	 * @param postId	id of post
	 * 
	 */
	public void markRepliesAsRead(long postId) throws SQLException {
		String query = "UPDATE Replies SET isReadByPostAuthor = TRUE WHERE postID = ? AND isDeleted = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setLong(1, postId);
			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p>
	 * Method: void deleteReply(long replyId, String deleteMessage)
	 * </p>
	 * 
	 * <p>
	 * Description: Soft deletes a reply.
	 * </p>
	 * 
	 * @throws SQLExecption
	 * @param replyId			id of reply
	 * @param deleteMessage		deletion message
	 * 
	 */
	public void deleteReply(long replyId, String deleteMessage) throws SQLException {
		String query = "UPDATE Replies SET isDeleted = TRUE WHERE replyID = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, replyId);
			pstmt.executeUpdate();
	}
	
	/*******
	 * <p>
	 * Method: long getNextReplyId()
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves the next reply ID.
	 * </p>
	 * 
	 * @return next id of a reply in the database
	 */
	public long getNextReplyId() {
	    String query = "SELECT MAX(replyID) FROM Replies";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            long maxId = rs.getLong(1);
	            return maxId + 1;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 1; // if table is empty
	}
	
	/**
	 * <p>
	 * Method: void insertGrade(String username, String grade, String feedback)
	 * </p>
	 * 
	 * <p>
	 * Description: Inserts a new grade record for a student.
	 * </p>
	 *
	 * @param username	the student's username
	 * @param grade		the grade to assign
	 * @param feedback	staff feedback or notes
	 * @throws SQLException if a database access error occurs
	 */
	public void insertGrade(String username, String grade, String feedback) throws SQLException {
		String insertQuery = "INSERT INTO Grades (studentUsername, studentGrade, staffNotes) VALUES (?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(insertQuery);	
		pstmt.setString(1, username);
		pstmt.setString(2, grade);
		pstmt.setString(3, feedback);
		pstmt.executeUpdate();
	}
	
	/**
	 * <p>
	 * Method: String searchStudentGrade(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves the grade for a given student. If
	 * student is not in the table, it returns null.
	 * </p>
	 *
	 * @param username	the student's username
	 * @return the student's grade, or null if not found
	 * @throws SQLException if a database access error occurs
	 */
	public String searchStudentGrade(String username) throws SQLException {
		String query = "SELECT studentGrade FROM Grades WHERE studentUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("studentGrade"); // Return the grade if student is in list
			}
		}
		
		return null;
	}
	
	/**
	 * <p>
	 * Method: String searchStudentFeedback(String username)
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves staff feedback/notes for a given student.
	 * </p>
	 *
	 * @param username	the student's username
	 * @return the staff feedback, or null if not found
	 * @throws SQLException if a database access error occurs
	 */
	public String searchStudentFeedback(String username) throws SQLException {
		String query = "SELECT staffNotes FROM Grades WHERE studentUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("staffNotes"); // Return the staff notes if student is in list
			}
		}
		
		return null;
	}
	
	/**
	 * <p>
	 * Method: void updateGrade(String username, String grade, String feedback)
	 * </p>
	 * 
	 * <p>
	 * Description: Updates the grade and feedback for a given student.
	 * </p>
	 *
	 * @param username	the student's username
	 * @param grade		the new grade
	 * @param feedback	the updated staff feedback, could be empty/null if no feedback given
	 * @throws SQLException if a database access error occurs
	 */
	public void updateGrade(String username, String grade, String feedback) throws SQLException {
		String query = "UPDATE Grades SET studentGrade = ?, staffNotes = ? WHERE studentUsername = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, grade);
			pstmt.setString(2, feedback);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p>
	 * Method: int getUserPrivilegeLevel(String username)
	 * </p>
	 * * <p>
	 * Description: Returns the privilege level for a given user.
	 * 2 for Admin, 1 for Staff, 0 for Student.
	 * </p>
	 */
	public int getUserPrivilegeLevel(String username) {
		String query = "SELECT adminRole, newRole1, newRole2 FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				if (rs.getBoolean("adminRole")) return 2; // Admin
				if (rs.getBoolean("newRole1")) return 1; // Staff
				return 0; // Student
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0; // Default to lowest privilege if user not found
	}
	
	/*******
	 * <p>
	 * Method: void updatePostVisibility(long postId, int visibilityLevel)
	 * </p>
	 * <p>
	 * Description: Updates the visibility level of a specific post.
	 * </p>
	 * @throws SQLException
	 * @param postId           id of the post
	 * @param visibilityLevel  the visibility level
	 * */
	public void updatePostVisibility(long postId, int visibilityLevel) throws SQLException {
		String query = "UPDATE Posts SET visibilityLevel = ? WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, visibilityLevel);
			pstmt.setLong(2, postId);
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p>
	 * Method: void updateReplyVisibility(long replyId, int visibilityLevel)
	 * </p>
	 * <p>
	 * Description: Updates the visibility level of a specific reply.
	 * </p>
	 * @throws SQLException
	 * @param replyId          id of the reply
	 * @param visibilityLevel  the visibility level
	 * */
	public void updateReplyVisibility(long replyId, int visibilityLevel) throws SQLException {
		String query = "UPDATE Replies SET visibilityLevel = ? WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, visibilityLevel);
			pstmt.setLong(2, replyId);
			pstmt.executeUpdate();
		}
	}
}
