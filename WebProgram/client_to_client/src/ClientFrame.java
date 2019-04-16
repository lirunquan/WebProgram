

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
        
		//����̶������˿�
		/*
		 new Socket������ֻ�Ƕ�Ŀ��˿ڽ����˼�����
		 ����ȥʹ��Ŀ��˿ڣ��������������������Socket��
		 �����֤�������ϵ�Ŀ��˿��Ѿ���ʹ�ã����Ǵ�Socketʹ�õģ���
		 ��֮��֤������˿ڲ�û�г���ʹ��
		 */
		int i;
		for ( i = 8001; i < 9000; i++) {
	         try {
	            System.out.println("�鿴 "+ i);
	            testsocket = new Socket(ip, i);
	            System.out.println("�˿� " + i + " �ѱ�ʹ��");
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
		//���͸�������
		JButton sendButton=new JButton("server");
		ActionListener send=new send();
		sendButton.addActionListener(send);
		//���͸��ͻ���
		JButton clientbutton=new JButton("client");
		clientbutton.addActionListener(send);
		
		//��߲���
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
		
		//�ұ߲���
		JPanel rightpanel=new JPanel();
		rightpanel.add(ReceivedMessage);
		
		//�ײ㲼��
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
		
		//������Ϊ������ �󶨶˿�
		try {
			ServerSocket server = new ServerSocket(port);
			chatclientaccept cca=new chatclientaccept(server);
			Thread chatclientacceptthread=new Thread(cca);
			chatclientacceptthread.start();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//�������socket
		clientmap=new HashMap<>();
		try {
			socket = new Socket(ip, 8000);
			chatServer chatserver=new chatServer(socket);
			Thread chatserverthread=new Thread(chatserver);
			chatserverthread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ReceivedMessage.append("�������쳣");
			System.out.println("�������쳣");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ReceivedMessage.append("�������쳣");
			System.out.println("�������쳣");
		}
			
		
		
	}
	
	//������Ϊ���������ܵ��߳�
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
	
	
	//��ͻ�֮��Ľ������߳�
	class SocketHandler implements Runnable {
		private Socket shsocket;
		public SocketHandler(Socket socket ) {
			this.shsocket = socket;
		} 
        
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(shsocket.getInputStream());// ������
				BufferedReader buffer_reader = new BufferedReader(reader);
				PrintWriter writer = new PrintWriter(shsocket.getOutputStream());
				String client = "<" + shsocket.getInetAddress().toString() + ":" + shsocket.getPort() + ">";
				String str;
				System.out.println("����"+shsocket.getLocalPort());	
				writer.println("����"+shsocket.getLocalPort());
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
	
	
	
	//��������������߳�
	class chatServer implements Runnable{
		private Socket socket;
		public chatServer(Socket socket){
			this.socket=socket;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				InputStreamReader reader = new InputStreamReader(socket.getInputStream());// ������
				buffer_reader = new BufferedReader(reader);
				writer = new PrintWriter(socket.getOutputStream());
				//�ȷ�IP�Ͷ˿�
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
					//�Ƿ����ӱ���Ϣ
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
						ReceivedMessage.append("�������쳣");
						System.out.println("�������쳣");
						break;
					}
				}
				writer.close();
				buffer_reader.close();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				ReceivedMessage.append("�������쳣");
				System.out.println("�������쳣");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ReceivedMessage.append("�������쳣");
				System.out.println("�������쳣");
			}
			
		}
		
	}
	
	class ClientSelect implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent e) {
            // ��ȡ���б�ѡ�е�ѡ������
            int[] indices = list.getSelectedIndices();
            System.out.println("ѡ��"+indices.length);
            // ��ȡѡ�����ݵ� ListModel
            ListModel listModel = list.getModel();
            // ���ѡ�е�ѡ��
            pair=new String[2];
            for (int index : indices) {
            	String a=(String) listModel.getElementAt(index);
            	pair=a.split(" ");
                System.out.println("ѡ��: " + index + " = " + pair[0]+"split"+pair[1]);
            }
            System.out.println();
        }
    }
	
	
	
	//�¼�������
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
