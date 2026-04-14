package entityClasses;


/*******
 * <p><b>Class:</b> CharacterCountAnalyzer Class
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 * The CharacterCountAnalyzer class acts acts as a model for testing the 
 * Character Count feature of the Discussion Board System. 
 * It provides functionality for setting user input and the roles of the user.
 * It also assists in validating user input through helpful error messages.
 * 
 * </p>
 * 
 * <p><b>User Stories Supported:</b></p>
 * <ul>
 *   <li>Type input into a post using the textbox</li>
 *   <li>Type input into a reply using the textbox</li>
 *   <li>Character count update after adding input to post/reply.</li>
 *   <li>Character count update after post/reply input deletion.</li>
 * </ul>
 * 
 * <p><b>Design Notes:</b></p>
 * <ul>
 *   <li>Validation checks are performed before operations to prevent
 *       invalid data from being sent to the JUnit tests.</li>
 * </ul>
 * 
 * @author Genesee Harmon
 * @version 1.0
 * 
 */
public class CharacterCountAnalyzer {
	
	private String input = "";
	private String role = "";
	
	/*****
	 * <p> 
	 * Method: setInput(String input)
	 * </p>
	 * 
	 * <p>
	 * Description: This sets a user's input.
	 * </p>
	 * 
	 * <p><b>Notes:</b></p>
	 * <p>
	 *
	 * Validation ensures:
	 * - Content of textbox not empty
	 * </p>
	 * 
	 * @param input	 user input to be typed in
	 * 
	 * @return void
	 */
	public void setInput(String input) {
		if(input == null) {
			this.input = "";
		} else {
			this.input = input;
		}
	}
	
	/*****
	 * <p> 
	 * Method: String setRole(String input, String role)
	 * </p>
	 * 
	 * <p>
	 * Description: This sets a user's role if it is valid and not null.
	 * </p>
	 * 
	 * <p><b>Notes:</b></p>
	 * <p>
	 *
	 * Validation ensures:
	 * - User can only post a post or reply if their role exists and is valid.
	 * </p>
	 * 
	 * @param role    user's role name
	 * 
	 * @return String message to indicate valid or invalid role
	 */
	public String setRole(String role) {
		if(role == null || role.isEmpty()) {
			return "Error: Role is invalid";
		}
		
		// check that user has a valid existing role assigned
		if(role != "admin" && role != "staff" && role != "student") {
			return "Error: Role does not exist";
		}
		
		this.role = role;
		return "Valid role";
		
	}
	
	/*****
	 * <p> 
	 * Method: String getRole()
	 * </p>
	 * 
	 * <p>
	 * Description: This sets the user's role.
	 * </p>
	 * 
	 * @return the user's valid role.
	 */
	public String getRole() {
		return role;
	}

	/*****
	 * <p> 
	 * Method: int getCharacterCount
	 * </p>
	 * 
	 * <p>
	 * Description: This returns the length of the input.
	 * </p>
	 * 
	 * @return the length of the input
	 */
	public int getCharacterCount() {
		return input.length();
	}
}
