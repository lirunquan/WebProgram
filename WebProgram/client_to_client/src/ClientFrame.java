

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class ClientFrame extends JFrame{
	private JTextArea ReceivedMessage;
	private JTextArea SendMessage;
	private JList list;
	private DefaultListModel clientItem;
	private Map<String,String> clientmap;
	private String ip;
	private int port;
	private String toServer,toclient;
	private String fromServer;
	private Socket socket,testsocket;
	private PrintWriter writer;
	private BufferedReader buffer_reader;
	private boolean isclosed;
	private String[] pair;
	public static void main(String[] args) {
		ClientFrame clientframe=new ClientFrame();
	}
	
	public ClientFrame() {
		//ip
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress().toString();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        
		//自身固定监听端口
		/*
		 new Socket操作后只是对目标端口进行了监听，
		 并非去使用目标端口；所以如果可以正常创建Socket，
		 则可以证明主机上的目标端口已经被使用（并非此Socket使用的）；
		 反之则证明这个端口并没有程序使用
		 */
		int i;
		for ( i = 8001; i < 9000; i++) {
	         try {
	            System.out.println("查看 "+ i);
	            testsocket = new Socket(ip, i);
	            System.out.println("端口 " + i + " 已被使用");
	         }
	         catch (UnknownHostException e) {
	            //System.out.println("Exception occured"+ e);
	            break;
	         }
	         catch (IOException e) {
	        	 //System.out.println("Exception occured"+ e);
	        	 break;
	         }
	      }
		port=i;
		
		
		JTextField IPAdress=new JTextField();
		IPAdress.setText(ip);
		IPAdress.setEditable(false);
		JTextField Port=new JTextField();
		Port.setText(String.valueOf(port));
		Port.setEditable(false);
		list = new JList();
		list.setPreferredSize(new Dimension(200, 100));
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		clientItem=new DefaultListModel();
		ClientSelect clientselect=new ClientSelect();
		list.addListSelectionListener(clientselect);
		SendMessage=new JTextArea(10,20);
		ReceivedMessage=new JTextArea(25,30);
		ReceivedMessage.setLineWrap(true);
		ReceivedMessage.setWrapStyleWord(true);
		//发送给服务器
		JButton sendButton=new JButton("server");
		ActionListener send=new send();
		sendButton.addActionListener(send);
		//发送给客户端
		JButton clientbutton=new JButton("client");
		clientbutton.addActionListener(send);
		
		//左边布局
		JPanel leftpanel=new JPanel();
		JPanel northpanel=new JPanel();
		JPanel centerpanel=new JPanel();
		JPanel southpanel=new JPanel();
		northpanel.setLayout(new GridLayout(2,2));
		northpanel.add(new JLabel("IPAdewss"));
		northpanel.add(IPAdress);
		northpanel.add(new JLabel("port:"));
		northpanel.add(Port);
		centerpanel.setLayout(new BorderLayout(0,15));
		centerpanel.add(list,BorderLayout.NORTH);
		centerpanel.add(SendMessage,BorderLayout.CENTER);
		southpanel.add(sendButton);
		southpanel.add(clientbutton);
		leftpanel.setLayout(new BorderLayout(0,20));
		leftpanel.add(northpanel,BorderLayout.NORTH);
		leftpanel.add(centerpanel,BorderLayout.CENTER);
		leftpanel.add(southpanel,BorderLayout.SOUTH);
		
		//右边布局
		JPanel rightpanel=new JPanel();
		rightpanel.add(ReceivedMessage);
		
		//底层布局
		JPanel bottonpanel=new JPanel();
		bottonpanel.setLayout(new FlowLayout());
		bottonpanel.add(leftpanel);
		bottonpanel.add(rightpanel);
		
		//JFrame
		add(bottonpanel);
		pack();
		WindowListener listener=new Terminator();
		addWindowListener(listener);
		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		
		//自身作为服务器 绑定端口
		try {
			ServerSocket server = new ServerSocket(port);
			chatclientaccept cca=new chatclientaccept(server);
			Thread chatclientacceptthread=new Thread(cca);
			chatclientacceptthread.start();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//与服务器socket
		clientmap=new HashMap<>();
		try {
			socket = new Socket(ip, 8000);
			chatServer chatserver=new chatServer(socket);
			Thread chatserverthread=new Thread(chatserver);
			chatserverthread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ReceivedMessage.append("服务器异常");
			System.out.println("服务器异常");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ReceivedMessage.append("服务器异常");
			System.out.println("服务器异常");
		}
			
		
		
	}
	
	//自身作为服务器接受的线程
	class chatclientaccept implements Runnable{
		private ServerSocket serversocket;
		public chatclientaccept(ServerSocket ss){
			this.serversocket=ss;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					Socket chattoclientsocket=serversocket.accept();
					SocketHandler sh=new SocketHandler(chattoclientsocket);
					Thread shthread=new Thread(sh);
					shthread.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	//与客户之间的交互的线程
	class SocketHandler implements Runnable {
		private Socket shsocket;
		public SocketHandler(Socket socket ) {
			this.shsocket = socket;
		} 
        
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(shsocket.getInputStream());// 输入流
				BufferedReader buffer_reader = new BufferedReader(reader);
				PrintWriter writer = new PrintWriter(shsocket.getOutputStream());
				String client = "<" + shsocket.getInetAddress().toString() + ":" + shsocket.getPort() + ">";
				String str;
				System.out.println("来自"+shsocket.getLocalPort());	
				writer.println("来自"+shsocket.getLocalPort());
				writer.flush();
			    while (true) {
					try{
						str = buffer_reader.readLine();
						ReceivedMessage.append(client+"say: "+str+"\n");
						}
					catch(Exception e) {
						 break;   
						}
				}
				buffer_reader.close();
				shsocket.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	//与服务器交互的线程
	class chatServer implements Runnable{
		private Socket socket;
		public chatServer(Socket socket){
			this.socket=socket;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				InputStreamReader reader = new InputStreamReader(socket.getInputStream());// 输入流
				buffer_reader = new BufferedReader(reader);
				writer = new PrintWriter(socket.getOutputStream());
				//先发IP和端口
				writer.println(ip);
				writer.println(port);
				writer.flush();
				isclosed=false;
				String str = null;
				while (true) {
					if(isclosed){
						break;
					}
					try{
					str = buffer_reader.readLine();
					//是否连接表信息
					if(str.equals("clientmap")){
						clientmap.clear();
						clientItem.removeAllElements();
						String port,ip;
						str=buffer_reader.readLine();
						while(!str.equals("over")){
							port=str;
							ip=buffer_reader.readLine();
							System.out.println("port+ip "+port+" "+ip);
							clientmap.put(port, ip);
							str=buffer_reader.readLine();
						}
						for(Map.Entry<String, String> entry : clientmap.entrySet()){
							
							clientItem.addElement(entry.getKey()+" "+entry.getValue());;
						}
						list.setModel(clientItem);
						System.out.println(clientmap.toString());
						continue;
					}
					ReceivedMessage.append(str+"\n");
					System.out.println("Server asy:" + str);
					}catch(Exception e){
						ReceivedMessage.append("服务器异常");
						System.out.println("服务器异常");
						break;
					}
				}
				writer.close();
				buffer_reader.close();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				ReceivedMessage.append("服务器异常");
				System.out.println("服务器异常");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ReceivedMessage.append("服务器异常");
				System.out.println("服务器异常");
			}
			
		}
		
	}
	
	class ClientSelect implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent e) {
            // 获取所有被选中的选项索引
            int[] indices = list.getSelectedIndices();
            System.out.println("选中"+indices.length);
            // 获取选项数据的 ListModel
            ListModel listModel = list.getModel();
            // 输出选中的选项
            pair=new String[2];
            for (int index : indices) {
            	String a=(String) listModel.getElementAt(index);
            	pair=a.split(" ");
                System.out.println("选中: " + index + " = " + pair[0]+"split"+pair[1]);
            }
            System.out.println();
        }
    }
	
	
	
	//事件监听器
	class send implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			if(arg0.getActionCommand().equals("server")){
				toServer=SendMessage.getText();
				writer.println(toServer);
				writer.flush();
			}
			
			if(arg0.getActionCommand().equals("client")){
				toclient=SendMessage.getText();
				int toport=Integer.parseInt(pair[0]);
				String toip=pair[1];
				try {
					Socket s=new Socket(toip,toport);
					PrintWriter writer = new PrintWriter(s.getOutputStream());
					writer.println(toclient);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	class Terminator extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			isclosed=false;
		}
	}
}
