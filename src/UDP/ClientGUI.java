package UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientGUI extends JFrame {
	private final String userName;
	private JTextArea chatArea;
	DefaultListModel<String> model = new DefaultListModel<>();
	private JList<String> userList;
	private JTextField messageField;
	private JButton sendButton;
	private JButton logoutButton;
	NetworkManager networkManager;

	public ClientGUI() {
		userName = JOptionPane.showInputDialog(null, "Enter your name");
		networkManager = new NetworkManager(this);
		initializeGUI();
	}

	private void initializeGUI() {
		setTitle("Chat " + userName);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Chat area
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		logoutButton = new JButton("Logout");
		logoutButton.addActionListener(e -> closeWindow());
		add(scrollPane, BorderLayout.CENTER);
		add(logoutButton, BorderLayout.NORTH);

		// User list
		userList = new JList<>(model);
		JScrollPane userListScrollPane = new JScrollPane(userList);
		userListScrollPane.setPreferredSize(new Dimension(100, 0));
		add(userListScrollPane, BorderLayout.EAST);

		// Bottom panel - INPUT / SEND button
		JPanel inputPanel = new JPanel(new BorderLayout());
		messageField = new JTextField();
		messageField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				networkManager.sendMessage(userName + ":" + messageField.getText());
				clearMessageField();
			}
		});
		sendButton = new JButton("Send");
		sendButton.addActionListener(e -> {
			networkManager.sendMessage(userName + ":" + messageField.getText());
			clearMessageField();
		});

		inputPanel.add(messageField, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);
		add(inputPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
	}

	private void closeWindow() {
		networkManager.closeConnection();
		dispose();
		System.exit(0);
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getUserList() {
		List<String> users = new ArrayList<>();
		for (int i = 0; i < model.getSize(); i++) {
			users.add(model.getElementAt(i));
		}
		return users;
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

	public void updateUserList(String users) {
		List<String> newUsers = Arrays.asList(users.split(","));
		SwingUtilities.invokeLater(() -> {
			if (newUsers.size() >= model.getSize()) {
				model.clear();
				newUsers.forEach(model::addElement);
			}
		});
	}

	public void addUser(String userName) {
		model.addElement(userName);
	}

	public void removeUser(String userName) {
		SwingUtilities.invokeLater(() -> {
			addMessage("DISCONNECTED: " + userName);
			model.removeElement(userName);
		});
	}
}