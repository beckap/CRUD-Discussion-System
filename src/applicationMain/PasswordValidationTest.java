package applicationMain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entityClasses.PasswordValidation;

class PasswordValidationTest {

	@Test
	public void testValidPassword() {
		String testResult = PasswordValidation.checkForValidPassword("Password1!");
		assertEquals("", testResult);
	}
	
	@Test
	public void testPasswordTooShort() {
		String testResult = PasswordValidation.checkForValidPassword("Pw1!");
		assertEquals("Must be at least 8 characters long", testResult);
	}
	
	@Test
	public void testPasswordTooLong() {
		String testResult = PasswordValidation.checkForValidPassword("PWRdn[CPZ0},MGx^UE@tkgt]Ry2d[c]GqVtkL1aN^7;l*N?pR<=YnaG}8CeHeIE2}\nYouJustLostTheGame");
		assertEquals("Must be at most 64 characters", testResult);
	}
	
	@Test
	public void testPasswordHasNoDigit() {
		String testResult = PasswordValidation.checkForValidPassword("Password!");
		assertEquals("The password must have at least one digit.", testResult);
	}
	
	@Test
	public void testPasswordHasNoUppercaseLetter() {
		String testResult = PasswordValidation.checkForValidPassword("password1!");
		assertEquals("The password must have at least one Uppercase letter.", testResult);
	}
	
	@Test
	public void testPasswordHasNoLowercaseLetter() {
		String testResult = PasswordValidation.checkForValidPassword("PASSWORD1!");
		assertEquals("The password must have at least one Lowercase letter.", testResult);
	}
	
	@Test
	public void testPasswordHasNoSpecialCharacter() {
		String testResult = PasswordValidation.checkForValidPassword("Password123");
		assertEquals("The password must have at least one special character.", testResult);
	}
	
	@Test
	public void testBoundary8Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdEf1!");
		assertEquals("", testResult);
	}

	@Test
	public void testBoundary7Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdE1!");
		assertEquals("Must be at least 8 characters long", testResult);
	}
	
	@Test
	public void testBoundary9Characters() {
		String testResult = PasswordValidation.checkForValidPassword("AbCdEf12!");
		assertEquals("", testResult);
	}
	
	@Test
	void testBoundary63Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abcdefghijklmnopbqrstuvwxyzbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb1!");
	    assertEquals("", testResult);
	}
	
	@Test
	void testBoundary64Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abcdefghijklmnopbqrstuvwxyzbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbc1!");
	    assertEquals("", testResult);
	}
	
	@Test
	void testBoundary65Chars() {
	    String testResult = PasswordValidation.checkForValidPassword("Abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb1!");
	    assertEquals("Must be at most 64 characters", testResult);

	}
	
}
