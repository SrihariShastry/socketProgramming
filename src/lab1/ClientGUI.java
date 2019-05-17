package lab1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import lab1.Client;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI implements ActionListener{

	JFrame frame;
	JTextField txtMessage;
	JTextField txtUsername;
	Client client;
	JTextField textPortNumber;
	JTextArea txtrReplies;
	JButton btnConnect;
	JButton btnSendMessage;
	JButton btnDisconnect;
	JButton btnClientList;
	JComboBox comboBox;
	boolean connected;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize UI elements of the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 581, 535);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnSendMessage = new JButton("Send Message");
		btnSendMessage.addActionListener(this);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);		
		
		txtMessage = new JTextField();
		txtMessage.setColumns(20);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		
		JLabel lblUserName = new JLabel("User Name");
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(this);
		
		btnClientList = new JButton("Client List");
		btnClientList.addActionListener(this);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"broadcast"}));
		comboBox.setSelectedIndex(0);
		
		textPortNumber = new JTextField();
		textPortNumber.setColumns(10);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addComponent(btnDisconnect)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 347, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblUserName)
							.addGap(18)
							.addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblPortNumber)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnConnect)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSendMessage))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnClientList)))
					.addContainerGap(204, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(16)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUserName)
						.addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPortNumber)
						.addComponent(textPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnConnect)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSendMessage))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnClientList))
					.addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnDisconnect)
					.addGap(12))
		);
		
		//enable and disable appropriate buttons
		txtrReplies = new JTextArea();
		scrollPane.setViewportView(txtrReplies);
		frame.getContentPane().setLayout(groupLayout);
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);		// cant disconnect when the client has not connected yet
		btnClientList.setEnabled(false);		// cant request for 
		btnSendMessage.setEnabled(false);		// cant send message without logging in
	}

	/*
	 * Put message on the UI text area
	 * Input:message to display
	 * Output: none 
	 */
	public void updateClientLog(String msg) {
		txtrReplies.append("\n" + msg);
	}
	
	/*
	 * On connection failure, relevent buttons are to be disabled
	 */
	void connectionFailed() {
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		btnClientList.setEnabled(false);
		txtMessage.setText("Enter your username");
		textPortNumber.setText("");
		textPortNumber.setEditable(true);
		connected = false;
	}

	/*
	 * send request to server based on the button pressed.
	 * Inputs: Event
	 * Output: None
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();			// get source button
		
		String[] request = new String[7];	// request string
		
		// Client decides to Logout
		if(o == btnDisconnect) {
			// form request with appropriate headers
			request[0] = "DELETE / HTTP/1.1";
			request[1] = "Host: localhost";
			request[2] = "User-Agent: " + client.getUsername();
			request[3] = "Content-Type: Text";
			request[4] = "Content-Length =  0";
			request[5] = "\r\n";
			request[6] = " ";
			client.sendMessage(request);
			
//			handle UI changes after disconnect
			btnConnect.setEnabled(true);
			btnDisconnect.setEnabled(false);
			btnClientList.setEnabled(false);
			txtMessage.setEditable(false);
			txtUsername.setEditable(true);
			textPortNumber.setEditable(true);
			return;
		}
		else if(o == btnClientList) {
			// form request with appropriate headers
			request[0] = "GET CLIENTLIST HTTP/1.1";
			request[1] = "Host: localhost";
			request[2] = "User-Agent: " + client.getUsername();
			request[3] = "Content-Type: client-list";
			request[4] = "Content-Length =  0";
			request[5] = "\r\n";
			request[6] = " ";
			client.sendMessage(request);
			return;
		}
		else if(o==btnSendMessage) {
			// Check if broadcast(1-N) or to selected receiver(1-1).
			String to = comboBox.getSelectedItem().toString();
			request[0] = "POST HTTP/1.1";
			request[1] = "Host: localhost";
			request[2] = "User-Agent: " + to;
			request[3] = "Content-Type: message";
			request[4] = "Content-Length = " + txtMessage.getText().length();
			request[5] = "\r\n";
			request[6] = client.getUsername();
			
			//indicate if broadcast or selected client
			if(request[2].contains("broadcast")) 
				request[6] += " (1-N): "+txtMessage.getText();
			else 
				request[6] += " (1-1) : " +txtMessage.getText();
			
			//send the message
			client.sendMessage(request);
			//empty the message area for new message to be written.
			txtMessage.setText("");
			return;
		}
		else if(o == btnConnect) {
			String username = txtUsername.getText().trim();
			String portNumber = textPortNumber.getText().trim();
			
			// if username or if port number is empty, do nothing.
			if(username.length() == 0 || portNumber.length() == 0)
				return;
			
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				//if parse error
				return;  
			}
			
			// create a new client object which handles reponses from server.
			client = new Client(port, username, this);
			// start the client thread which continiously listens to the server for responses.
			if(!client.startClient()) 
				return;
			// empty the message area for new message to be typed.
			txtMessage.setText("");
			
			// connected status is true.
			connected = true;
			
			//UI elemets are to be set after connection
			btnConnect.setEnabled(false);
			btnDisconnect.setEnabled(true);
			btnClientList.setEnabled(true);
			textPortNumber.setEditable(false);
			txtUsername.setEditable(false);
		}

	}

	/*
	 * updates the UI dropdown which shows the client list
	 * Input: Response from the server which contains the client list
	 * Output: None
	 */
	public void updateClientList(String[] response) {
		
		// split the response to get individual clients
				String[] list = response[6].split(",");
		// update the client list dropdown
				comboBox.setModel(new DefaultComboBoxModel(list));			
	}

}
