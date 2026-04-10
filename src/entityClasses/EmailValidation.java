package entityClasses;

/**
 * <p>
 * Title: NameValidation class.
 * </p>
 * 
 * <p>
 * Description: This class validates that the entered email input of the 
 * user is correct and meets the validation requirements. 
 * The method checkForValidEmail returns a specific message 
 * should the email not follow rules. </p>
 * 
 * @author Becka Perez Guerrero
 * 
 * @version 1.00 2026-01-31 Initial class based on other Validation classes
 * 
 */
public class EmailValidation {
	
	/*
	 * Class attributes that will be used to create specific
	 * messages to the user if they have any errors
	 */
	public static String emailRecognizerErrorMessage = ""; // The error message text
	public static String emailRecognizerInput = ""; // The input being processed
	public static int emailRecognizerIndexofError = -1; // The index of error location
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
	 * valid email entered by the user.
	 * 
	 * @param input The input string
	 * @return An output string that is empty if every things is okay or it is a
	 *         String with a helpful description of the error
	 */
	public static String checkForValidEmail(String input) {
		// Check to ensure that there is input to process
		if (input.length() <= 0) {
			return "The input is empty";
		}
		
		if (input.length() > 254) {
			return "Must be at most 254 characters";
		}
		
		if(!input.contains("@")) {
			return "Email does not have a domain.\n";
		}
		// The local variables used to perform the Finite State Machine simulation
		state = 0; // This is the FSM state number
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character
		currentChar = input.charAt(0); // The current character from above indexed position
		emailRecognizerInput = input; // Save a copy of the input
		running = true; // Start the loop
		nextState = -1; // There is no next state
		

		// The validation continues until the end of the input is reached or
		// at some state the current character does not match any valid transition 
		// to a next state
		while (running) {
			switch (state) {
			case 0:
				// State 0 has 1 valid transition

				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') ||     // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z') || // Check for a-z
						(currentChar >= '0' && currentChar <= '9')) {     // Check for 0-9
					nextState = 1;
				} else {  // If it is none of those characters, the FSM halts
					running = false;
				} 

				// The execution of this state is finished
				break;
				
			case 1:
				// State 1 has 3 valid transitions
		
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') ||     // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z') || // Check for a-z
						(currentChar >= '0' && currentChar <= '9')) {     // Check for 0-9
					nextState = 1;
				} else if (currentChar == '.') {   // Check for .
					nextState = 2;
				} else if (currentChar == '@') {    // Check for @
					nextState = 3;
				} else {  // If it is none of those characters, the FSM halts
					running = false;
				} 
		
				// The execution of this state is finished
				break;

			case 2:
				// State 2 has 1 valid transition

				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') ||       // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||   // Check for a-z
						(currentChar >= '0' && currentChar <= '9')) {       // Check for 0-9
					nextState = 1;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;
				break;
				
			case 3:
				// State 3 has 2 valid transitions
				
				if ((currentChar >= 'A' && currentChar <= 'Z') ||       // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||   // Check for a-z
						(currentChar >= '0' && currentChar <= '9')) {       // Check for 0-9
					nextState = 3;
				} else if (currentChar == '.') {
					nextState = 4;
				} else
					running = false;
				
				break;
				
			case 4:
				// State 4 has 1 valid transition and it is a final state
				
				if ((currentChar >= 'A' && currentChar <= 'Z') ||       // Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z')) {   // Check for a-z
					nextState = 4;
				} else 
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

		}
		

		// When the validation halts, we must determine if the situation is an error or not.
		// That depends of the current state of the FSM and whether or not the
		// whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states
		// and that makes it possible for this code to display a very specific error 
		// message to improve the user experience.
		emailRecognizerIndexofError = currentCharNdx; // Set index of a possible error;
		emailRecognizerErrorMessage = "\n";

		
		switch (state) {
		case 0:
			emailRecognizerErrorMessage += "An email must start with an alphanumeric character.\n";
			return emailRecognizerErrorMessage;
		case 1:
			emailRecognizerErrorMessage += "Email must have an alphanumeric character, period, or @.\n";
			return emailRecognizerErrorMessage;
		case 2:
			emailRecognizerErrorMessage += "A period must be followed by an alphanumeric character.\n";
			return emailRecognizerErrorMessage;
			
		case 3:
			emailRecognizerErrorMessage += "An email must have one @ and a single period after it.\n";
			return emailRecognizerErrorMessage;
			
		case 4:
			// State 3 is a final state. Check to see if the email is valid. If so
			// we must ensure the whole string has been consumed.
			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				emailRecognizerErrorMessage += "An email must finish with a valid domain.\n";
				return emailRecognizerErrorMessage;
			} else {
				// Email is valid
				emailRecognizerIndexofError = -1;
				emailRecognizerErrorMessage = "";
				return emailRecognizerErrorMessage;
			}

		default:
			// This is for the case where we have a state that is outside of the valid
			// range.
			// This should not happen
			return "";
		}
	}

}
