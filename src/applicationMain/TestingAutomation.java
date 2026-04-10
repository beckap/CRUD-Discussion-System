package applicationMain;

/*******
 * <p><b>Class:</b> TestingAutomation class (legacy).
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 *   Implements test cases for string input validation via the performTestCase method.
 *   NOTE: For Phase 2 and beyond, unit testing has been migrated to JUnit.
 * </p>
 * 
 * <p><b>Student user stories tested:</b></p>
 * <ul>
 *   <li>Create an account from an invitation code</li>
 *   <li>Edit personal account details (preferred name, etc)</li>
 *   <li>Set a secure password</li>
 * </ul>
 * 
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>The main method executes calls to performTestCase in sequence.</li>
 *   <li>Validation within performTestCase done using simple if/else logic.</li>
 *   <li>Supported: USERNAME, PASSWORD, NAME, LASTNAME, EMAIL</li>
 * </ul>
 * 
 * @author Becka Perez Guerrero
 * @version 1.0 Created
 * 
 * @author Genesee Harmon
 * @version 2.0 Test cases implemented
 * 
 * @author Hannah Henderson
 * @version 2.1 Added documentation
 * 
 */
public class TestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the username test cases **************/
		
		// This is a properly written username positive test
		performTestCase("USERNAME", 1, "Aa_b15678", true);
		
		// This is a properly written username negative test, because the
		// first character of the username is not a letter (A-Z, a-z)
		performTestCase("USERNAME", 2, "1Abcde", false);
		
		// This is a properly written username negative test, because the
		// username is less than 4 characters
		performTestCase("USERNAME", 3, "abc", false);
		
		// This is a properly written username negative test, because the
		// username is longer than 16 characters
		performTestCase("USERNAME", 4, "Abc12345678901234", false);
		
		// This is a properly written username negative test, because a
		// special character is not followed by a UNChar
		performTestCase("USERNAME", 5, "Abc1234..", false);
		
		// This is a properly written username negative test, because the
		// username contains an invalid special character
		performTestCase("USERNAME", 6, "abcd@gmail.com", false);
		
		// This is a properly written username negative test, because the
		// username contains a space
		performTestCase("USERNAME", 7, "Abcd Efgh", false);
		
		/************** End of the username test cases **************/
		
		/************** Start of the password test cases **************/
		
		// This is a properly written username positive test
		performTestCase("PASSWORD", 1, "Password1!", true);
		
		// This is a properly written username negative test, because the
		// password does not contain an uppercase letter
		performTestCase("PASSWORD", 2, "password1!", false);
		
		// This is a properly written username negative test, because the
		// password does not contain a lowercase letter
		performTestCase("PASSWORD", 3, "PASSWORD1!", false);
		
		// This is a properly written username negative test, because the
		// password does not contain a digit
		performTestCase("PASSWORD", 4, "Password*!", false);
		
		// This is a properly written username negative test, because the
		// password does not contain a special character
		performTestCase("PASSWORD", 5, "Password123", false);
		
		// This is a properly written username negative test, because the
		// password is less than 8 characters 
		performTestCase("PASSWORD", 6, "Pw1*!", false);
		
		// This is a properly written username negative test, because the
		// password is longer than 64 characters
		performTestCase("PASSWORD", 7, "PWRdn[CPZ0},MGx^UE@tkgt]Ry2d[c]GqVtkL1aN^7;l*N?pR<=YnaG}8CeHeIE2}\n"
				+ "", false);
		
		/************** End of the password test cases **************/
		
		/************** Start of the name (first, middle, preferred) test cases **************/
		
		// This is a properly written name positive test
		performTestCase("NAME", 1, "Genesee", true);
		
		// This is a properly written username negative test, because the
		// name does not start with an uppercase letter
		performTestCase("NAME", 2, "becka", false);
		
		// This is a properly written username negative test, because the
		// name contains a space
		performTestCase("NAME", 3, "Becka Guerrero", false);
		
		// This is a properly written username negative test, because the
		// name contains a number
		performTestCase("NAME", 4, "Diogo1", false);
		
		// This is a properly written username negative test, because the
		// name contains a special character
		performTestCase("NAME", 5, "Hannah.", false);
		
		// This is a properly written username negative test, because the
		// name is longer than 35 letters 
		performTestCase("NAME", 6, "Aurelianthropomorphosylvandreonephiraxis", false);
		
		/************** End of the name test cases **************/
		
		/************** Start of the last name test cases **************/
		
		// This is a properly written last name positive test
		performTestCase("LASTNAME", 1, "Harmon", true);
		
		// This is a properly written last name positive test
		performTestCase("LASTNAME", 2, "Perez Guerrero", true);
		
		// This is a properly written last name positive test
		performTestCase("LASTNAME", 3, "Bay-Anderson", true);
		
		// This is a properly written last name negative test, because the
		// last name does not start with an uppercase letter
		performTestCase("LASTNAME", 4, "harmon", false);
		
		// This is a properly written last name negative test, because the
		// last name contains a number
		performTestCase("LASTNAME", 5, "Moscato1", false);
		
		// This is a properly written last name test, because the
		// last name contains an invalid special character
		performTestCase("LASTNAME", 6, "Henderson.", false);
		
		// This is a properly written last name negative test, because the
		// last name is longer than 100 characters 
		performTestCase("LASTNAME", 7, "Valenorathquintessimaranetransubstantiaryismxelectrohyperfantasiarealmcosmopalaeontotechnicsvectorial", false);
		
		/************** End of the last name test cases **************/
		
		/************** Start of the email test cases **************/
		
		// This is a properly written email positive test
		performTestCase("EMAIL", 1, "MyEmail123@gmail.com", true);
		
		// This is a properly written email negative test, because the
		// email does not end with a valid domain
		performTestCase("EMAIL", 2, "emailaddress", false);
		
		// This is a properly written username negative test, because the
		// email contains more than one period
		performTestCase("EMAIL", 3, "email123@gmail..com", false);
		
		// This is a properly written username negative test, because the
		// email contains a period that is not followed by a UNChar
		performTestCase("EMAIL", 4, "Diogo.@aol.net", false);
		
		// This is a properly written username negative test, because the
		// email does not contain a period after the special character
		performTestCase("EMAIL", 5, "hannah@aol", false);
		
		// This is a properly written username negative test, because the
		// email is longer than 254 letters 
		performTestCase("EMAIL", 6, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbb"
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
				+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbcccccccccccccc"
				+ "cccccccccccccccccccccccccc@gmail.com", false);
		
		/************** End of the email test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/**
	 * <p>Method: void performTestCase(String, int, String, boolean)</p>
	 * 
	 * <p>
	 * Description:
	 * This method sets up the input value for the test from the input parameters,
	 * displays test execution information, invokes precisely the same method
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 * </p>
	 * 
	 * @param testType One of: USERNAME, PASSWORD, NAME, LASTNAME, EMAIL
	 * @param testCase Index or ID of test case
	 * @param inputText The string to be tested
	 * @param expectedPass Whether the tested string is supposed to be valid or invalid
	 */
	private static void performTestCase(String testType, int testCase, String inputText, boolean expectedPass) {
				
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\n" + testType + " TEST CASE: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		String resultText = "";
		
		/************** Call the method in the appropriate class to process the input **************/
		if(testType == "USERNAME") {
		
			resultText = entityClasses.UsernameValidation.checkForValidUserName(inputText);
			
			/************** Interpret the result and display that interpreted information **************/
			System.out.println();
			
			// If the resulting text is empty, the method accepted the input
			if (resultText != "") {
				 // If the test case expected the test to pass then this is a failure
				 // This will only run if a test case was written incorrectly
				if (expectedPass) {
					System.out.println("***Failure*** The username <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be valid, so this is a failure!\n");
					System.out.println("Error message: " + resultText);
					numFailed++;
				}
				// If the test case expected the test to fail then this is a success
				else {			
					System.out.println("***Success*** The username <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be invalid, so this is a pass!\n");
					System.out.println("Error message: " + resultText);
					numPassed++;
				}
			}
			
			// If the resulting text is empty, the method accepted the input
			else {	
				// If the test case expected the test to pass then this is a success
				if (expectedPass) {	
					System.out.println("***Success*** The username <" + inputText + 
							"> is valid, so this is a pass!");
					numPassed++;
				}
				// If the test case expected the test to fail then this is a failure
				// This will only run if a test case was written incorrectly
				else {
					System.out.println("***Failure*** The username <" + inputText + 
							"> was judged as valid" + 
							"\nBut it was supposed to be invalid, so this is a failure!");
					numFailed++;
				}
			}
		} else if(testType == "PASSWORD") {
			
			resultText = entityClasses.PasswordValidation.checkForValidPassword(inputText);
			
			/************** Interpret the result and display that interpreted information **************/
			System.out.println();
			
			// If the resulting text is empty, the method accepted the input
			if (resultText != "") {
				 // If the test case expected the test to pass then this is a failure
				 // This will only run if a test case was written incorrectly
				if (expectedPass) {
					System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be valid, so this is a failure!\n");
					System.out.println("Error message: " + resultText);
					numFailed++;
				}
				// If the test case expected the test to fail then this is a success
				else {			
					System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be invalid, so this is a pass!\n");
					System.out.println("Error message: " + resultText);
					numPassed++;
				}
			}
			
			// If the resulting text is empty, the method accepted the input
			else {	
				// If the test case expected the test to pass then this is a success
				if (expectedPass) {	
					System.out.println("***Success*** The password <" + inputText + 
							"> is valid, so this is a pass!");
					numPassed++;
				}
				// If the test case expected the test to fail then this is a failure
				// This will only run if a test case was written incorrectly
				else {
					System.out.println("***Failure*** The password <" + inputText + 
							"> was judged as valid" + 
							"\nBut it was supposed to be invalid, so this is a failure!");
					numFailed++;
				}
			}
		} else if(testType == "NAME") {
			
			resultText = entityClasses.NameValidation.checkForValidName(inputText);
			
			/************** Interpret the result and display that interpreted information **************/
			System.out.println();
			
			// If the resulting text is empty, the method accepted the input
			if (resultText != "") {
				 // If the test case expected the test to pass then this is a failure
				 // This will only run if a test case was written incorrectly
				if (expectedPass) {
					System.out.println("***Failure*** The name <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be valid, so this is a failure!\n");
					System.out.println("Error message: " + resultText);
					numFailed++;
				}
				// If the test case expected the test to fail then this is a success
				else {			
					System.out.println("***Success*** The name <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be invalid, so this is a pass!\n");
					System.out.println("Error message: " + resultText);
					numPassed++;
				}
			}
			
			// If the resulting text is empty, the method accepted the input
			else {	
				// If the test case expected the test to pass then this is a success
				if (expectedPass) {	
					System.out.println("***Success*** The name <" + inputText + 
							"> is valid, so this is a pass!");
					numPassed++;
				}
				// If the test case expected the test to fail then this is a failure
				// This will only run if a test case was written incorrectly
				else {
					System.out.println("***Failure*** The name <" + inputText + 
							"> was judged as valid" + 
							"\nBut it was supposed to be invalid, so this is a failure!");
					numFailed++;
				}
			}
		} else if(testType == "LASTNAME") {
			
			resultText = entityClasses.NameValidation.checkForValidLastName(inputText);
			
			/************** Interpret the result and display that interpreted information **************/
			System.out.println();
			
			// If the resulting text is empty, the method accepted the input
			if (resultText != "") {
				 // If the test case expected the test to pass then this is a failure
				 // This will only run if a test case was written incorrectly
				if (expectedPass) {
					System.out.println("***Failure*** The last name <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be valid, so this is a failure!\n");
					System.out.println("Error message: " + resultText);
					numFailed++;
				}
				// If the test case expected the test to fail then this is a success
				else {			
					System.out.println("***Success*** The last name <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be invalid, so this is a pass!\n");
					System.out.println("Error message: " + resultText);
					numPassed++;
				}
			}
			
			// If the resulting text is empty, the method accepted the input
			else {	
				// If the test case expected the test to pass then this is a success
				if (expectedPass) {	
					System.out.println("***Success*** The last name <" + inputText + 
							"> is valid, so this is a pass!");
					numPassed++;
				}
				// If the test case expected the test to fail then this is a failure
				// This will only run if a test case was written incorrectly
				else {
					System.out.println("***Failure*** The last name <" + inputText + 
							"> was judged as valid" + 
							"\nBut it was supposed to be invalid, so this is a failure!");
					numFailed++;
				}
			}
		} else if(testType == "EMAIL") {
			
			resultText = entityClasses.EmailValidation.checkForValidEmail(inputText);
			
			/************** Interpret the result and display that interpreted information **************/
			System.out.println();
			
			// If the resulting text is empty, the method accepted the input
			if (resultText != "") {
				 // If the test case expected the test to pass then this is a failure
				 // This will only run if a test case was written incorrectly
				if (expectedPass) {
					System.out.println("***Failure*** The email <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be valid, so this is a failure!\n");
					System.out.println("Error message: " + resultText);
					numFailed++;
				}
				// If the test case expected the test to fail then this is a success
				else {			
					System.out.println("***Success*** The email <" + inputText + "> is invalid." + 
							"\nBut it was supposed to be invalid, so this is a pass!\n");
					System.out.println("Error message: " + resultText);
					numPassed++;
				}
			}
			
			// If the resulting text is empty, the method accepted the input
			else {	
				// If the test case expected the test to pass then this is a success
				if (expectedPass) {	
					System.out.println("***Success*** The email <" + inputText + 
							"> is valid, so this is a pass!");
					numPassed++;
				}
				// If the test case expected the test to fail then this is a failure
				// This will only run if a test case was written incorrectly
				else {
					System.out.println("***Failure*** The email <" + inputText + 
							"> was judged as valid" + 
							"\nBut it was supposed to be invalid, so this is a failure!");
					numFailed++;
				}
			}
		}
	}
}
