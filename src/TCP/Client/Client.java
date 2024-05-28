package TCP.Client;

import util.Message;
import util.User;

import java.io.*;
import java.net.*;

public class Client {
	private Socket socket;
	//	private static final String IP = "127.0.0.1";
	private final int PORT = 1337;
	private String userName;
	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	private final User user = new User();
	private GUI gui;

	public Client(InetAddress inetAddress) {
		try {
			this.socket = new Socket(inetAddress, PORT);
			this.objOutput = new ObjectOutputStream(socket.getOutputStream());
			this.objInput = new ObjectInputStream(socket.getInputStream());
			this.gui = new GUI(this);
			setUserName(gui.getUserName());
			user.setUsername(this.userName);
			sendUserInfo();

			gui.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("User initialized");
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void listenForMessages() {
		new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Object obj = objInput.readObject();
					switch (obj) {
						case Message message -> gui.addMessage(message.getMessage());
//						case UserList userList -> gui.updateUserList(userList);
						case User newUser -> gui.addUser(newUser);
						case String string -> gui.addMessage(string);
						case null, default -> System.out.println("Unknown Message Type");
					}
				} catch (IOException | ClassNotFoundException e) {
					close();
					e.printStackTrace();
				}
			}
			close();
		}).start();
	}

	public void sendMessage(String message) {
		if (message != null) {
			try {
				Message messageToSend = new Message(userName, message);
				objOutput.writeObject(messageToSend);
				objOutput.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void sendUserInfo() {
		try {
			objOutput.writeObject(user);
			objOutput.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (objOutput != null) {
				objOutput.close();
			}
			if (objInput != null) {
				objInput.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
//			InetAddress inetAddress = InetAddress.getByName(IP);
			Client client = new Client(inetAddress);
			client.listenForMessages();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
