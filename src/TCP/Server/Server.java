package TCP.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int PORT = 1337;
	//	private static final String IP = "127.0.0.1";
	ServerSocket serverSocket;

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void start() {
		System.out.println("-- SERVER STARTED --");
		System.out.println("Listening on port: " + PORT);
		try {
			while (!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				System.out.println("Client connected");
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
		} catch (IOException e) {
			stop();
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		System.out.println("-- SERVER SHUTDOWN --");
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			System.out.println("Something went wrong :D");
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		Server server = new Server(serverSocket);
		server.start();
	}
}
