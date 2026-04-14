package applicationMain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entityClasses.User;

class UserTest {

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
	
	@Test
	void testEmptyUserInput() {
	    User user = new User("", "", "", "", "", "", "", false, false, false);

	    assertEquals("", user.getUserName());
	    assertEquals("", user.getFirstName());
	    assertEquals("", user.getEmailAddress());
	    assertEquals(0, user.getNumRoles());
	}
	
	@Test
	void testBoundaryNoUserRoles() {
		User user = new User();
		assertEquals(0, user.getNumRoles());
	}
	
	@Test
	void testBoundaryOneUserRole() {
		User user = new User();
		user.setAdminRole(true);
		assertEquals(1, user.getNumRoles());
	}
	
	@Test
	void testBoundaryTwoUserRoles() {
		User user = new User();
		user.setAdminRole(true);
		user.setStaffRoleUser(true);
		assertEquals(2, user.getNumRoles());
	}
	
	@Test
	void testBoundaryThreeUserRoles() {
		User user = new User();
		user.setAdminRole(true);
		user.setStaffRoleUser(true);
		user.setStudentRoleUser(true);
		assertEquals(3, user.getNumRoles());
	}

}
