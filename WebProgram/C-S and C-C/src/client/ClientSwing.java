package client;

import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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

public class ClientSwing {

	static DefaultListModel<String> dlm1 = new DefaultListModel<String>();
	static DefaultListModel<String> dlm2 = new DefaultListModel<String>();

	static JList<String> jList1 = null; // 展示与客户通信的socket
	static JList<String> jList2 = null; // 对话框

	static JFrame f;
	JButton button1 = null;
	static JTextField ipAddress = null;
	String _ipAddress = null;
	static JTextField port = null;
	int _port = 0;
	JTextArea message = null;
	String _message = null;
	Socket socket = null;
	JButton button2 = null;
	JButton button3 = null;
	JButton button4 = null;
	JButton button5 = null;
	JButton button6 = null;
	int localBind=0;
	PrintWriter writer = null;

	public ClientSwing() throws IOException {
		Start();
	}

	private void Start() throws IOException {
		f = new JFrame();
		f.setTitle("客户端");

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(570, 570);
		f.setLocation(1330, 500);
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

		JLabel ipAddress_label = new JLabel("服务器端口：");
		ipAddress_label.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(ipAddress_label);
		ipAddress_label.setBounds(35, 15, 120, 33);

		ipAddress = new JTextField();
		panel.add(ipAddress);
		ipAddress.setBounds(135, 15, 100, 33);

		JLabel port_label = new JLabel("设置本机端口：");
		port_label.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(port_label);
		port_label.setBounds(288, 15, 140, 33);

		port = new JTextField();
		panel.add(port);
		port.setBounds(400, 15, 50, 33);

		// 服务器端口号
		ipAddress.setText("8888");

		// 构建监听按钮button1
		button1 = new JButton("连接服务器");
		panel.add(button1);
		button1.setBounds(101, 57, 116, 30);
		button1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					button1ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// 构建断开按钮button2
		button2 = new JButton("断开连接");
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
				button4ActionPerformed(evt);
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

	// 连接服务器,创建通信socket
	private void button1ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, IOException {

		// 当socket为null或close时，创建一个socket，以输入的端口作为监听端口并开启监听
		// 当socket已建立好连接时，若再次点击连接服务器，则应给出对应提示
		if (socket == null || socket.isClosed()) {
			try {
				// 获取客户端监听的端口
				if (port.getText().equals("")) {
					JOptionPane.showMessageDialog(f, "    请先输入监听端口", "提   示",
							JOptionPane.INFORMATION_MESSAGE);// 消息对话框
				} else {
					ipAddress.setText("8888");
					socket = new Socket("127.0.0.1", 8888);
					localBind = Integer.valueOf(port.getText());
					//当与服务器建立连接后，把监听端口发送给服务器，然后显示在jList1上，而不是展示与服务器连接的动态端口
					writer = new PrintWriter(socket.getOutputStream());
					writer.println("127.0.0.1:"+localBind);
					writer.flush();
					//启动监听
					ClientThread clientThread = new ClientThread(dlm1, dlm2,
							jList1, jList2, localBind);
					new Thread(clientThread).start();
					//启动线程接收服务器的消息
					ClientReceiveServer clientReceive = new ClientReceiveServer(socket, dlm1,
							dlm2, jList1, jList2);
					Thread thread = new Thread(clientReceive);
					thread.start();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(f, "    服务器未开启监听", "提   示",
						JOptionPane.INFORMATION_MESSAGE);// 消息对话框
			}
		} else {
			JOptionPane.showMessageDialog(f, "    请勿重复建立连接", "提   示",
					JOptionPane.INFORMATION_MESSAGE);// 消息对话框
		}
	}

	// 断开连接
	private void button2ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, IOException {
		if (socket == null) {
			JOptionPane.showMessageDialog(f, "    请先建立连接", "错  误",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (socket.isClosed()) {
			// 消息对话框
			JOptionPane.showMessageDialog(f, "    socket已关闭", "错  误",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			ipAddress.setText("");
			port.setText("");
			dlm1.clear();
			dlm2.clear();
			jList1.setModel(dlm1);
			jList2.setModel(dlm2);
			socket.close();
		}
	}

	// 清空
	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {
		if (jList2.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(f, "     列表内没有消息", "温馨提示",
					JOptionPane.INFORMATION_MESSAGE);// 消息对话框
		} else {
			dlm2.clear();
			jList2.updateUI();
		}
	}

	// 接受
	private void button4ActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println(socket.toString());
	}

	// 发送
	private void button5ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		// 发送框内容
		_message = message.getText();
		List<String> m = jList1.getSelectedValuesList();
		if(m.size()==0){
			//m.size()==0的时候代表不是与其他客户端对话，此时发送给服务器
			if (socket != null) {
				try {
					if (!_message.equals("")) {
						writer = new PrintWriter(socket.getOutputStream());
						String sendMessage="端口" + localBind + "  say:  "+_message;
						writer.println(sendMessage);
						writer.flush();
					} else {
						JOptionPane.showMessageDialog(f, "     请输入消息", "提   示",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (SocketException e) {
					JOptionPane.showMessageDialog(f, "     连接已断开", "错  误",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(f, "    请先建立连接", "错  误",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}else{
			if (!_message.equals("")) {
				//此时通过获取选中的客户端，获取其监听端口，然后建立socket连接
				for(int i=0;i<m.size();i++){
					String client=m.get(i);
					int index=client.indexOf(':')+1;
					int last=client.lastIndexOf(':');
					//获取将要与其通信的客户端的监听端口，建立socket通信，此时设置为每通信一次，建立一个socket，不保留连接
					int link_port=Integer.valueOf(client.substring(index, last));
					Socket socketSend=new Socket("127.0.0.1",link_port);
					PrintWriter writeSend = new PrintWriter(socketSend.getOutputStream());
					String sendMessage="客户" + localBind + "  say:  "+_message;
					writeSend.println(sendMessage);
					writeSend.flush();
					socketSend.close();
				}
			} else {
				JOptionPane.showMessageDialog(f, "     请输入消息", "提   示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// 关闭
	private void button6ActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ClientSwing();
				} catch (IOException ex) {
					Logger.getLogger(ClientSwing.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		});
	}

}
