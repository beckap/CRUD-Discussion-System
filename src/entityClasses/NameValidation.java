package entityClasses;

/**
 * <p>
 * Title: NameValidation class.
 * </p>
 * 
 * <p>
 * Description: This class validates that the entered first, middle, last or
 * preferred first name of the user is correct and meets the validation requirements. 
 * The methods checkForValidName and checkForValidLastName
 * return a specific message should the name not follow rules. </p>
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-01-31 Initial class based on UserNameRecognizer
 * @version 1.01 2026-01-31 Added a validation method for last names
 * 
 */

public class NameValidation {
	/*
	 * Class attributes that will be used to create specific
	 * messages to the user if they have any errors
	 */
	public static String nameRecognizerErrorMessage = ""; // The error message text
	public static String nameRecognizerInput = ""; // The input being processed
	public static int nameRecognizerIndexofError = -1; // The index of error location
	private static int state = 0; // The current state value
	private static int nextState = 0; // The next state value
	private static String inputLine = ""; // The input line
	private static char currentChar; // The current character in the line
	private static int currentCharNdx; // The index of the current character
	private static boolean running; // The flag that specifies if the FSM is running


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
	 * valid first, middle, or preferred name entered by the user.
	 * 
	 * @param input The input string
	 * @return An output string that is empty if every things is okay or it is a
	 *         String with a helpful description of the error
	 */
	public static String checkForValidName(String input) {
		// Check to ensure that there is input to process
		if (input.length() <= 0) {
			return "The input is empty";
		}
		
		if (input.length() > 35) {
			return "Must be at most 35 characters";
		}

		// The local variables used to perform the Finite State Machine simulation
		state = 0; // This is the FSM state number
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character
		currentChar = input.charAt(0); // The current character from above indexed position

		nameRecognizerInput = input; // Save a copy of the input
		running = true; // Start the loop
		nextState = -1; // There is no next state

		// This is the place where semantic actions for a transition to the initial
		// state occur

		// The validation continues until the end of the input is reached or
		// at some state the current character does not match any valid transition 
		// to a next state
		while (running) {
			switch (state) {
			case 0:
				// State 0 has 1 valid transition that is addressed by an if statement.

				// The current character is checked against A-Z. If any are matched
				// the FSM goes to state 1

				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z')) { // Check for A-Z
					nextState = 1;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 1:
				// State 1 has two valid transitions,
				// 1: a A-Z, a-z that transitions back to state 1

				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') || // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z')) {
					nextState = 1;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;
				break;
			}

			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar. If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}

		// When the FSM halts, we must determine if the situation is an error or not.
		// That depends
		// of the current state of the FSM and whether or not the whole string has been
		// consumed.
		// This switch directs the execution to separate code for each of the FSM states
		// and that
		// makes it possible for this code to display a very specific error message to
		// improve the
		// user experience.
		nameRecognizerIndexofError = currentCharNdx; // Set index of a possible error;
		nameRecognizerErrorMessage = "\n";

		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			nameRecognizerErrorMessage += "A name must start with a capital letter.\n";
			return nameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state. Check to see if the name is valid. If so
			// we must ensure the whole string has been consumed.
			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				nameRecognizerErrorMessage += "A name may only contain letters\n";
				return nameRecognizerErrorMessage;
			} else {
				// Name is valid
				nameRecognizerIndexofError = -1;
				nameRecognizerErrorMessage = "";
				return nameRecognizerErrorMessage;
			}

		default:
			// This is for the case where we have a state that is outside of the valid
			// range.
			// This should not happen
			return "";
		}
	}
	
	/**********
	 * This method validates the string parameter to make sure it is a 
	 * valid last name entered by the user.
	 * 
	 * @param input The input string
	 * @return An output string that is empty if everything is okay or it is a
	 *         String with a helpful description of the error
	 */
	public static String checkForValidLastName(String input) {
		// Check to ensure that there is input to process
		if (input.length() <= 0) {
			return "The input is empty";
		}
		
		if (input.length() > 100) {
			return "Must be at most 100 characters";
		}

		// The local variables used to perform the Finite State Machine simulation
		state = 0; // This is the state number
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character
		currentChar = input.charAt(0); // The current character from above indexed position

		nameRecognizerInput = input; // Save a copy of the input
		running = true; // Start the loop
		nextState = -1; // There is no next state
		
		
		// The validation continues until the end of the input is reached or
		// at some state the current character does not match any valid transition 
		// to a next state
		while (running) {
			switch (state) {
			case 0:
				// State 0 has 1 valid transition that is addressed by an if statement.

				// The current character is checked against A-Z. If any are matched
				// the FSM goes to state 1

				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z')) { // Check for A-Z
					nextState = 1;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 1:
				// State 1 has 3 valid transitions,
				// 1: a A-Z, a-z that transitions back to state 1
				// 2: a - or space that transitions to state 0

				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') || // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z')) {
					nextState = 1;
				} else if(currentChar == '-' || currentChar == ' '){
					nextState = 0;
				} else {  // If it is none of those characters, the FSM halts
					running = false;
				} 
				
				break;
			}

			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar. If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}

		}

		// When the FSM halts, we must determine if the situation is an error or not.
		// That depends
		// of the current state of the FSM and whether or not the whole string has been
		// consumed.
		// This switch directs the execution to separate code for each of the FSM states
		// and that
		// makes it possible for this code to display a very specific error message to
		// improve the
		// user experience.
		nameRecognizerIndexofError = currentCharNdx; // Set index of a possible error;
		nameRecognizerErrorMessage = "\n";

		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			nameRecognizerErrorMessage += "A last name must start with a capital letter.\n";
			return nameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state. Check to see if the last name is valid. If so
			// wee must ensure the whole string has been consumed.
			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				nameRecognizerErrorMessage += "A last name may only contain letters, hyphens, or spaces\n";
				return nameRecognizerErrorMessage;
			} else {
				// Last name is valid
				nameRecognizerIndexofError = -1;
				nameRecognizerErrorMessage = "";
				return nameRecognizerErrorMessage;
			}

		default:
			// This is for the case where we have a state that is outside of the valid
			// range.
			// This should not happen
			return "";
		}
	}
}
