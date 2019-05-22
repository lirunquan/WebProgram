package FileDownload;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.SwingConstants;
public class DownloadUI extends JFrame{
	public JButton downloadButton;
	public JTextField fileUrlField;
	public JTextField saveUrlField;
	public JButton cancelButton;
	public JTextArea showArea;
	public MultithreadDownload thread;
	public JLabel[] labels = new JLabel[MultithreadDownload.THREAD_COUNT+1];
	public DownloadUI() {
		// TODO Auto-generated constructor stub
		downloadButton = new JButton("Download");
		fileUrlField = new JTextField(40);
		saveUrlField = new JTextField(40);
		cancelButton = new JButton("Cancel");
		showArea = new JTextArea();
		showArea.setEditable(false);
		showArea.setAutoscrolls(true);
		showArea.setLineWrap(true);
		for (int i=0; i<labels.length; i++) {
			labels[i] = new JLabel();
			if (i==0) {
				labels[i].setText("Input the url of file to download and where to save, then click \"Download\"");
			}
			else {
				labels[i].setText("Thread No."+(i)+": Not start.");
			}
		}
		downloadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					thread = new MultithreadDownload(DownloadUI.this);
					thread.download();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		JPanel panel = new JPanel(new BorderLayout());
		JPanel upPanel = new JPanel(new GridLayout(3, 1, 0, 0));
		JPanel urlPanel = new JPanel();
		JPanel savePanel = new JPanel();
		JLabel urlLabel = new JLabel("URL: ");
		urlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		urlPanel.add(urlLabel);
		urlPanel.add(fileUrlField);
		JLabel saveLabel = new JLabel("Save: ");
		saveLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		savePanel.add(saveLabel);
		savePanel.add(saveUrlField);
		JPanel btnPanel = new JPanel();
		btnPanel.add(downloadButton);
		btnPanel.add(cancelButton);
		upPanel.add(urlPanel);
		upPanel.add(savePanel);
		upPanel.add(btnPanel);
		panel.add(upPanel, BorderLayout.NORTH);
		JPanel labelPanel = new JPanel(new GridLayout(MultithreadDownload.THREAD_COUNT+1, 1, 0, 0));
		for (JLabel label : labels) {
			labelPanel.add(label);
		}
		panel.add(labelPanel, BorderLayout.CENTER);
		getContentPane().add(panel);
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 450);
		this.setLocation(100, 200);
		this.setVisible(true);
	}
	public static void main(String[] args) {
		DownloadUI ui = new DownloadUI();
	}
}
