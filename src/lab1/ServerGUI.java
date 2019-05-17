package lab1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;

import lab1.Server;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;

public class ServerGUI implements ActionListener,WindowListener{

	JFrame frame;
	JTextField textPortNum;
	JTextArea txtrServerLog;
	Server server;
	JButton btnStartServer;
	boolean keepGoing;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 490, 379);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textPortNum = new JTextField();
		textPortNum.setText("3030");
		textPortNum.setColumns(10);
		
		JLabel lblPortNumber = new JLabel("PORT Number");
		
		btnStartServer = new JButton("Start Server");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblServerLog = new JLabel("Server Log");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(40)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 377, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblServerLog)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblPortNumber)
							.addGap(33)
							.addComponent(textPortNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnStartServer)))
					.addContainerGap(55, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textPortNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPortNumber)
						.addComponent(btnStartServer))
					.addGap(35)
					.addComponent(lblServerLog)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(38, Short.MAX_VALUE))
		);
		
		txtrServerLog = new JTextArea();
		scrollPane_1.setViewportView(txtrServerLog);
		frame.getContentPane().setLayout(groupLayout);
		btnStartServer.addActionListener(this);
	}


	// handle button clicks 
	@Override
	public void actionPerformed(ActionEvent e) {
				// stop server if already running
				if(server != null) {
					server.stop();
					server = null;
					textPortNum.setEditable(true);
					btnStartServer.setText("Start");
					return;
				}
				// start the server by getting the port number 
				int port;
				try {
					port = Integer.parseInt(textPortNum.getText().trim());
				}
				catch(Exception error) {
					updateLog("Invalid port number");
					return;
				}
				
				// ceate a new Server
				server = new Server(port, this);
				// Start server Thread and continiously listen to the client for requests
				new ServerRunning().start();
				
				// set text of start button to stop
				btnStartServer.setText("Stop");
				// port number should not be editable after server is switched on
				textPortNum.setEditable(false);
		
	}
	
	/*
	 * Print messages on server log
	 * Input: Message to put on log
	 * Output: None
	 */
	void updateLog(String message) {
		txtrServerLog.append(message);
	}
	
	//	
	class ServerRunning extends Thread {
		public void run() {
			
			//continue exeuting until server crash
			server.startServer(); 

			// server crashed
			btnStartServer.setText("Start");
			textPortNum.setEditable(true);
			updateLog("Server crashed\n");
			server = null;
		}
	}

	/*
	 * handle window closing 
	 * Input	: Closing event
	 * Output	: None 
	 */

	@Override
	public void windowClosing(WindowEvent e) {
	
		if(server != null) {
			try {
				//close server connection
				server.stop();			
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
