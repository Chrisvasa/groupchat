package TCP.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class GUI extends JFrame {
	private String userName;
	private JTextArea chatArea;
	private JTextField messageField;
	private JButton sendButton;
	private JButton logoutButton;
	DefaultListModel<String> model = new DefaultListModel<>();
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
		userList = new JList<>(model);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		userList.addListSelectionListener(new ListSelectionListener() {
//
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//
//			}
//		});
		JScrollPane userScrollPane = new JScrollPane(userList);
		userScrollPane.setPreferredSize(new Dimension(100, 0));
		userPanel.add(userList, BorderLayout.CENTER);
		userPanel.add(userScrollPane, BorderLayout.EAST);
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

	private void closeWindow() {
		SwingUtilities.invokeLater(() -> {
			dispose();
			System.exit(0);
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

	public void addUser(String userName) {
		model.addElement(userName);
	}

	public void removeUser(String userName) {
		SwingUtilities.invokeLater(() -> {
			model.removeElement(userName);
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

	public String getUserName() {
		return userName;
	}
}
