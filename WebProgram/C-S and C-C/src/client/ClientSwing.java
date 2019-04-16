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

	static JList<String> jList1 = null; // չʾ��ͻ�ͨ�ŵ�socket
	static JList<String> jList2 = null; // �Ի���

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
		f.setTitle("�ͻ���");

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(570, 570);
		f.setLocation(1330, 500);
		Container con = f.getContentPane();
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel label3 = new JLabel("��ǰ�����û���");
		label3.setFont(new Font("����", Font.BOLD, 14));
		panel.add(label3);
		label3.setBounds(35, 102, 170, 33);

		jList1 = new JList<String>();
		jList1.setModel(dlm1);
		JScrollPane sp1 = new JScrollPane(jList1);
		panel.add(sp1);
		sp1.setBounds(28, 132, 135, 244);

		JLabel label4 = new JLabel("�Ի���");
		label4.setFont(new Font("����", Font.BOLD, 16));
		panel.add(label4);
		label4.setBounds(170, 102, 170, 33);

		jList2 = new JList<String>();
		jList2.setModel(dlm2);
		JScrollPane sp2 = new JScrollPane(jList2);
		panel.add(sp2);
		sp2.setBounds(170, 135, 340, 205);

		JLabel ipAddress_label = new JLabel("�������˿ڣ�");
		ipAddress_label.setFont(new Font("����", Font.BOLD, 16));
		panel.add(ipAddress_label);
		ipAddress_label.setBounds(35, 15, 120, 33);

		ipAddress = new JTextField();
		panel.add(ipAddress);
		ipAddress.setBounds(135, 15, 100, 33);

		JLabel port_label = new JLabel("���ñ����˿ڣ�");
		port_label.setFont(new Font("����", Font.BOLD, 16));
		panel.add(port_label);
		port_label.setBounds(288, 15, 140, 33);

		port = new JTextField();
		panel.add(port);
		port.setBounds(400, 15, 50, 33);

		// �������˿ں�
		ipAddress.setText("8888");

		// ����������ťbutton1
		button1 = new JButton("���ӷ�����");
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

		// �����Ͽ���ťbutton2
		button2 = new JButton("�Ͽ�����");
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

		// ���������Ϣ��ťbutton3
		JButton button3 = new JButton("�����Ϣ");
		panel.add(button3);
		button3.setBounds(216, 343, 96, 30);
		button3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button3ActionPerformed(evt);
			}
		});

		// Ⱥ��
		JButton button4 = new JButton("Ⱥ��");
		panel.add(button4);
		button4.setBounds(360, 343, 66, 30);
		button4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button4ActionPerformed(evt);
			}
		});

		// ��Ҫ���͵���Ϣ
		message = new JTextArea(10, 40);
		JScrollPane sp = new JScrollPane(message);
		panel.add(sp);
		sp.setBounds(25, 390, 410, 125);

		JButton button6 = new JButton("����");
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

		JButton button7 = new JButton("�˳�");
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

	// ���ӷ�����,����ͨ��socket
	private void button1ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, IOException {

		// ��socketΪnull��closeʱ������һ��socket��������Ķ˿���Ϊ�����˿ڲ���������
		// ��socket�ѽ���������ʱ�����ٴε�����ӷ���������Ӧ������Ӧ��ʾ
		if (socket == null || socket.isClosed()) {
			try {
				// ��ȡ�ͻ��˼����Ķ˿�
				if (port.getText().equals("")) {
					JOptionPane.showMessageDialog(f, "    ������������˿�", "��   ʾ",
							JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���
				} else {
					ipAddress.setText("8888");
					socket = new Socket("127.0.0.1", 8888);
					localBind = Integer.valueOf(port.getText());
					//����������������Ӻ󣬰Ѽ����˿ڷ��͸���������Ȼ����ʾ��jList1�ϣ�������չʾ����������ӵĶ�̬�˿�
					writer = new PrintWriter(socket.getOutputStream());
					writer.println("127.0.0.1:"+localBind);
					writer.flush();
					//��������
					ClientThread clientThread = new ClientThread(dlm1, dlm2,
							jList1, jList2, localBind);
					new Thread(clientThread).start();
					//�����߳̽��շ���������Ϣ
					ClientReceiveServer clientReceive = new ClientReceiveServer(socket, dlm1,
							dlm2, jList1, jList2);
					Thread thread = new Thread(clientReceive);
					thread.start();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(f, "    ������δ��������", "��   ʾ",
						JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���
			}
		} else {
			JOptionPane.showMessageDialog(f, "    �����ظ���������", "��   ʾ",
					JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���
		}
	}

	// �Ͽ�����
	private void button2ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, IOException {
		if (socket == null) {
			JOptionPane.showMessageDialog(f, "    ���Ƚ�������", "��  ��",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (socket.isClosed()) {
			// ��Ϣ�Ի���
			JOptionPane.showMessageDialog(f, "    socket�ѹر�", "��  ��",
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

	// ���
	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {
		if (jList2.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(f, "     �б���û����Ϣ", "��ܰ��ʾ",
					JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���
		} else {
			dlm2.clear();
			jList2.updateUI();
		}
	}

	// ����
	private void button4ActionPerformed(java.awt.event.ActionEvent evt) {
		// System.out.println(socket.toString());
	}

	// ����
	private void button5ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException {
		// ���Ϳ�����
		_message = message.getText();
		List<String> m = jList1.getSelectedValuesList();
		if(m.size()==0){
			//m.size()==0��ʱ��������������ͻ��˶Ի�����ʱ���͸�������
			if (socket != null) {
				try {
					if (!_message.equals("")) {
						writer = new PrintWriter(socket.getOutputStream());
						String sendMessage="�˿�" + localBind + "  say:  "+_message;
						writer.println(sendMessage);
						writer.flush();
					} else {
						JOptionPane.showMessageDialog(f, "     ��������Ϣ", "��   ʾ",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (SocketException e) {
					JOptionPane.showMessageDialog(f, "     �����ѶϿ�", "��  ��",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(f, "    ���Ƚ�������", "��  ��",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}else{
			if (!_message.equals("")) {
				//��ʱͨ����ȡѡ�еĿͻ��ˣ���ȡ������˿ڣ�Ȼ����socket����
				for(int i=0;i<m.size();i++){
					String client=m.get(i);
					int index=client.indexOf(':')+1;
					int last=client.lastIndexOf(':');
					//��ȡ��Ҫ����ͨ�ŵĿͻ��˵ļ����˿ڣ�����socketͨ�ţ���ʱ����Ϊÿͨ��һ�Σ�����һ��socket������������
					int link_port=Integer.valueOf(client.substring(index, last));
					Socket socketSend=new Socket("127.0.0.1",link_port);
					PrintWriter writeSend = new PrintWriter(socketSend.getOutputStream());
					String sendMessage="�ͻ�" + localBind + "  say:  "+_message;
					writeSend.println(sendMessage);
					writeSend.flush();
					socketSend.close();
				}
			} else {
				JOptionPane.showMessageDialog(f, "     ��������Ϣ", "��   ʾ",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// �ر�
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
