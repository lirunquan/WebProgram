package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ServerUI extends JFrame {
	private JTextArea ReceivedMessage;
	private JTextArea SendMessage;
	private String ip;
	private String fromclient,toclient;
	private JList list; 
	private DefaultListModel clientItem;
	private ArrayList<Socket> socketlist; 
	private int clientNo,index;
	JTextField Port;
	private Map<String,String> clientmap;
	public static void main(String[] args) {
		ServerUI serverframe=new ServerUI();
	}
	
	public ServerUI() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress().toString();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

		
		JTextField IPAdress=new JTextField();
		IPAdress.setText(ip);
		IPAdress.setEditable(false);
		Port =new JTextField();
		Port.setText("8848");
		Port.setEditable(false);
	    list = new JList();
		list.setPreferredSize(new Dimension(200, 100));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		clientItem = new DefaultListModel();
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				socketlist.clear();
	            int[] indices = list.getSelectedIndices();
	            ListModel<Socket> listModel = list.getModel();
	            for (int index : indices) {
	            	socketlist.add(listModel.getElementAt(index));
	                System.out.println("Selected:  " + index + " = " + listModel.getElementAt(index));
	            }
	            System.out.println();
			}
		});
		SendMessage=new JTextArea(10,20);
		ReceivedMessage=new JTextArea(25,30);
		ReceivedMessage.setLineWrap(true);
		ReceivedMessage.setWrapStyleWord(true);
		JButton sendButton=new JButton("Send to selected");
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e1) {
				// TODO Auto-generated method stub
				if(SendMessage.getText()!="") {
					String message=SendMessage.getText();
					for(Socket socket: socketlist){
						try {
							PrintWriter writer = new PrintWriter(socket.getOutputStream());
							writer.println(message);
							writer.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		JButton toAllButton = new JButton("Send to all");
		toAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(SendMessage.getText()!="") {
					socketlist.clear();
					ListModel<Socket> listModel = list.getModel();
					for(int i=0;i<listModel.getSize();i++) {
						socketlist.add(listModel.getElementAt(i));
					}
					String message=SendMessage.getText();
					for(Socket socket: socketlist){
						try {
							PrintWriter writer = new PrintWriter(socket.getOutputStream());
							writer.println(message);
							writer.flush();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		JPanel leftpanel=new JPanel();
		JPanel northpanel=new JPanel();
		JPanel centerpanel=new JPanel();
		JPanel southpanel=new JPanel();
		northpanel.setLayout(new GridLayout(2,2));
		northpanel.add(new JLabel("IPAdress"));
		northpanel.add(IPAdress);
		northpanel.add(new JLabel("port:"));
		northpanel.add(Port);
		centerpanel.setLayout(new BorderLayout(0,15));
		centerpanel.add(list,BorderLayout.NORTH);
		centerpanel.add(SendMessage,BorderLayout.CENTER);
		southpanel.add(sendButton);
		southpanel.add(toAllButton);
		leftpanel.setLayout(new BorderLayout(0,20));
		leftpanel.add(northpanel,BorderLayout.NORTH);
		leftpanel.add(centerpanel,BorderLayout.CENTER);
		leftpanel.add(southpanel,BorderLayout.SOUTH);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(ReceivedMessage);
		JPanel rightpanel=new JPanel(new BorderLayout());
		rightpanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottonpanel=new JPanel();
		bottonpanel.setLayout(new FlowLayout());
		bottonpanel.add(leftpanel);
		bottonpanel.add(rightpanel);
		
		add(bottonpanel);
		pack();
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);		
		clientNo=0;
		clientmap = new HashMap<>();
		try {
			ServerSocket server = new ServerSocket(Integer.parseInt(Port.getText()));
			socketlist = new ArrayList<Socket>();
			while (true) {
				Socket socket = server.accept();
				clientItem.addElement(socket);
				list.setModel(clientItem);		
				ReceivedMessage.append("Client"+clientNo+" connected: "+socket.getInetAddress().getHostAddress()+" port:"+socket.getPort()+"\n time:"+new Date()+"\n");
				SocketHandler handler = new SocketHandler(socket,clientNo);
				Thread thread = new Thread(handler);
				thread.start();
				clientNo++;
				index++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class SocketHandler implements Runnable {
		private Socket socket;
		private int no;
		public SocketHandler(Socket socket ,int clientno) {
			this.socket = socket;
			this.no=clientno;
		} 
        
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(socket.getInputStream());
				BufferedReader buffer_reader = new BufferedReader(reader);
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				String client = "<" + socket.getInetAddress().toString() + ":" + socket.getPort() + ">";
				writer.println("Connected to server["+ip+":"+Port.getText()+"]");
				writer.flush();
				String str;
				String clientip = buffer_reader.readLine();
				String clientport=buffer_reader.readLine();
			    clientmap.put(clientport, clientip);
			    broadcast();
			    System.out.println("clientMap: "+clientmap.toString());
				
			    while (true) {
					try{
						str = buffer_reader.readLine();
						ReceivedMessage.append("client"+no+": "+str+"\n");
						System.out.println(client + str);
						}
					catch(Exception e) {
						 ReceivedMessage.append("client"+no+" disconnected."+"\n");
						 for(int i=0;i<clientItem.size();i++)
						 System.out.println(clientItem.get(i)+" "+clientItem.indexOf(clientItem.get(i)));
						 clientItem.removeElement(socket);
						 list.setModel(clientItem);
						 break;   
						}
				}
				buffer_reader.close();
				socket.close();
				//连接关闭  客户ip端口去除
				clientmap.remove(clientport);
				broadcast();
				System.out.println("ClientMap: "+clientmap.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	//广播连接者
	public void broadcast(){
		for(int i=0;i<clientItem.size();i++){
			Socket socket=(Socket) clientItem.getElementAt(i);
			try {
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				writer.println("userMap");
				for(Map.Entry<String, String> entry : clientmap.entrySet()){
					writer.println(entry.getKey());
					writer.println(entry.getValue());
				}
				writer.println("end");
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
