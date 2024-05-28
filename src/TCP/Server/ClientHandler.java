package TCP.Server;

import util.Message;
import util.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	/*
	Holds all clients in a static list, which allows us to message all the different clients that are connected
	to the server.
	 */
	public static final ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	public static final ArrayList<User> users = new ArrayList<User>();
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private User user;

	public ClientHandler(Socket socket) {
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());
			this.socket = socket;
			clients.add(this);
		} catch (Exception e) {
			close();
		}
	}

	@Override
	public void run() {
		while(socket.isConnected()) {
			try {
				listenForMessages();
			} catch (Exception e) {
				close();
				break;
			}
		}
	}

	private void listenForMessages() throws IOException, ClassNotFoundException {
		Object receivedObject;

		while ((receivedObject = (Object) input.readObject()) != null) {
			processIncomingData(receivedObject);
		}
	}

	private void processIncomingData(Object receivedObject) {
		switch (receivedObject) {
			case User newUser -> registerNewClient(newUser);
			case Message message -> processIncomingMessage(message);
			case String message -> {
				Message newMessage = new Message("Unknown Sender", message);
				sendMessageToClients(newMessage);
			}
			case null, default -> System.out.println("Unknown message type received.");
		}
	}

	private void processIncomingMessage(Message message) {
		System.out.println("Message from " + message.getSender() + ": " + message.getMessage());
		message.setMessage(message.getSender() + ": " + message.getMessage());
		sendMessageToClients(message);
	}

	private void registerNewClient(User newUser) {
		this.user = newUser;
		users.add(newUser);
		Message joinMessage = new Message(
				user.getUsername(),
				"SERVER: " + user.getUsername() + " joined the server.");
		sendMessageToClients(joinMessage);
		sendMessageOnJoin();
		sendListToClients();
	}

	private void sendMessageOnJoin() {
		try {
			Message joinMessage = new Message("SERVER: ", "You have connected to the server.");
			this.output.writeObject(joinMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendListToClients() {
			for(ClientHandler client : clients) {
				try {
					if(users.size() > 1) {
						users.addFirst(new User());
					}
					for(var user : users) {
						client.output.writeObject(user);
						client.output.flush();
					}
				} catch (IOException e) {
					close();
					throw new RuntimeException(e);
				}
			}
	}

	private void sendMessageToClients(Message message) {
		for(ClientHandler client : clients) {
			String currentUsername = client.user.getUsername();
			if(!currentUsername.equals(message.getSender())) {
				try {
					client.output.writeObject(message);
					client.output.flush();
				} catch (IOException e) {
					close();
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void close() {
		System.out.println("Client has disconnected");
		if (user != null) {
			Message leaveMessage = new Message(user.getUsername(),
					"SERVER: " + user.getUsername() + " left the server.");
			sendMessageToClients(leaveMessage);
			users.remove(user);
		}
		clients.remove(this);
		sendListToClients();

		try {
			if (socket != null) socket.close();
			if (output != null) output.close();
			if (input != null) input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
