package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClient extends Thread{
	ClientUI ui;
	Socket clientSocket;
	BufferedReader bfReader;
	PrintWriter printWriter;
	String host;
	int port;
	
	public MyClient(ClientUI ui) {
		// TODO Auto-generated constructor stub
		this.ui = ui;
		this.host = ui.ipField.getText();
		this.port = Integer.parseInt(ui.portField.getText());
		try {
			clientSocket = new Socket(host, port);
			ui.btnSend.setEnabled(true);
			print("Connected to Server success, IP: "+host+" port: "+port);
			bfReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			printWriter = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO: handle exception
			print("Connect failed. IP: "+host+" port: "+port);
			print(e.toString());
			e.printStackTrace();
		}
		this.start();
	}
	public void run() {
		String msg = "";
		while(true) {
			try {
				msg = bfReader.readLine();
			} catch (IOException e) {
				// TODO: handle exception
				print("Disconnected.");
				break;
			}
			if(msg!=null && msg.trim()!="") {
				print(">> "+msg);
			}
		}
	}
	public void sendMsg(String msg) {
		try {
			printWriter.println(msg);
			printWriter.flush();
		} catch (Exception e) {
			// TODO: handle exception
			print(e.toString());
		}
	}
	public void print(String msg) {
		if(msg!=null) {
			this.ui.tShow.append(msg+"\n");
			System.out.println(msg+"\n");
		}
	}
	public void closeClient() {
		try {
			if(clientSocket!=null) {
				clientSocket.close();
			}
			if(bfReader!=null) {
				bfReader.close();
			}
			if(printWriter!=null) {
				printWriter.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
