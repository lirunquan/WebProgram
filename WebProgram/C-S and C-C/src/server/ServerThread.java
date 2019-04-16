package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ServerThread implements Runnable {
	static ServerSocket server ;
	DefaultListModel<String> dlm1;
	DefaultListModel<String> dlm2;
	JList<String> jList1;
	JList<String> jList2;
	static ArrayList<Socket> socket = new ArrayList<Socket>(); // 创建一个Socket列表存储socket

	public ServerThread(DefaultListModel<String> dlm1,
			DefaultListModel<String> dlm2, JList<String> jList1,
			JList<String> jList2) {
		this.dlm1 = dlm1;
		this.dlm2 = dlm2;
		this.jList1 = (JList<String>) jList1;
		this.jList2 = (JList<String>) jList2;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(8888);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			Socket socketTemp = null;
			int localBind=0;
			try {
				socketTemp = server.accept();
				InputStreamReader reader = new InputStreamReader(socketTemp.getInputStream());
				BufferedReader buffer_reader = new BufferedReader(reader);
				String request = buffer_reader.readLine();
				localBind = Integer.valueOf(request.substring(request
						.lastIndexOf(":") + 1));
				socket.add(socketTemp);
				dlm1.addElement(request+":"+socketTemp.getPort());
				jList1.setModel(dlm1);
				//每增加一个客户，都把最新的客户列表发送给所有客户
				System.out.println("客户列表大小："+dlm1.getSize());
				System.out.println("socket列表大小："+socket.size());
				for (int j = 0; j < socket.size(); j++) {
					PrintWriter writer = new PrintWriter(socket.get(j)
							.getOutputStream());
					try {
				    	//暂停5毫秒后程序继续执行,靠后发送的socket暂停时间久点
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i=0;i<dlm1.getSize();i++){
						String message=dlm1.elementAt(i);
						writer.println(message);
						System.out.println(socket.get(j).toString()+"  "+message);
						writer.flush();
					    try {
					    	//暂停8毫秒后程序继续执行,靠后发送的socket暂停时间久点
							Thread.sleep(8);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ServerReceive serverReceive = new ServerReceive(socketTemp, dlm1,
					dlm2, jList1, jList2,localBind);
			Thread thread = new Thread(serverReceive);
			thread.start();
		}
	}

}
