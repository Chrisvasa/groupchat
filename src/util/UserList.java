package util;

import java.io.Serializable;
import java.util.List;

public class UserList implements Serializable {
	private List<User> users;

	public UserList(List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void removeUser(User user) {
		users.remove(user);
	}
}
