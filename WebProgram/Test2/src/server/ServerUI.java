package server;
import java.util.ArrayList;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.SwingConstants;

public class ServerUI extends JFrame{
	
	public JButton btnStart;
	public JButton btnSend;
	public JButton btnSendAll;
	public JTextArea tShow;
	public JTextField portField;
	public JTextArea tMessage;
	public MyServer server;
	public ArrayList<Socket> clientSockets;
	public JList list;
	public DefaultListModel clientListModel;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerUI ui = new ServerUI();
	}
	
	public ServerUI() {
		// TODO Auto-generated constructor stub
		super("Server");
		btnStart = new JButton("Start");
		btnSend = new JButton("Send to selected");
		btnSend.setEnabled(false);
		btnSendAll = new JButton("Send to all");
		btnSendAll.setEnabled(false);
		portField = new JTextField("8848");
		tMessage = new JTextArea();
		tShow = new JTextArea();
		list = new JList();
		list.setPreferredSize(new Dimension(200, 100));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		clientListModel = new DefaultListModel();
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				btnSend.setEnabled(true);
				clientSockets.clear();
				int[] indexes = list.getSelectedIndices();
				ListModel<Socket> listModel = list.getModel();
				for(int index : indexes) {
					clientSockets.add(listModel.getElementAt(index));
					tShow.append("Client "+index+" Selected\n");
				}
			}
		});
		
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				server = new MyServer(ServerUI.this);
			}
		});
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(tMessage.getText()!="") {
					tShow.append("\nMessage: "+tMessage.getText()+"\n");
					server.sendMsg(tMessage.getText());
					tMessage.setText("");
					tShow.append("Sent.\n");
				}
				else {
					tShow.append("Cannot send empty message.\n");
				}
			}
		});
		btnSendAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clientSockets.clear();
				ListModel<Socket> listModel = list.getModel();
				for(int i=0;i<listModel.getSize();i++) {
					clientSockets.add(listModel.getElementAt(i));
				}
				if(tMessage.getText()!="") {
					tShow.append("\nMessage: "+tMessage.getText()+"\n");
					server.sendMsg(tMessage.getText());
					tMessage.setText("");
					tShow.append("Sent.\n");
				}
				else {
					tShow.append("Cannot send empty message.\n");
				}
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
					if(server!=null) {
						server.closeServer();
					}
					System.exit(0);
			}
		});
		
		JPanel leftPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 2));
		JLabel label = new JLabel("Port: ");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(label);
		topPanel.add(portField);
		topPanel.add(new JLabel(" "));
		topPanel.add(btnStart);
		centerPanel.setLayout(new BorderLayout(0, 15));
		centerPanel.add(list, BorderLayout.NORTH);
		centerPanel.add(tMessage, BorderLayout.CENTER);
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(btnSend, BorderLayout.NORTH);
		bottomPanel.add(btnSendAll, BorderLayout.CENTER);
		leftPanel.setLayout(new BorderLayout(0, 20));
		leftPanel.add(topPanel, BorderLayout.NORTH);
		leftPanel.add(centerPanel, BorderLayout.CENTER);
		leftPanel.add(bottomPanel, BorderLayout.SOUTH);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(this.tShow);
		tShow.setLineWrap(true);
		tShow.setAutoscrolls(true);
		this.tShow.setEditable(false);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(scrollPane, BorderLayout.CENTER);
		
		getContentPane().add(leftPanel, BorderLayout.WEST);
		getContentPane().add(rightPanel, BorderLayout.CENTER);
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 450);
		this.setLocation(100, 200);
		this.setVisible(true);
	}
}
