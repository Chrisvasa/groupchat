package TCP.Server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
	public ServerGUI() {
		initializeGUI();
	}

	public void initializeGUI() {
		setTitle("SERVER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setSize(400, 600);
	}
}
