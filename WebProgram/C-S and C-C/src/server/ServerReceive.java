package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class ServerReceive implements Runnable {
	Socket socket;
	DefaultListModel<String> dlm1;
	DefaultListModel<String> dlm2;
	JList<String> jList1;
	JList<String> jList2;
	InputStreamReader reader;
	BufferedReader buffer_reader;
	int localBind;

	public ServerReceive(Socket socket, DefaultListModel<String> dlm1,
			DefaultListModel<String> dlm2, JList<String> jList1,
			JList<String> jList2, int localBind) {
		this.socket = socket;
		this.dlm1 = dlm1;
		this.dlm2 = dlm2;
		this.jList1 = jList1;
		this.jList2 = jList2;
		this.localBind = localBind;
	}

	public void run() {
		while (true) {
			if (socket.isClosed()) {
				break;
			}
			boolean flag = false;
			try {
				reader = new InputStreamReader(socket.getInputStream());
				buffer_reader = new BufferedReader(reader);
				String request = null;
				try {
					request = buffer_reader.readLine();
					if (request == null) {
						ArrayList<Socket> socket1 = ServerThread.socket;
						DefaultListModel<String> m = (DefaultListModel<String>) jList1
								.getModel();
						JOptionPane.showMessageDialog(ServerSwing.f, "     端口"
								+ socket.getPort() + "正常断开", "提  示",
								JOptionPane.INFORMATION_MESSAGE);// 消息对话框
						// 证明有客户端已断开连接，此时发信号给其他客户端先清空用户列表，再把更新后的列表发送过去
						for (int z = 0; z < socket1.size(); z++) {
							PrintWriter writer = new PrintWriter(socket1.get(z)
									.getOutputStream());
							// 发送清空信号到客户端，客户端辨识出清空列表信号后清空列表
							// 下一个for循环就把最新的用户列表发送过去
							writer.println("clear");
							writer.flush();
						}
						for (int i = 0, len = socket1.size(); i < len; i++) {
							if (socket1.get(i).getPort() == socket.getPort()) {
								socket1.remove(i);
								for (int j = 0; j < m.getSize(); j++) {
									String s = m.getElementAt(j);
									int port = Integer.valueOf(s.substring(s
											.lastIndexOf(":") + 1));
									if (socket.getPort() == port) {
										m.removeElementAt(j);
										jList1.setModel(m);
										// 在此之前socket1的长度已经减一
										for (int x = 0; x < socket1.size(); x++) {
											PrintWriter writer = new PrintWriter(
													socket1.get(x)
															.getOutputStream());
											System.out.println("现在发送到："
													+ socket1.get(x).getPort());
											try {
												// 暂停5毫秒后程序继续执行,靠后发送的socket暂停时间久点
												Thread.sleep(5);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											for (int y = 0; y < m.getSize(); y++) {
												String message = m.elementAt(y);
												writer.println(message);
												System.out.println(socket1.get(
														x).toString()
														+ "  " + message);
												writer.flush();
												try {
													// 暂停8毫秒后程序继续执行,靠后发送的socket暂停时间久点
													Thread.sleep(8);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											}
										}
										flag = true;
									}
								}
								if (flag) {
									break;
								}
							}
						}
						socket.close();
					}
				} catch (SocketException e) {
					// 此时处理客户端异常退出
					ArrayList<Socket> socket1 = ServerThread.socket;
					DefaultListModel<String> m = (DefaultListModel<String>) jList1
							.getModel();
					JOptionPane.showMessageDialog(ServerSwing.f, "     端口"
							+ socket.getPort() + "异常断开", "提  示",
							JOptionPane.INFORMATION_MESSAGE);
					// 证明有客户端已断开连接，此时发信号给其他客户端先清空用户列表，再把更新后的列表发送过去
					for (int z = 0; z < socket1.size(); z++) {
						PrintWriter writer = new PrintWriter(socket1.get(z)
								.getOutputStream());
						// 发送清空信号到客户端，客户端辨识出清空列表信号后清空列表
						// 下一个for循环就把最新的用户列表发送过去
						writer.println("clear");
						writer.flush();
					}
					for (int i = 0, len = socket1.size(); i < len; i++) {
						if (socket1.get(i).getPort() == socket.getPort()) {
							socket1.remove(i);
							for (int j = 0; j < m.getSize(); j++) {
								String s = m.getElementAt(j);
								int port = Integer.valueOf(s.substring(s
										.lastIndexOf(":") + 1));
								if (socket.getPort() == port) {
									m.removeElementAt(j);
									jList1.setModel(m);
									// 在此之前socket1的长度已经减一
									for (int x = 0; x < socket1.size(); x++) {
										PrintWriter writer = new PrintWriter(
												socket1.get(x)
														.getOutputStream());
										System.out.println("现在发送到："
												+ socket1.get(x).getPort());
										try {
											// 暂停5毫秒后程序继续执行,靠后发送的socket暂停时间久点
											Thread.sleep(5);
										} catch (InterruptedException e2) {
											e2.printStackTrace();
										}
										for (int y = 0; y < m.getSize(); y++) {
											String message = m.elementAt(y);
											writer.println(message);
											System.out.println(socket1.get(x)
													.toString()
													+ "  "
													+ message);
											writer.flush();
											try {
												// 暂停8毫秒后程序继续执行,靠后发送的socket暂停时间久点
												Thread.sleep(8);
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											}
										}
									}
									flag = true;
								}
							}
							if (flag) {
								break;
							}
						}
					}
					socket.close();
				}
				if (!flag) {
					dlm2.addElement(request);
					jList2.setModel(dlm2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
