package applicationMain;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p><b>Class:</b> CharacterCountAnalyzerTest (JUnit)
 * </p>
 * 
 * <p><b>Responsibilities:</b></p>
 * <p>
 *   Implements unit and integration tests using JUnit,
 *   including full test coverage of the character count 
 *   feature for the Discussion Board System.
 * </p>
 * 
 * <p><b>Character count features tested:</b></p>
 * <ul>
 *   <li>Empty input</li>
 *   <li>Type valid input into a post</li>
 *   <li>Type valid input into a reply to posts</li>
 *   <li>See the character count of a post/reply in real-time</li>
 *   <li>Cannot type a post or reply if user has an invalid role</li>
 * </ul>
 * 
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>JUnit tests implemented using \@Test method decorator.</li>
 *   <li>Reproducible test environment using Mockito.</li>
 *   <li>Object validation implemented using junit.Assert methods</li>
 * </ul>
 * 
 * @author Genesee Harmon
 * @version 1.0 Created with documentation
 */

import org.junit.jupiter.api.Test;

import entityClasses.CharacterCountAnalyzer;

class CharacterCountAnalyzerTest {

	/**
	 * Test #1: Character count sets at 0 for empty input
	 */
	@Test
	void testEmptyInputCount() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("");
		assertEquals(0, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #2: Character count sets correctly for valid input
	 */
	@Test
	void testValidInputCount() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("Hello");
		assertEquals(5, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #3: Character count sets correctly for valid input with spaces
	 */
	@Test
	void testValidInputCountWithSpaces() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("You just lost the game");
		assertEquals(22, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #4: Character count sets correctly for valid input wjth digits
	 */
	@Test
	void testValidInputCountWithDigits() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("Ch1");
		assertEquals(3, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #5: Character count sets correctly for valid input wjth special characters
	 */
	@Test
	void testValidInputCountWithSpecialChars() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("Hello!:)");
		assertEquals(8, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #6: Character count updates accurately for valid input when more is typed
	 */
	@Test
	void testValidInputCountWhenInputIncreases() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("Hello");
		assertEquals(5, analyzer.getCharacterCount());
		
		analyzer.setInput("Hello everyone");
		assertEquals(14, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #7: Character count updates correctly for valid input when input is deleted
	 */
	@Test
	void testValidInputCountWhenInputDecreases() {
		CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
		analyzer.setInput("Hello everyone");
		assertEquals(14, analyzer.getCharacterCount());
		
		analyzer.setInput("Hello");
		assertEquals(5, analyzer.getCharacterCount());
	}
	
	/**
	 * Test #8: User role is valid for "student"
	 */
    @Test
    void testValidStudentRole() {
        CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String result = analyzer.setRole("student");
        assertEquals("Valid role", result);
        assertEquals("student", analyzer.getRole());
    }

	/**
	 * Test #9: User role is valid for "staff"
	 */
    @Test
    void testValidStaffRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String role = analyzer.setRole("staff");
        assertEquals("Valid role", role);
        assertEquals("staff", analyzer.getRole());
    }

	/**
	 * Test #10: User role is valid for "admin"
	 */
    @Test
    void testValidAdminRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String role = analyzer.setRole("admin");
        assertEquals("Valid role", role);
        assertEquals("admin", analyzer.getRole());
    }

	/**
	 * Test #11: User role is invalid for null role
	 */
    @Test
    void testNullRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String role = analyzer.setRole(null);
        assertEquals("Error: Role is invalid", role);
    }

	/**
	 * Test #12: User role is invalid for empty role
	 */
    @Test
    void testEmptyRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String role = analyzer.setRole("");
        assertEquals("Error: Role is invalid", role);
    }

	/**
	 * Test #13: User role is invalid for string other than student, staff, or admin
	 */
    @Test
    void testInvalidRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
        String role = analyzer.setRole("user");
        assertEquals("Error: Role does not exist", role);
    }

	/**
	 * Test #14: Character count updates correctly for user with a valid role
	 */
    @Test
    void testCharacterCountWithValidRole() {
    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
    	analyzer.setRole("student");
    	analyzer.setInput("Hello");
        assertEquals(5, analyzer.getCharacterCount());
    }
    
//	/**
//	 * FUTURE Test #15: Character count does not update if user has no valid role
//	 */
//    @Test
//    void testCharacterCountWithNoRole() {
//    	CharacterCountAnalyzer analyzer = new CharacterCountAnalyzer();
//    	analyzer.setInput("Hello");
//        assertEquals(-1, analyzer.getCharacterCount());
//    }
}
