 package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.IIOException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClientUI extends JFrame{
	public JList userList;
	public JTextField serverIpField;
	public JTextField serverPortField;
	public JTextField clientPortField;
	public JTextArea messageField;
	public JTextArea showArea;
	public JButton connectButton;
	public JButton toServerButton;
	public JButton toClientButton;
	public boolean isClosed;
	public int cPort;
	public String cIp;
	private Socket socket, testSocket;
	public String toServer, toClient;
	public PrintWriter writer;
	public BufferedReader reader;
	public DefaultListModel userModel;
	public Map<String, String> userMap;
	public String[] pair;
	
	public static void main(String[] args) {
		ClientUI clientUI = new ClientUI();
	}
	public ClientUI() {
		// TODO Auto-generated constructor stub
		super("Client");
		try {
			InetAddress address = InetAddress.getLocalHost();
			cIp = address.getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//确定客户端自身监听的端口
		int i;
		for(i=9000;i<10000;i++) {
			try {
				testSocket = new Socket(cIp, i);
			}catch (UnknownHostException e) {
				// TODO: handle exception
				break;
			}catch (IOException e) {
				// TODO: handle exception
				break;
			}
		}
		cPort = i;
		
		connectButton = new JButton("Connect");
		toServerButton = new JButton("Send to server");
		toClientButton = new JButton("Send to Client");
		serverIpField = new JTextField("127.0.0.1");
		serverPortField = new JTextField("8848");
		clientPortField = new JTextField(String.valueOf(cPort));
		clientPortField.setEditable(false);
		messageField = new JTextArea(10,20);
		showArea = new JTextArea(25,30);
		userList = new JList();
		userModel = new DefaultListModel();
		userMap = new HashMap<>();
		clientPortField.setEditable(false);
		userList.setPreferredSize(new Dimension(200,100));
		userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		showArea.setLineWrap(true);
		showArea.setWrapStyleWord(true);
		showArea.setEditable(false);
		showArea.setAutoscrolls(true);
		try {
			ServerSocket serverSocket = new ServerSocket(cPort);
			ClientAccept cAccept = new ClientAccept(serverSocket);
			Thread cThread = new Thread(cAccept);
			cThread.start();
		} catch (IOException e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
		try {
			socket = new Socket(serverIpField.getText(), Integer.parseInt(serverPortField.getText()));
			InteractServer itServer = new InteractServer(socket);
			Thread sThread = new Thread(itServer);
			sThread.start();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			showArea.append("Connect to server failed. Host: "+serverIpField.getText()+" port: "+serverPortField.getText()+"\n");
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO: handle exception
			showArea.append("Connect to server failed. Host: "+serverIpField.getText()+" port: "+serverPortField.getText()+"\n");
			e2.printStackTrace();
		}
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		userList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				int[] indices = userList.getSelectedIndices();
				ListModel listModel = userList.getModel();
				pair = new String[2];
				for(int index : indices) {
					String string = (String) listModel.getElementAt(index);
					pair = string.split(" ");
					System.out.println("Selected: "+index+"["+pair[1]+":"+pair[0]+"]");
				}
			}
		});
		toServerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				toServer = messageField.getText();
				writer.println(toServer);
				writer.flush();
			}
		});
		toClientButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				toClient = messageField.getText();
				String ip = pair[1];
				int port = Integer.parseInt(pair[0]);
				try {
					Socket s = new Socket(ip, port);
					PrintWriter printWriter = new PrintWriter(s.getOutputStream());
					printWriter.println(toClient);
					printWriter.flush();
				} catch (IOException e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				isClosed = false;
			}
		});
		JPanel leftPanel = new JPanel();
		JPanel upPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel downPanel = new JPanel();
		upPanel.setLayout(new GridLayout(4,2));
		upPanel.add(new JLabel("Server IP: "));
		upPanel.add(serverIpField);
		upPanel.add(new JLabel("Server port: "));
		upPanel.add(serverPortField);
		upPanel.add(new JLabel("My port: "));
		upPanel.add(clientPortField);
//		upPanel.add(connectButton);
		centerPanel.setLayout(new BorderLayout(0, 15));
		centerPanel.add(messageField,BorderLayout.CENTER);
		centerPanel.add(userList, BorderLayout.NORTH);
		downPanel.add(toClientButton);
		downPanel.add(toServerButton);
		leftPanel.setLayout(new BorderLayout(0, 20));
		leftPanel.add(upPanel, BorderLayout.NORTH);
		leftPanel.add(centerPanel, BorderLayout.CENTER);
		leftPanel.add(downPanel, BorderLayout.SOUTH);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(showArea);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(leftPanel, BorderLayout.WEST);
		getContentPane().add(rightPanel, BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
	}
	//与服务器交互的线程
	class InteractServer implements Runnable{
		private Socket socket;
		public InteractServer(Socket socket) {
			// TODO Auto-generated constructor stub
			this.socket = socket;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				writer.println(cIp);
				writer.println(cPort);
				writer.flush();
				isClosed = false;
				String string = "";
				while (true) {
					if (isClosed) {
						break;
					}
					try {
						string = reader.readLine();
						if(string.equals("userMap")) {
							userMap.clear();
							userModel.removeAllElements();
							String port, ip;
							string = reader.readLine();
							while (!string.equals("end")) {
								port = string;
								ip = reader.readLine();
								System.out.println("[ip-port]: ["+ip+"-"+port+"]");
								userMap.put(port, ip);
								string = reader.readLine();
							}
							for(Map.Entry<String, String> entry:userMap.entrySet()) {
								userModel.addElement(entry.getKey()+" "+entry.getValue());
							}
							userList.setModel(userModel);
							System.out.println(userMap.toString());
							continue;
						}
						showArea.append("Receive from server: "+string+"\n");
						System.out.println("Receive from server: "+string);
					} catch (Exception e) {
						// TODO: handle exception
						showArea.append(e.toString());
						break;
					}
				}
				writer.close();
				reader.close();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO: handle exception
				showArea.append("Connect to server failed. Host: "+serverIpField.getText()+" port: "+serverPortField.getText()+"\n");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				showArea.append("Connect to server failed. Host: "+serverIpField.getText()+" port: "+serverPortField.getText()+"\n");
				e.printStackTrace();
			}
		}
		
	}
	//自身作为服务器时接收其他客户端信息的线程
	class ClientAccept implements Runnable{
		private ServerSocket serverSocket;
		public ClientAccept(ServerSocket ss) {
			// TODO Auto-generated constructor stub
			this.serverSocket = ss;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					Socket acceptSocket = serverSocket.accept();
					InteractClient itClient = new InteractClient(acceptSocket);
					Thread thread = new Thread(itClient);
					thread.start();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}
	//与其他客户端交互的线程
	class InteractClient implements Runnable{
		private Socket shsocket;
		public InteractClient(Socket socket ) {
			this.shsocket = socket;
		} 
        
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(shsocket.getInputStream());// 输入流
				BufferedReader buffer_reader = new BufferedReader(reader);
				PrintWriter writer = new PrintWriter(shsocket.getOutputStream());
				String client = "<" + shsocket.getInetAddress().toString() + ":" + shsocket.getPort() + ">";
				String str;
				System.out.println("From: "+shsocket.getLocalPort());	
				writer.println("From: "+shsocket.getLocalPort());
				writer.flush();
			    while (true) {
					try{
						str = buffer_reader.readLine();
						showArea.append(client+": "+str+"\n");
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
}
