package UDP;

import java.io.IOException;
import java.net.*;
import java.util.List;

// This class sets up the "connection" and handles with sending and receiving messages from other clients
public class NetworkManager {
	private static final String IP = "239.1.2.3";
	private static final int PORT = 33344;
	private InetAddress inetAddress;
	private InetSocketAddress group;
	private MulticastSocket socket;
	private NetworkInterface netIf;
	private Thread listener;
	private final ClientGUI clientGUI;

	public NetworkManager(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
		initializeConnection();
	}

	private void initializeConnection() {
		try {
			inetAddress = InetAddress.getByName(IP);
			group = new InetSocketAddress(inetAddress, PORT);
			netIf = NetworkInterface.getByName("wlp3s0");
			socket = new MulticastSocket(PORT);
			socket.joinGroup(group, netIf);

			listenForMessages();
			broadcastJoin(clientGUI.getUserName());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendMessage(String message) {
		try {
			if(!socket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), inetAddress, PORT);
				socket.send(packet);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to send message.", e);
		}
	}

	// Creates a thread that will listen to incoming messages from others
	public void listenForMessages() {
		listener = new Thread(() -> {
			byte[] receiveData = new byte[1024];

			while (!Thread.interrupted()) {
				try {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(receivePacket);
					String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
					handleMessage(message);
				} catch (SocketException e) {
					if (socket.isClosed()) {
						System.out.println("Socket was closed successfully.");
					};
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		});
		listener.start();
	}

	private void handleMessage(String message) {
		// Gets the content part of the message (Username or list of users)
		String content = message.substring(message.indexOf(':') + 1);
		String command = message.split(":")[0];

		switch (command) {
			case "USER_JOINED" -> {
				clientGUI.addUser(content);
				broadcastUserList(clientGUI.getUserList());
			}
			case "USER_LEFT" -> clientGUI.removeUser(content);
			case "USER_LIST" -> clientGUI.updateUserList(content);
			default -> clientGUI.addMessage(message);
		}
	}


	public void broadcastJoin(String userName) {
		sendMessage("USER_JOINED:" + userName);
	}

	public void broadcastLeft(String userName) {
		sendMessage("USER_LEFT:" + userName);
	}

	public void broadcastUserList(List<String> users) {
		sendMessage("USER_LIST:" + String.join(",", users));
	}

	// Broadcasts to other users that current user is disconnecting
	// and then attempting to close the listener thread and socket afterward
	public void closeConnection() {
		broadcastLeft(clientGUI.getUserName());
		try {
			if (listener != null && listener.isAlive()) {
				listener.interrupt();
			}
			if (socket != null && !socket.isClosed()) {
				socket.leaveGroup(group, netIf);
				socket.close();
			}
		} catch (IOException e ) {
			e.printStackTrace();
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
	}
}