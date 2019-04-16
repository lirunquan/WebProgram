package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ClientReceiveFromClient implements Runnable {
	Socket socket;
	DefaultListModel<String> dlm2;
	JList<String> jList2;
	InputStreamReader reader;
	BufferedReader buffer_reader;

	public ClientReceiveFromClient(Socket socket,
			DefaultListModel<String> dlm2, JList<String> jList2) {
		this.socket = socket;
		this.dlm2 = dlm2;
		this.jList2 = jList2;
	}

	public void run() {
		try {
			reader = new InputStreamReader(socket.getInputStream());
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		buffer_reader = new BufferedReader(reader);
		try {
			String request = buffer_reader.readLine();
			dlm2.addElement(request);
			jList2.setModel(dlm2);
			socket.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
