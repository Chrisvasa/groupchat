package UDP;

import javax.swing.*;

public class Client {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ClientGUI client = new ClientGUI();
			client.setVisible(true);
		});
	}
}