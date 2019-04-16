package server;

import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerSwing {

	/**
     *
     */
	public static int Space; // 创建类变量Space，记录选择要缩进的空格数
	public static boolean isSelected; // 创建类变量isSelected，记录行号是否已选择
	ArrayList<String> Path = new ArrayList<>(); // 创建字符串数组Path，当程序为批量转换时，记录所有被选择文件的路径信息
	ArrayList<String> Name = new ArrayList<>(); // 创建字符串数组Name，当程序为批量转换时，记录所有被选择文件的文件名
	static DefaultListModel<String> dlm1 = new DefaultListModel<String>();
	static DefaultListModel<String> dlm2 = new DefaultListModel<String>();

	static JList<String> jList1 = null; // 展示与客户端通信的socket
	static JList<String> jList2 = null; // 展示客户端发来的信息

	static JFrame f;
	JButton button1 = null;
	JTextField ipAddress = null;
	JTextField port = null;
	JTextArea message = null;

	public ServerSwing() throws IOException {
		Start();
	}

	private void Start() throws IOException {
		f = new JFrame();
		f.setTitle("服务端");

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(570, 570);
		f.setLocation(1330, 7);
		Container con = f.getContentPane();
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel label3 = new JLabel("当前在线用户：");
		label3.setFont(new Font("宋体", Font.BOLD, 14));
		panel.add(label3);
		label3.setBounds(35, 102, 170, 33);

		jList1 = new JList<String>();
		jList1.setModel(dlm1);
		JScrollPane sp1 = new JScrollPane(jList1);
		panel.add(sp1);
		sp1.setBounds(28, 132, 135, 244);

		JLabel label4 = new JLabel("对话框：");
		label4.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(label4);
		label4.setBounds(170, 102, 170, 33);

		jList2 = new JList<String>();
		jList2.setModel(dlm2);
		JScrollPane sp2 = new JScrollPane(jList2);
		panel.add(sp2);
		sp2.setBounds(170, 135, 340, 205);

		JLabel label1 = new JLabel("IP地址：");
		label1.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(label1);
		label1.setBounds(35, 15, 110, 33);

		ipAddress = new JTextField();
		ipAddress.setText("127.0.0.1");
		ipAddress.setFont(new Font("宋体", Font.PLAIN, 15));
		panel.add(ipAddress);
		ipAddress.setBounds(95, 15, 180, 33);

		JLabel port_label = new JLabel("端口号：");
		port_label.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(port_label);
		port_label.setBounds(322, 15, 110, 33);

		port = new JTextField();
		port.setText("8888");
		port.setFont(new Font("宋体", Font.PLAIN, 15));
		panel.add(port);
		port.setBounds(395, 15, 50, 33);

		// 构建监听按钮button1
		button1 = new JButton("开始监听");
		panel.add(button1);
		button1.setBounds(101, 57, 96, 30);
		button1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					button1ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// 构建断开连接按钮button2
		JButton button2 = new JButton("断开连接");
		panel.add(button2);
		button2.setBounds(293, 57, 96, 30);
		button2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					button2ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// 构建清空消息按钮button3
		JButton button3 = new JButton("清空消息");
		panel.add(button3);
		button3.setBounds(216, 343, 96, 30);
		button3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button3ActionPerformed(evt);
			}
		});

		// 群发
		JButton button4 = new JButton("群发");
		panel.add(button4);
		button4.setBounds(360, 343, 66, 30);
		button4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					button4ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// 所要发送的消息
		message = new JTextArea(10, 40);
		JScrollPane sp = new JScrollPane(message);
		panel.add(sp);
		sp.setBounds(25, 390, 410, 125);

		JButton button6 = new JButton("发送");
		panel.add(button6);
		button6.setBounds(445, 385, 60, 60);
		button6.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					button5ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JButton button7 = new JButton("退出");
		panel.add(button7);
		button7.setBounds(445, 450, 60, 60);
		button7.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button6ActionPerformed(evt);
			}
		});

		con.add(panel);
		f.setVisible(true);
	}

	// "开始监听"事件
	private void button1ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		if (evt.getSource() == button1) {
			ServerThread socketThread = new ServerThread(dlm1, dlm2, jList1,
					jList2);
			Thread thread = new Thread(socketThread);
			thread.start();
		} else {
			dlm1.addElement("354783495");
			jList1.setModel(dlm1);
		}
	}

	// 断开连接
	private void button2ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		ArrayList<Socket> socket1 = ServerThread.socket;
		for(int j=0;j<socket1.size();j++){
			socket1.get(j).close();
		}
	}

	public static void updateUI() {
		jList1.setModel(dlm1);
		jList2.setModel(dlm2);
		// jList1.updateUI();
	}

	// 群发按钮
	private void button4ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		String _message = "";
		ArrayList<Socket> socket1 = ServerThread.socket;
		for (int j = 0; j < socket1.size(); j++) {
			PrintWriter writer = new PrintWriter(socket1.get(j)
					.getOutputStream());
			_message = message.getText();
			writer.println(_message);
			writer.flush();
		}
	}

	// 清空消息
	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {

		if (jList2.getModel().getSize() == 0) {
			// 判断JList列表中是否有内容
			JOptionPane.showMessageDialog(f, "对话框中没有消息记录", "错 误",
					JOptionPane.INFORMATION_MESSAGE);// 消息对话框

		} else {
			dlm2.clear();
			jList2.setModel(dlm2);
		}
	}

	// 发送事件
	private void button5ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, NullPointerException {
		String _message = "";
		ArrayList<Socket> socket1 = ServerThread.socket;
		List<String> m = jList1.getSelectedValuesList();
		if(m.size()==0){
			JOptionPane.showMessageDialog(f, "请先选中发送对象", "提  示",
					JOptionPane.INFORMATION_MESSAGE);// 消息对话框
		}else{
			for (int i = 0, len = m.size(); i < len; i++) {
				String s = m.get(i).toString();
				int port = Integer.valueOf(s.substring(s.lastIndexOf(":") + 1));
				for (int j = 0; j < socket1.size(); j++) {
					if (socket1.get(j).getPort() == port) {
						PrintWriter writer=null;
						try{
							writer = new PrintWriter(socket1.get(j)
									.getOutputStream());
							_message = message.getText();
							writer.println(_message);
							writer.flush();
						}catch(SocketException e){
							System.out.println("ServerSwing已捕获socketException异常");
						}
					}
				}
			}
		}
	}

	// 退出事件
	private void button6ActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ServerSwing();
				} catch (IOException ex) {
					Logger.getLogger(ServerSwing.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		});
	}

}
