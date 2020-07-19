// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  String loginID;
  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginID, String host, int port) 
  {
	this.loginID=loginID;
    try 
    {
      client= new ChatClient(host, port, this);
      client.handleMessageFromClientUI("#login "+loginID);
    } 
    catch(IOException exception) 
    {
      System.out.println("Cannot open connection."
                + " Awaiting command.");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      BufferedReader fromConsole = 
        new BufferedReader(new InputStreamReader(System.in));
      String message;
		
      while (true) 
      {
    	message = fromConsole.readLine();
        if(message.charAt(0)=='#') {
        	chatCommands(message.split(" "));
        }
        else {
        	client.handleMessageFromClientUI(message);
        }
      } 
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  
  protected void chatCommands(String[] args) {
	  switch(args[0]) {
	  	case "#quit":
	  		try{
	  			client.closeConnection();
		  		client.quit();
	  		}catch(Exception e) {}
	  		break;
	  	case "#logoff":
	  		try {
	  			client.closeConnection();
	  		}catch(Exception e) {}
	  		break;
	  	case "#sethost":
	  		if(!client.isConnected())
	  			client.setHost(args[1]);
	  		break;
	  	case "#setport":
	  		if(!client.isConnected()) {
	  			try{
	  				client.setPort(Integer.parseInt(args[1]));
	  			}catch(NumberFormatException e){}
	  		}
	  		break;
	  	case "#login":
	  		try{
	  			client.openConnection();
	  			
	  		}catch(IOException e) {}
	  		break;
	  	case "#gethost":
	  		System.out.println("Host: "+client.getHost());
	  		break;
	  	case "#getport":
	  		System.out.println("Port: "+client.getPort());
	  		break;
	  }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "";
    String logID = "";
    int port = 0;  //The port number
    if(args.length==0) {
    	System.out.println("ERROR - No login ID specified. Connection Aborted.");
    	System.exit(1);
    }
    else if(args.length<2) {
    	logID=args[0];
    	host="localhost";
    	port=DEFAULT_PORT;
    }
    else{	
    	for(int i = 1; i<args.length;i++) {
    		try {
    			port=Integer.parseInt(args[i]);
    		}
    		catch(NumberFormatException|IndexOutOfBoundsException e){
 				if(e instanceof NumberFormatException)
 					host.replace("", args[i].toString());
 				if(e instanceof IndexOutOfBoundsException)
 					port=DEFAULT_PORT;
    		}
    	}
    }
    ClientConsole chat= new ClientConsole(logID, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
