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
	public static int Space; // ���������Space����¼ѡ��Ҫ�����Ŀո���
	public static boolean isSelected; // ���������isSelected����¼�к��Ƿ���ѡ��
	ArrayList<String> Path = new ArrayList<>(); // �����ַ�������Path��������Ϊ����ת��ʱ����¼���б�ѡ���ļ���·����Ϣ
	ArrayList<String> Name = new ArrayList<>(); // �����ַ�������Name��������Ϊ����ת��ʱ����¼���б�ѡ���ļ����ļ���
	static DefaultListModel<String> dlm1 = new DefaultListModel<String>();
	static DefaultListModel<String> dlm2 = new DefaultListModel<String>();

	static JList<String> jList1 = null; // չʾ��ͻ���ͨ�ŵ�socket
	static JList<String> jList2 = null; // չʾ�ͻ��˷�������Ϣ

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
		f.setTitle("�����");

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(570, 570);
		f.setLocation(1330, 7);
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

		JLabel label1 = new JLabel("IP��ַ��");
		label1.setFont(new Font("����", Font.BOLD, 16));
		panel.add(label1);
		label1.setBounds(35, 15, 110, 33);

		ipAddress = new JTextField();
		ipAddress.setText("127.0.0.1");
		ipAddress.setFont(new Font("����", Font.PLAIN, 15));
		panel.add(ipAddress);
		ipAddress.setBounds(95, 15, 180, 33);

		JLabel port_label = new JLabel("�˿ںţ�");
		port_label.setFont(new Font("����", Font.BOLD, 16));
		panel.add(port_label);
		port_label.setBounds(322, 15, 110, 33);

		port = new JTextField();
		port.setText("8888");
		port.setFont(new Font("����", Font.PLAIN, 15));
		panel.add(port);
		port.setBounds(395, 15, 50, 33);

		// ����������ťbutton1
		button1 = new JButton("��ʼ����");
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

		// �����Ͽ����Ӱ�ťbutton2
		JButton button2 = new JButton("�Ͽ�����");
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
				try {
					button4ActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
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

	// "��ʼ����"�¼�
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

	// �Ͽ�����
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

	// Ⱥ����ť
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

	// �����Ϣ
	private void button3ActionPerformed(java.awt.event.ActionEvent evt) {

		if (jList2.getModel().getSize() == 0) {
			// �ж�JList�б����Ƿ�������
			JOptionPane.showMessageDialog(f, "�Ի�����û����Ϣ��¼", "�� ��",
					JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���

		} else {
			dlm2.clear();
			jList2.setModel(dlm2);
		}
	}

	// �����¼�
	private void button5ActionPerformed(java.awt.event.ActionEvent evt)
			throws IOException, NullPointerException {
		String _message = "";
		ArrayList<Socket> socket1 = ServerThread.socket;
		List<String> m = jList1.getSelectedValuesList();
		if(m.size()==0){
			JOptionPane.showMessageDialog(f, "����ѡ�з��Ͷ���", "��  ʾ",
					JOptionPane.INFORMATION_MESSAGE);// ��Ϣ�Ի���
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
							System.out.println("ServerSwing�Ѳ���socketException�쳣");
						}
					}
				}
			}
		}
	}

	// �˳��¼�
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
