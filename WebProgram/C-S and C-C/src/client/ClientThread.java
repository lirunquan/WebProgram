package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ClientThread implements Runnable {
	ServerSocket server;
	DefaultListModel<String> dlm1;
	DefaultListModel<String> dlm2;
	JList<String> jList1;
	JList<String> jList2;
	int localBind;

	// static ArrayList<Socket> socket = new ArrayList<Socket>(); //
	// 创建一个Socket列表存储socket

	public ClientThread(DefaultListModel<String> dlm1,
			DefaultListModel<String> dlm2, JList<String> jList1,
			JList<String> jList2, int localBind) {
		this.dlm1 = dlm1;
		this.dlm2 = dlm2;
		this.jList1 = (JList<String>) jList1;
		this.jList2 = (JList<String>) jList2;
		this.localBind = localBind;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(localBind);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			Socket socketTemp = null;
			try {
				socketTemp = server.accept();
				// String client = socketTemp.getInetAddress().toString() + ":"
				// + socketTemp.getPort();
				// socket.add(socketTemp);
				// dlm1.addElement(client);
				// jList1.setModel(dlm1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ClientReceiveFromClient serverReceive = new ClientReceiveFromClient(
					socketTemp, dlm2, jList2);
			Thread thread = new Thread(serverReceive);
			thread.start();
		}
	}

}
