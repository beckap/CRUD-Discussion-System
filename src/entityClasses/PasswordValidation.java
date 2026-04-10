package entityClasses;

/**
 * <p>
 * Title: NameValidation class.
 * </p>
 * 
 * <p>
 * Description: This class validates that the entered first or middle name 
 * of the user is correct and meets the validation requirements. The method
 * checkForValidName returns a specific message should the name not follow
 * rules. </p>
 * 
 * @author Becka Perez Guerrero, Diogo Oliveira Moscato
 * 
 * @version 1.00 2026-01-31 Initial class based on NameValidation
 * 
 */
public class PasswordValidation {
	
	/*
	 * Class attributes that will be used to create specific
	 * messages to the user if they have any errors
	 */
	public static String passwordRecognizerErrorMessage = ""; // The error message text
	public static String passwordRecognizerInput = ""; // The input being processed
	public static int passwordRecognizerIndexofError = -1; // The index of error location
	private static String inputLine = ""; // The input line
	private static char currentChar; // The current character in the line
	private static int currentCharNdx; // The index of the current character
	private static boolean running; // The flag that specifies if the FSM is running

	private static boolean hasUpperCase = false; // Has one Upper case
	private static boolean hasLowerCase = false; // Has one Lower case
	private static boolean hasSpecialChar = false; // Has one special character
	private static boolean hasDigit = false; // Has one digit
	

	// Private method to move to the next character within the limits of the input
	// line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method validates the string parameter to make sure it is a 
	 * valid password entered by the user.
	 * 
	 * @param input The input string
	 * @return An output string that is empty if everything is okay or it is a
	 *         String with a helpful description of the error
	 */
	public static String checkForValidPassword(String input) {
		// Check to ensure that there is input to process
		if (input == null || input.length() < 8) {
			return "Must be at least 8 characters long";
		}
		
		if (input.length() > 64) {
			return "Must be at most 64 characters";
		}

		// Reset validation flags for each password check
		hasUpperCase = false;
		hasLowerCase = false;
		hasSpecialChar = false;
		hasDigit = false;

		// The local variables used to perform the validation loop
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character
		currentChar = input.charAt(0); // The current character from above indexed position

		passwordRecognizerInput = input; // Save a copy of the input
		running = true; // Start the loop


		// The validation continues until the end of the input is reached
		while (running) {
			// Check for uppercase letter
			if(currentChar >= 'A' && currentChar <= 'Z') {
				hasUpperCase = true;
			} 
			// Check for lowercase letter
			else if (currentChar >= 'a' && currentChar <= 'z') {
				hasLowerCase = true;
			} 
			// Check for digit
			else if (currentChar >= '0' && currentChar <= '9') {
				hasDigit = true;
			} 
			// Check for special character
			else if (currentChar == '~' || currentChar == '`' || currentChar == '!'
					|| currentChar == '@' || currentChar == '#' || currentChar == '$'
					|| currentChar == '%' || currentChar == '^' || currentChar == '&'
					|| currentChar == '*' || currentChar == '(' || currentChar == ')' 
					|| currentChar == '-' || currentChar == '_' || currentChar == '+'
					|| currentChar == '[' || currentChar == ']' || currentChar == '{'
					|| currentChar == '}' || currentChar == '|' || currentChar == ','
					|| currentChar == '.' || currentChar == '?' || currentChar == '/') {
				hasSpecialChar = true;
			}

			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar. If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();
			}
			// Should the FSM get here, the loop starts again

		}

		// When the validation halts, we must determine if the situation is an error or not.
		// That depends on the boolean variables determining if the password contains
		// at least one of every requirement, displaying a very specific error message to
		// improve the user experience.
		passwordRecognizerIndexofError = currentCharNdx; // Set index of a possible error;
		passwordRecognizerErrorMessage = "\n";

		if (!hasDigit) {
			passwordRecognizerErrorMessage = "The password must have at least one digit.";
			return passwordRecognizerErrorMessage;
		} else if (!hasUpperCase) {
			passwordRecognizerErrorMessage = "The password must have at least one Uppercase letter.";
			return passwordRecognizerErrorMessage;
		} else if (!hasLowerCase) {
			passwordRecognizerErrorMessage = "The password must have at least one Lowercase letter.";
			return passwordRecognizerErrorMessage;
		} else if (!hasSpecialChar) {
			passwordRecognizerErrorMessage = "The password must have at least one special character.";
			return passwordRecognizerErrorMessage;
		}
		
		return "";
	}

}
