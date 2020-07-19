// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package server;

import java.io.*;
import common.*;

//import com.lloseng.ocsf.server.ConnectionToClient;
import ocsf.server.ConnectionToClient;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  static ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI=serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  protected void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    if(msg.toString().charAt(0)=='#') {
    	if(client.getInfo("LoginID")==null) {
    		String type = "LoginID";
    		client.setInfo(type, msg.toString());
    		System.out.println(client+"Login ID set to "+ client.getInfo("LoginID"));
    	}
    	else {
    		try {
    			client.sendToClient("Error. Login ID cannot be changed once created.");
    		}catch(IOException e) {}
    	}
    }
    else
    	if(client.getInfo("LoginID")!=null) {
    		this.sendToAllClients(client.getInfo("LoginID").toString()+": "+msg);
    		System.out.println(client.getInfo("LoginID").toString()+": "+msg);
    	}
    	else {
    		try {
    			client.sendToClient("Error. Login ID cannot be changed once created.");
    			client.close();
    		}catch(IOException e) {}
    	}
  }

    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println(client.getName()+": Connected");
	  sendToAllClients(client.getName()+" has connected.");
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String msg = client.getName()+": Disconnected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);
  }
  
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  String msg = client.getName()+": Forcefully Disconnected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);  }
  
  public void sendToAllClients(Object msg)
  {
    Thread[] clientThreadList = getClientConnections();

    for (int i=0; i<clientThreadList.length; i++)
    {
      try
      {
        ((ConnectionToClient)clientThreadList[i]).sendToClient(msg);
      }
      catch (Exception ex) {}
    }
  }
  
  public void handleMessageFromServerUI(String message) {
	  System.out.println("SERVER MSG> "+message);
	  sendToAllClients("SERVER MSG> "+message);
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port, serverUI);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}

//End of EchoServer class
