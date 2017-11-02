package test;

import model.Permission;
import model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
	@Test
	public void checkCorrectPassword() throws Exception {
		User user = new User("name", "username", "password", Permission.NORMAL);
		assertTrue(user.checkPassword("password"));
	}

	@Test
	public void checkWrongPassword() throws Exception {
		User user = new User("name", "username", "password", Permission.NORMAL);
		assertFalse(user.checkPassword("wrong"));
	}
}