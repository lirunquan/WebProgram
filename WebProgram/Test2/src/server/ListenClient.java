package server;

import server.ServerUI;
import sun.misc.Cleaner;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ListenClient extends Thread{
	ServerUI ui;
	Socket clientSocket;
	
	int num;
	public ListenClient(ServerUI ui, Socket client, int clientNum) {
		// TODO Auto-generated constructor stub
		this.ui = ui;
		this.clientSocket = client;
		this.num = clientNum;
		this.start();
	}
	
	public void run() {
		try {//server监听客户端并从客户端接收字符串
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter prWriter = new PrintWriter(clientSocket.getOutputStream());
			String clientInfo = "<" + clientSocket.getInetAddress().toString()+":"+clientSocket.getPort()+">";
			String string = "";
			while(true) {
				try {
					string = bfReader.readLine();
					print("Client "+num+" sent: "+string);
					System.out.println(clientInfo+string);
				} catch (Exception e) {
					// TODO: handle exception
					print("Client "+num+" disconnected. ");
					ui.clientListModel.remove(num);
					ui.list.setModel(ui.clientListModel);
					break;
				}
			}
			bfReader.close();
			clientSocket.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void print(String s) {
		if(s!=null) {
			this.ui.tShow.append(s+"\n");
			System.out.println(s);
		}
	}
}
