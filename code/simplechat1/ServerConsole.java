// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import server.*;
import common.*;
import ocsf.server.*;

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
public class ServerConsole implements ChatIF 
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
  static EchoServer server;

  
  //Constructors ****************************************************

  public ServerConsole(int port) 
  {
      server= new EchoServer(port, this);
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
        	server.handleMessageFromServerUI(message);
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
	  		try {
	  			server.close();
	  		}catch(IOException e) {}
	  		break;
	  	case "#stop":
	  		server.stopListening();
	  		Thread[] clientThreadList = server.getClientConnections();
	        for (int i=0; i<clientThreadList.length; i++)
	        {
	           try
	           {
	             ((ConnectionToClient)clientThreadList[i]).close();
	           }
	           // Ignore all exceptions when closing clients.
	           catch(Exception ex) {}
	        }
	  		break;
	  	case "#close":
	  		server.stopListening();
	  		break;
	  	case "#setport":
	  		if(!server.isListening()) {
	  			try{
	  				server.setPort(Integer.parseInt(args[0]));
	  			}
	  			catch(NumberFormatException e) {}
	  		}
	  		break;
	  	case "#start":
	  		if(!server.isListening()) {
	  			try{
	  				server.listen();
	  			}catch(IOException e) {}
	  		}
	  		break;
	  	case "#getport":
	  		System.out.println("Port: "+server.getPort());
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
  
  public static void main(String[] args) 
  {
    int port = 0;  //The port number
	try {
    	port=Integer.parseInt(args[0]);
    }
    catch(NumberFormatException|ArrayIndexOutOfBoundsException e){
		port=DEFAULT_PORT;
    }
    ServerConsole chat= new ServerConsole(port);
    try{
    	server.listen();
    }catch(IOException e) {}
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class