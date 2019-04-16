package one_to_many_cs;

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

public class ServerFrame extends JFrame {
	private JTextArea ReceivedMessage;
	private JTextArea SendMessage;
	private String ip;
	private String fromclient,toclient;
	private JList list; 
	private DefaultListModel clientItem;
	private ArrayList<Socket> socketlist; 
	private int clientNo,index;
	public static void main(String[] args) {
		ServerFrame serverframe=new ServerFrame();
	}
	
	public ServerFrame() {
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
		JTextField Port=new JTextField();
		Port.setText("8000");
		Port.setEditable(false);
	    list = new JList();
		list.setPreferredSize(new Dimension(200, 100));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		clientItem = new DefaultListModel();
		ClientSelect clientselect=new ClientSelect();
		list.addListSelectionListener(clientselect);
		SendMessage=new JTextArea(10,20);
		ReceivedMessage=new JTextArea(25,30);
		ReceivedMessage.setLineWrap(true);
		ReceivedMessage.setWrapStyleWord(true);
		JButton sendButton=new JButton("SendMessage");
		Send send=new Send();
		sendButton.addActionListener(send);
		
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
		//setSize(400,500);
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		
		clientNo=0;
		index=0;
		try {
			ServerSocket server = new ServerSocket(8000);
			socketlist = new ArrayList<Socket>();
			while (true) {
				Socket socket = server.accept();
				clientItem.add(index, socket);;
				list.setModel(clientItem);
				ReceivedMessage.append("Client"+clientNo+" connected from: "+socket.getInetAddress().getHostAddress()+" port:"+socket.getPort()+"\n time:"+new Date()+"\n");
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
				InputStreamReader reader = new InputStreamReader(socket.getInputStream());// 输入流
				BufferedReader buffer_reader = new BufferedReader(reader);
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				String client = "<" + socket.getInetAddress().toString() + ":" + socket.getPort() + ">";
				writer.println("hello i am server...");
				writer.flush();
				String str;
				while (true) {
					try{
						str = buffer_reader.readLine();
						ReceivedMessage.append("client"+no+"say: "+str+"\n");
						System.out.println(client + str);
						}
					catch(Exception e) {
						 ReceivedMessage.append("client"+no+" 断开连接"+"\n");
						 clientItem.remove(no);
						 list.setModel(clientItem);
						 break;   
						}
				}
				buffer_reader.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	class ClientSelect implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent e) {
        	socketlist.clear();
            // 获取所有被选中的选项索引
            int[] indices = list.getSelectedIndices();
            // 获取选项数据的 ListModel
            ListModel<Socket> listModel = list.getModel();
            // 输出选中的选项
            for (int index : indices) {
            	socketlist.add(listModel.getElementAt(index));
                System.out.println("选中: " + index + " = " + listModel.getElementAt(index));
            }
            System.out.println();
        }
    }
	
	class Send implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
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
}
