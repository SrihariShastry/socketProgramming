package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.text.DateFormatter;

import lab1.Server;


public class Server {
	ServerGUI serverGUI;
	int port;
	ArrayList<CThread> ClientList;
	int clientID;
	private boolean runT;
	
	/*
	 * Initialize server variables for server object with a port 
	 * Inputs	: Port number  ad server GUI instance
	 * Outputs	: None 
	 */
	public Server(int port, ServerGUI serverGUI) {
		// GUI object
		this.serverGUI = serverGUI;
		// port on which the server will work
		this.port = port;
		// ArrayList for the Client list
		ClientList = new ArrayList<CThread>();
		// Initialize Client ID
		clientID = 0;
	}
	
//	public Server(int port) {
//		this(port,null);
//	}
	
	// continiously listen to the socket for connection requests from clients
	public void startServer() {
		runT = true;
		try 
		{
			// socket used by server
			ServerSocket serverSocket = new ServerSocket(port);

			// continiously wait for request
			while(runT) 
			{
				// notify that we are waiting for connection requests
				display("Server waiting for Clients on port " + port + ". \n");
				
				Socket socket = serverSocket.accept();  	// accept connection
				
				// if server was to shut down
				if(!runT)
					break;
				
				// Create thread for each client
				CThread cThread = new CThread(socket);  
				//add client to client list
				ClientList.add(cThread);	
				//start the thread and listen to it coniniously 
				cThread.start();
			}
			// if server was asked to shut down
			try {				
				//close the client thread input and output streams of every client
				for(int i = 0; i < ClientList.size(); ++i) {
					CThread cThread = ClientList.get(i);
					try {
						cThread.inputStream.close();
						cThread.outputStream.close();
						cThread.socket.close();
					}
					catch(IOException ioE) {
						ioE.printStackTrace();
					}
				}
				
				serverSocket.close(); 	// close the server socket
			}
			catch(Exception e) {
				// if error in closing client or server
				display("Exception closing the server and clients: " + e);
			}
		}
		// if server crash
		catch (IOException e) {
            String msg = " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	/*
	 * display server messages
	 * Input	: Message to be displayed
	 * output	: None 
	 */
	private void display(String msg) {
		serverGUI.txtrServerLog.append(msg);
	}
	
	/*
	 *Stop the server  
	 */
	protected void stop() {
		//set run Thread to false to notify that the server has crashed 
		runT = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * create one thread to handle every client
	 */
	
	class CThread extends Thread {
		//socket, input output streams 
		Socket socket;
		ObjectInputStream inputStream;
		ObjectOutputStream outputStream;
		
		// unique id for each client. 
		int id;
		
		// the user of the Client
		String user;

		// Constructor
		CThread(Socket socket) {
			// unique id for each client
			id = ++clientID;
			this.socket = socket;
			try
			{
				// creating  data streams
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream  = new ObjectInputStream(socket.getInputStream());
				
				// read the user
				user = (String) inputStream.readObject();
				// notify that the user connected
				display(user + " just connected.");
			}
			catch (IOException e) {
				// if failed to create I/O data streams
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * returns the user for the particular client thread
		 */
		private String getUserName() {
			return user;
		}

		/*
		 * display messages on server Log
		 * Input: Message to write on log
		 */
		private void display(String string) {
				serverGUI.updateLog(string+ "\n");
			
		}
		
		/*
		 * send Response to the client
		 * Input: the response to be sent to the client
		 */
		private boolean respond(String[] respose) {
			// check if socket is connected
			if(!socket.isConnected()) {
				close();
				return false;
			}
			
			// if connected, then write to the output datastream
			try {
				outputStream.writeObject(respose);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		/*
		 * continiously listen to the client requests
		 * input	: none
		 * output 	: none
		 */
		public void run() {
			// Continiously listen to the cient for any requests
			boolean runT = true;
			while(runT) {
				// read a String (which is an object)
				String[] request = new String[7];		// request string
				String[] response = new String[7];		// response string
				try {
					//read client request
					request = (String[]) inputStream.readObject();	
					//get the current date and time for response 
					Date date = new Date();
				    String strDateFormat = "DD/MM/YYYY hh:mm:ss a";
				    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
				    // get the date and time in the format
				    String formattedDate= dateFormat.format(date);
				    
				    //prepare response by adding appropriate headers 				    
					response[0] = "HTTP/1.1 200 OK";
					response[1] = "Server: localhost";
					response[2] = "Content-type: message";
					response[3] = "Date: " + formattedDate;
					response[4] = "Content-Length: " + request[5].length();
					response[5] = "\r\n";
					response[6] = "";
					
					//if client asks for client list
					if(request[0].contains("GET")) {
						response[2] = "Content-type: client-list";
						// add all usernames to the list and send the response
						for(CThread ct : ClientList) {
							response[6] += ct.user + ",";
						}
						// add broadcast to the list
						response[6] += "broadcast";
						
						//unparsed HTTP request
						String requestLine= "";
						for(String s: request) {
							requestLine+=s;
						}
						//print unparsed HTTP request on server log
						serverGUI.updateLog(requestLine);
						respond(response);
					}
					
					// if client sends a 1-N message
					else if (request[0].contains("POST")&&(request[2].contains("BROADCAST")||request[2].contains("broadcast"))) 
					{
						//unparsed HTTP request
						String requestLine= "";
						for(String s: request) {
							requestLine+=s;
						}
						//print unparsed HTTP request on server log
						serverGUI.updateLog(requestLine);
						
						//get the message to be sent inside the response data
						response[6] = request[6];
						for(CThread ct : ClientList) {
//							send message to all clients
							if(!ct.respond(response))
								ClientList.remove(ct);	// if sending message fails then remove the client from client list
							}
						
					}
					
					// if client sends a 1-1 message
					else if(request[0].contains("POST") && !(request[2].contains("broadcast"))) {

						//unparsed HTTP request
						String requestLine= "";
						for(String s: request) {
							requestLine+=s;
						}
						//print unparsed HTTP request on server log
						serverGUI.updateLog(requestLine);
						
						//get the destination client name
						String userDest = request[2].substring(11).trim(); 
						//message to be sent
						response[6] = request[6];
						//check client list for destination client and send message 
						for(CThread ct : ClientList) {
							if(ct.getUserName().equalsIgnoreCase(userDest)) {
								ct.respond(response);
							}
						}
					}
					// if client requests logout
					else if(request[0].contains("DELETE")) {
						
						//unparsed HTTP request
						String requestLine= "";
						for(String s: request) {
							requestLine+=s;
						}
						//print unparsed HTTP request on server log
						serverGUI.updateLog(requestLine);
						
						// get the user of the client who wants to logout
						String[] userAgent = request[2].split(":");
						
						//remove the respective client from the list by closing input and output streams
						Iterator it = ClientList.iterator();
						while(it.hasNext()) {
							CThread ct = (CThread) it.next();
							if(userAgent[1].trim().equalsIgnoreCase(ct.getUserName())) {
								it.remove();
							}
							
						}
					}
				}
				catch (IOException e) {
					// if probelm in reading message
					display(user + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients 
			ClientList.remove(id-1);
			close();
		}
		
		// closing I/O streams
		private void close() {
			try {
				if(outputStream != null) outputStream.close();
				if(inputStream != null) inputStream.close();
				if(socket != null) socket.close();
				serverGUI.updateLog(user + "Disconnected" );
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		// logout user remove 
		synchronized void remove(int id) {
			// 
			for(CThread ct : ClientList ) {
//				CThread ct = ClientList.get(i);
				// found it
				if(ct.id == id) {
					ClientList.remove(ct);
					return;
				}
			}
		}
	}
}

