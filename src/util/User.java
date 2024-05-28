package util;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;

	public User() {}

	public User(String username) {
		this.username = username;
	}

	public String getUsername() {
		if(username == null) {
			username = "";
		}
		return username;
	}
	public void setUsername(String username) {
		if(!username.isEmpty()) {
			this.username = username;
		}
		else {
			throw new IllegalArgumentException("Username cannot be empty");
		}
	}
}
