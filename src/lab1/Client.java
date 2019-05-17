package lab1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client {

	// Input output Streams for receiving and sending data
	private ObjectInputStream inputStream;			// Reading from socket
	private ObjectOutputStream outputStream;		// Writing to the socket
	private Socket socket;							// Socket to pass data 

	ClientGUI clientGUI;							// To interact with the GUI
	
	private String server = "localhost";
	private String username;
	private int port;

	/*
	 * Constructor call to initialize client object with port number, user name and client gui instance
	 */
	Client(int port, String username, ClientGUI clientGUI) {
		this.port = port;
		this.username = username;
		this.clientGUI = clientGUI;
	}
	/*
	 * To fetch UserName of the client
	 * Input	: none
	 * Output	: Username of the client
	 */
	public String getUsername() {
		return username;
	}
	
	/*
	 * To set the username of the client
	 * Input	: user name
	 * output	: none
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * Start conversation with the server
	 */
	public boolean startClient() {
		// Connecting to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed, display error message
		catch(Exception ec) {
			showMessage("Error connectiong to server:" + ec);
			return false;
		}
		
		// Showing Success message for successful connection
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		showMessage(msg);
		
		//enable the client to send messages
		clientGUI.btnSendMessage.setEnabled(true);
	
		/* Creating Data Streams for input and output*/
		try
		{
			inputStream  = new ObjectInputStream(socket.getInputStream());
			outputStream= new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			// if failed, display the error message
			showMessage("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// Thread to handle server response
		new ServerReply().start();
		// send the username to server to register
		try
		{
			outputStream.writeObject(username);
		}
		catch (IOException eIO) {
			// if failed to connect, show appopriate message to the client
			showMessage("Exception doing login : " + eIO);
			// set all input and output streams to null
			disconnect();
			return false;
		}
		// notify client that the connection was successful
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 * input	: Message to display
	 * output	: Message is displayed on the GUI 
	 */
	private void showMessage(String msg) {
			clientGUI.updateClientLog(msg);		
	}
	
	/*
	 * To send messge to client(s) through the server
	 */
	void sendMessage(String[] msg) {
		try {
			outputStream.writeObject(msg);
		}
		catch(IOException e) {
			showMessage("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect
	 */
	private void disconnect() {
		try { 
			if(inputStream != null) inputStream.close();
			if(outputStream != null) outputStream.close();
			if(socket != null) socket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
			
	}

	/*
	 * continiously wait for reply from server and parse response 
	 */
	class ServerReply extends Thread {
		public void run() {
			while(true) {
				try {
					String[] response = new String[6];
					
					response = (String[]) inputStream.readObject();
					
					//get the content type to determine if a message or server sent the client list.					
					String contentType = response[2].substring(13);
					
					// if message 
					if(contentType.trim().equalsIgnoreCase("message")) {
						clientGUI.updateClientLog(response[6]);
					}
					//if client list
					else {
						clientGUI.updateClientList(response);
					}
				}
				catch(Exception e) {
					// connection failed. 
					showMessage("Server has close the connection: " + e);
					if(clientGUI != null) 
						clientGUI.connectionFailed();
					break;
				}
				
			}
		}
	}	
}
