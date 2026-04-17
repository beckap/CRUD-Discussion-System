package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entityClasses.User;

/**
 * <p><b>Class:</b> UserTest</p>
 *
 * <p><b>Responsibilities:</b></p>
 * <p>
 * Contains JUnit test cases for the User class.
 * These tests verify the initialization of User objects,
 * functionality of setter and getter methods, and
 * accurate tracking of user roles.
 * </p>
 *
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>JUnit tests are implemented using the \@Test method decorator.</li>
 *   <li>Assertions are used to verify expected field values, role counts, and error messages.</li>
 *   <li>Test cases include constructor behavior, setter methods, and boundary conditions for role assignments.</li>
 * </ul>
 *
 * @author Genesee Harmon
 * @version 1.0
 */

class UserTest {

	/**
	 * Test #1: Verifies that the parameterized User constructor
	 * correctly initializes all fields and roles.
	 */
	@Test
	void testUserConstructor() {
		User user = new User("user1", "pass", "Becka", "", "Guerrero", "Becka Perez", "hello@email.com", true, true, false);
		
		assertEquals("user1", user.getUserName());
		assertEquals("Becka", user.getFirstName());
		assertEquals("Guerrero", user.getLastName());
		assertEquals("Becka Perez", user.getPreferredFirstName());
		assertEquals("hello@email.com", user.getEmailAddress());
		assertTrue(user.getAdminRole());
		assertTrue(user.getNewStaffRole());
		assertFalse(user.getNewStudentRole());
	}
	
	/**
	 * Test #2: Verifies that setter methods correctly update User fields
	 * and that the updated values are returned by the corresponding getters.
	 */
	@Test
	void testUserSetters() {
		User user = new User();
		
		user.setUserName("user1");
		user.setFirstName("Becka");
		user.setLastName("Guerrero");
		user.setEmailAddress("hi123@email.com");
		user.setAdminRole(true);
		
		
		assertEquals("user1", user.getUserName());
		assertEquals("Becka", user.getFirstName());
		assertEquals("Guerrero", user.getLastName());
		assertEquals("hi123@email.com", user.getEmailAddress());
		assertTrue(user.getAdminRole());
	}
	
	/**
	 * Test #3: Verifies that a User initialized with empty input values
	 * stores empty strings correctly and has no assigned roles.
	 */
	@Test
	void testEmptyUserInput() {
	    User user = new User("", "", "", "", "", "", "", false, false, false);

	    assertEquals("", user.getUserName());
	    assertEquals("", user.getFirstName());
	    assertEquals("", user.getEmailAddress());
	    assertEquals(0, user.getNumRoles());
	}
	
	/**
	 * Test #4: Verifies that a User with no roles assigned
	 * returns a role count of zero.
	 */
	@Test
	void testBoundaryNoUserRoles() {
		User user = new User();
		assertEquals(0, user.getNumRoles());
	}
	
	/**
	 * Test #5: Verifies that a User with one role assigned
	 * returns the correct role count.
	 */
	@Test
	void testBoundaryOneUserRole() {
		User user = new User();
		user.setAdminRole(true);
		assertEquals(1, user.getNumRoles());
	}
	
	/**
	 * Test #6: Verifies that a User with two roles assigned
	 * returns the correct role count.
	 */
	@Test
	void testBoundaryTwoUserRoles() {
		User user = new User();
		user.setAdminRole(true);
		user.setStaffRoleUser(true);
		assertEquals(2, user.getNumRoles());
	}
	
	/**
	 * Test #7: Verifies that a User with three roles assigned
	 * returns the correct role count.
	 */
	@Test
	void testBoundaryThreeUserRoles() {
		User user = new User();
		user.setAdminRole(true);
		user.setStaffRoleUser(true);
		user.setStudentRoleUser(true);
		assertEquals(3, user.getNumRoles());
	}

}
