package one_to_many_cs;

import java.awt.BorderLayout;
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
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientFrame extends JFrame{
	private JTextArea ReceivedMessage;
	private JTextArea SendMessage;
	private String ip;
	private String toServer;
	private String fromServer;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader buffer_reader;
	private boolean isclosed;
	public static void main(String[] args) {
		ClientFrame clientframe=new ClientFrame();
	}
	
	public ClientFrame() {
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
		SendMessage=new JTextArea(20,30);
		ReceivedMessage=new JTextArea(25,30);
		ReceivedMessage.setLineWrap(true);
		ReceivedMessage.setWrapStyleWord(true);
		JButton sendButton=new JButton("SendMessage");
		ActionListener send=new send();
		sendButton.addActionListener(send);
		
		//左边布局
		JPanel leftpanel=new JPanel();
		JPanel northpanel=new JPanel();
		JPanel southpanel=new JPanel();
		northpanel.setLayout(new GridLayout(2,2));
		northpanel.add(new JLabel("IPAdewss"));
		northpanel.add(IPAdress);
		northpanel.add(new JLabel("port:"));
		northpanel.add(Port);
		southpanel.add(sendButton);
		leftpanel.setLayout(new BorderLayout());
		leftpanel.add(northpanel,BorderLayout.NORTH);
		leftpanel.add(SendMessage,BorderLayout.CENTER);
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
		
		//socket
		try {
			socket = new Socket(InetAddress.getLocalHost(), 8000);
			InputStreamReader reader = new InputStreamReader(socket.getInputStream());// 输入流
			buffer_reader = new BufferedReader(reader);
			writer = new PrintWriter(socket.getOutputStream());
			isclosed=false;
			String str = null;
			while (true) {
				if(isclosed){
					break;
				}
				try{
				str = buffer_reader.readLine();
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
	
	//事件监听器
	class send implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			toServer=SendMessage.getText();
			writer.println(toServer);
			writer.flush();
		}
		
	}
	
	class Terminator extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			isclosed=false;
		}
	}
}
