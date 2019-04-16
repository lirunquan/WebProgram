package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class ClientReceiveServer implements Runnable {
	
	Socket socket;
	DefaultListModel<String> dlm1;
	DefaultListModel<String> dlm2;
	JList<String> jList1;
	JList<String> jList2;
	InputStreamReader reader;
	BufferedReader buffer_reader;

	public ClientReceiveServer(Socket socket, DefaultListModel<String> dlm1,
			DefaultListModel<String> dlm2, JList<String> jList1,
			JList<String> jList2) {
		this.socket = socket;
		this.dlm1 = dlm1;
		this.dlm2 = dlm2;
		this.jList1 = jList1;
		this.jList2 = jList2;
	}

	@Override
	public void run() {
		while (true) {
			if(socket.isClosed()){
				break;
			}
			boolean flag=false;
			try {
				reader = new InputStreamReader(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer_reader = new BufferedReader(reader);
			String server_say = null;
			// 当服务器断开时，此处应能监听到，并做出反应
			try {
				server_say = buffer_reader.readLine();
				//出来服务器正常断开连接
				if(server_say==null){
					flag=true;
					socket.close();
					ClientSwing.ipAddress.setText("");
					ClientSwing.port.setText("");
					JOptionPane.showMessageDialog(ClientSwing.f, "  服务器正常断开连接",
							"提  示", JOptionPane.INFORMATION_MESSAGE);
				}
				//System.out.println("socket端口"+socket.getLocalPort()+" say:"+server_say);
				if(server_say.startsWith("127.0.0.1")){
					System.out.println(server_say);
					if(dlm1.getSize()==0){
						dlm1.addElement(server_say);
					}else{
						boolean flag2=false;
						for(int i=0,len=dlm1.getSize();i<len;i++){
							if(dlm1.elementAt(i).equals(server_say)){
								flag2=true;
							}
						}
						if(!flag2){
							dlm1.addElement(server_say);
						}
					}
					flag=true;
					jList1.setModel(dlm1);
				}
				if(server_say.equals("clear")){
					dlm1.clear();
					jList1.setModel(dlm1);
					flag=true;
				}
			} catch (SocketException e) {
				if(!socket.isClosed()){
					JOptionPane.showMessageDialog(ClientSwing.f, "  服务器异常断开连接",
							"提  示", JOptionPane.INFORMATION_MESSAGE);// 消息对话框
					System.exit(0);
				}
				flag=true;
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!flag){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
				dlm2.addElement(df.format(new Date()) + "   服务器回复: " + (String)server_say);
				jList2.setModel(dlm2);
			}
		}
	}

}
