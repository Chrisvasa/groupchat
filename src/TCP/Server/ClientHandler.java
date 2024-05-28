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
	public static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String userName;

	public ClientHandler(Socket socket) {
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());
			this.socket = socket;
			synchronized (clients) {
				clients.add(this);
			}
		} catch (Exception e) {
			close();
			System.out.println("Client disconnected");
		}
	}

	@Override
	public void run() {
		while(socket.isConnected()) {
			try {
				listenForMessages();
			} catch (Exception e) {
				System.out.println("Client disconnected");
				close();
				break;
			}
		}
	}

	private void listenForMessages() throws IOException, ClassNotFoundException {
		Object obj;

		while ((obj = (Object) input.readObject()) != null) {
			if(obj instanceof User u) {
				this.userName = u.getUsername();
				Message joinMessage = new Message(userName, "SERVER: " + userName + " joined the server.");
				sendMessageToClients(joinMessage);
			}
			else if(obj instanceof Message message) {
				System.out.println("Message from " + message.getSender() + ": " + message.getMessage());
				message.setMessage(message.getSender() + ": " + message.getMessage());
				sendMessageToClients(message);
			}
			else {
				System.out.println("Unknown message type received.");
			}
		}
	}

	private void sendMessageToClients(Message message) {
		for(ClientHandler client : clients) {
			if(!client.userName.equals(message.getSender())) {
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
		Message leaveMessage = new Message(userName, "SERVER: " + userName + " left the server.");
		sendMessageToClients(leaveMessage);
		clients.remove(this);
		try {
			if(socket != null) {
				socket.close();
			}
			if(output != null) {
				output.close();
			}
			if(input != null) {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
