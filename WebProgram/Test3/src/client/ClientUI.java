package client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientUI extends JFrame{
	public JList userList;
	public JTextField usernameField;
	public JTextField serverIpField;
	public JTextField serverPortField;
	public JTextField clientPortField;
	public JTextField messageField;
	public JTextArea showArea;
	public JButton connectButton;
	public JButton sendButton;
	public MyClient myClient;
	
	public ClientUI() {
		// TODO Auto-generated constructor stub
		super("Client");
		connectButton = new JButton("Connect");
		sendButton = new JButton("Send");
		usernameField = new JTextField();
		serverIpField = new JTextField("127.0.0.1");
		serverPortField = new JTextField("8868");
		clientPortField = new JTextField();
		messageField = new JTextField();
		showArea = new JTextArea();
		userList = new JList();
	}

}
