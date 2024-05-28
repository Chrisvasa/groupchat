package TCP.Client;

import util.Message;
import util.User;

import java.io.*;
import java.net.*;

public class Client {
	private Socket socket;
	private InetAddress inetAddress;
	private final int PORT = 1337;
	private String userName;
	private Thread listener;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private User user = new User();
	private GUI gui;

	public Client(InetAddress inetAddress) {
		try {
			this.inetAddress = inetAddress;
			this.socket = new Socket(inetAddress, PORT);
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
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
			listener = new Thread(() -> {
				while (!Thread.interrupted()) {
					try {
						Object obj = ois.readObject();
						if(obj instanceof Message m) {
							gui.addMessage(m.getMessage());
						}
						else {
							System.out.println("MESSAGE WTF");
						}
					} catch (EOFException e) {
						e.printStackTrace();
						close();
						break;
					} catch (IOException | ClassNotFoundException e) {
						close();
						e.printStackTrace();
					}
				}
				close();
			});
			listener.start();
	}

	public void sendMessage(String message) {
		if(message != null) {
			try {
				Message messageToSend = new Message(userName, message);
				oos.writeObject(messageToSend);
				oos.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void sendUserInfo() {
		if(user != null) {
			try {
				oos.writeObject(user);
				oos.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (ois != null) {
				ois.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			Client client = new Client(inetAddress);
			client.listenForMessages();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
