package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entityClasses.PasswordValidation;

/**
 * <p><b>Class:</b> PasswordValidationTest
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 * 	Contains JUnit test cases for the PasswordValidation class.
 * 	These tests verify that password validation rules are enforced
 * 	correctly and that the expected error messages are returned for
 * 	invalid passwords.
 * </p>
 * 
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>JUnit tests are implemented using the \@Test method decorator.</li>
 *   <li>Assertions are used to verify expected validation results and error messages.</li>
 *   <li>Boundary and invalid-input cases are included to test password requirements.</li>
 * </ul>
 * 
 * @author Genesee Harmon
 * @version 1.0 Created with documentation
 */

class PasswordValidationTest {

	/**
	 * Test #1: Verifies that a valid password returns no error message.
	 */
	@Test
	public void testValidPassword() {
		String testResult = PasswordValidation.checkForValidPassword("Password1!");
		assertEquals("", testResult);
	}
	
	/**
	 * Test #2: Verifies that an invalid password with less than 8 characters
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordTooShort() {
		String testResult = PasswordValidation.checkForValidPassword("Pw1!");
		assertEquals("Must be at least 8 characters long", testResult);
	}
	
	/**
	 * Test #3: Verifies that an invalid password with more than 64 characters
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordTooLong() {
		String testResult = PasswordValidation.checkForValidPassword("PWRdn[CPZ0},MGx^UE@tkgt]Ry2d[c]GqVtkL1aN^7;l*N?pR<=YnaG}8CeHeIE2}\nYouJustLostTheGame");
		assertEquals("Must be at most 64 characters", testResult);
	}
	
	/**
	 * Test #4: Verifies that an invalid password with no digit character
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordHasNoDigit() {
		String testResult = PasswordValidation.checkForValidPassword("Password!");
		assertEquals("The password must have at least one digit.", testResult);
	}
	
	/**
	 * Test #5: Verifies that an invalid password with no uppercase letter
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordHasNoUppercaseLetter() {
		String testResult = PasswordValidation.checkForValidPassword("password1!");
		assertEquals("The password must have at least one Uppercase letter.", testResult);
	}
	
	/**
	 * Test #6: Verifies that an invalid password with no lowercase letter
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordHasNoLowercaseLetter() {
		String testResult = PasswordValidation.checkForValidPassword("PASSWORD1!");
		assertEquals("The password must have at least one Lowercase letter.", testResult);
	}
	
	/**
	 * Test #7: Verifies that an invalid password with no special character
	 * returns the correct error message.
	 */
	@Test
	public void testPasswordHasNoSpecialCharacter() {
		String testResult = PasswordValidation.checkForValidPassword("Password123");
		assertEquals("The password must have at least one special character.", testResult);
	}
	
	/**
	 * Test #8: Verifies that a valid password with exactly 8 characters 
	 * (minimum valid length) returns no error message.
	 */
	@Test
	public void testBoundary8Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdEf1!");
		assertEquals("", testResult);
	}

	/**
	 * Test #9: Verifies that a password with 7 characters (below minimum length)
	 * is invalid and returns the correct error message.
	 */
	@Test
	public void testBoundary7Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdE1!");
		assertEquals("Must be at least 8 characters long", testResult);
	}
	
	/**
	 * Test #10: Verifies that a valid password with 9 characters 
	 * (above minimum length) returns no error message.
	 */
	@Test
	public void testBoundary9Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdEf12!");
		assertEquals("", testResult);
	}
	
	/**
	 * Test #11: Verifies that a valid password with 63 characters 
	 * (one below maximum length) returns no error message.
	 */
	@Test
	void testBoundary63Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abcdefghijklmnopbqrstuvwxyzbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb1!");
	    assertEquals("", testResult);
	}
	
	/**
	 * Test #12: Verifies that a valid password with exactly 64 characters 
	 * (maximum allowed length) returns no error message.
	 */
	@Test
	void testBoundary64Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abcdefghijklmnopbqrstuvwxyzbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbc1!");
	    assertEquals("", testResult);
	}
	
	/**
	 * Test #13: Verifies that an invalid password with 65 characters 
	 * (above maximum length) returns the correct error message.
	 */
	@Test
	void testBoundary65Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb1!");
	    assertEquals("Must be at most 64 characters", testResult);

	}
	
}
