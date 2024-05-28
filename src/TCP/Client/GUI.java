package TCP.Client;

import util.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GUI extends JFrame {
	private String userName;
	private JTextArea chatArea;
	private JTextField messageField;
	private JButton sendButton;
	private JButton logoutButton;
	DefaultListModel<String> listModel = new DefaultListModel<>();
	JList<String> userList;
	Client client;

	public GUI(Client client) {
		do {
			userName = JOptionPane.showInputDialog("Enter your user name");
		} while (userName == null);
		this.client = client;
		initializeGUI();
	}

	private void initializeGUI() {
		setTitle("Chat [" + userName + "]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setSize(600, 400);

		// Sets up the panel that handles the chat message history
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane chatScrollPane = new JScrollPane(chatArea);
		chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatPanel.add(chatScrollPane, BorderLayout.CENTER);

		// Sets up the logout button on top of the chat area.
		JPanel logoutPanel = new JPanel();
		logoutPanel.setLayout(new BorderLayout());
		logoutButton = new JButton("Logout");
		logoutPanel.add(logoutButton, BorderLayout.CENTER);

		// Sets up the message field panel which takes user input
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		messageField = new JTextField(15);
		sendButton = new JButton("Send");
		messagePanel.add(messageField, BorderLayout.CENTER);
		messagePanel.add(sendButton, BorderLayout.EAST);

		// Sets up the user panel that handles the user list of connected users
		JPanel userPanel = new JPanel();
		JLabel userLabel = new JLabel("Users online:");
		userPanel.setLayout(new BorderLayout());
		userList = new JList<>(listModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane userScrollPane = new JScrollPane(userList);
		userScrollPane.setPreferredSize(new Dimension(100, 0));
		userPanel.add(userScrollPane, BorderLayout.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);

		add(logoutPanel, BorderLayout.NORTH);
		add(chatPanel, BorderLayout.CENTER);
		add(messagePanel, BorderLayout.SOUTH);
		add(userPanel, BorderLayout.EAST);

		// Sets up all the necessary actionListeners
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});

		messageField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		setVisible(true);
	}

	public void sendMessage() {
		SwingUtilities.invokeLater(() -> {
			String message = messageField.getText();
			client.sendMessage(message);
			addMessage("Me: " + message);
			clearMessageField();
		});
	}

	public void addMessage(String message) {
		SwingUtilities.invokeLater(() -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String newMessage = "[" + LocalDateTime.now().format(formatter) + "] " + message;
			chatArea.append(newMessage + '\n');
		});
	}

	public void clearMessageField() {
		SwingUtilities.invokeLater(() -> {
			messageField.setText("");
		});
	}

	public void updateUserList(ArrayList<User> users) {
		SwingUtilities.invokeLater(() -> {
			listModel.removeAllElements();
			for(User user : users) {
				listModel.addElement(user.toString());
			}
		});
	}

	public void addUser(User user) {
		SwingUtilities.invokeLater(() -> {
			if (user.getUsername().isEmpty()) {
				listModel.clear();
			} else {
				listModel.addElement(user.getUsername());
			}
		});
	}

	public String getUserName() {
		return userName;
	}

	private void closeWindow() {
		SwingUtilities.invokeLater(() -> {
			dispose();
			System.exit(0);
		});
	}
}
