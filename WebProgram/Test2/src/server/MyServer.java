package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MyServer extends Thread{
	ServerUI ui;
	ServerSocket socket;
	BufferedReader bfReader;
	PrintWriter printWriter;
	int port;
	int clientNum;
	int index;
	
	public MyServer(ServerUI ui) {
		// TODO Auto-generated constructor stub
		this.ui = ui;
		this.port = Integer.parseInt(ui.portField.getText());
		this.clientNum = 0;
		this.index = 0;
		this.start();
	}

	public void run() {
		// TODO Auto-generated method stub
		try {//server 启动
			socket = new ServerSocket(port);
			ui.clientSockets = new ArrayList<Socket>();
			print("Start server Success, Port: "+port);
			while(true) {
				print("Waiting for clients ... ");
				Socket client = socket.accept();
				ui.clientListModel.add(index, client);
				ui.list.setModel(ui.clientListModel);
				ui.btnSendAll.setEnabled(true);
				print("Connected, Client " + clientNum + ": " + client.toString());
				new ListenClient(ui, client, clientNum);//启动server监听client的线程
				clientNum++;
				index++;
			}
		} catch (IOException e) {
			// TODO: handle exception
			print("Start server Failed, Port: "+port);
			print(e.toString());
			e.printStackTrace();
		}
	}
	public synchronized void sendMsg(String msg) {
		try {//server向client发送字符串
			for(int i=0;i<ui.clientSockets.size();i++) {
				Socket client = ui.clientSockets.get(i);
				printWriter = new PrintWriter(client.getOutputStream(), true);
				printWriter.println(msg);
				printWriter.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
			print(e.toString());
		}
	}
	public void print(String s) {
		if(s!=null) {
			this.ui.tShow.append(s+"\n");
			System.out.println(s);
		}
	}
	public void closeServer() {
		try {
			if(socket!=null) {
				socket.close();
			}
			if(bfReader!=null) {
				bfReader.close();
			}
			if(printWriter!=null) {
				printWriter.close();
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

