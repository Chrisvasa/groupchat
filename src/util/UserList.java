package util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	private ArrayList<User> users = new ArrayList<User>();

	public ArrayList<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void removeUser(User user) {
		users.remove(user);
	}
}
