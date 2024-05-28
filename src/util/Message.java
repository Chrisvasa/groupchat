package util;

import java.io.Serializable;

public class Message implements Serializable {
	private final String sender;
	private String message;

	public Message(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
