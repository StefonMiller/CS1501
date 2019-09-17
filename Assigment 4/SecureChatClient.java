/*
 * Stefon Miller
 * CS1501 Summer 2019
 * 
 * 
 * Secure chat client.  GUI is from the ImprovedChatClient.java class
 * provided by professor Ramirez.  This client provides the same functionality
 * as ImprovedChatClient but encrypts all messages sent between client and server
 */

import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener 
{
	
    public static final int PORT = 8765;									//Port used by server
    ObjectInputStream myReader;												//ObjectOutputStream for reading
    ObjectOutputStream myWriter;											//ObjectInputStream for writing
    
    //GUI elements
    JTextArea outputArea;													
    JLabel prompt;
    JTextField inputField;
    
    String myName, serverName;												//User and server names
	Socket connection;														//Current connection
	BigInteger e;															//E value for RSA
	BigInteger n;															//N value for RSA
	String cType;															//Cypher type used
	SymCipher cyph;															//Cypher object created
	BigInteger key;															//Key sent by server
	BigInteger eKey;														//Encrypted key sent to server
	byte[] eName;															//Encrypted name sent to server
	
    public SecureChatClient()
    {
        try 
        {
        	//Get server name and open a socket to the specified port
        	serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        	InetAddress addr = InetAddress.getByName(serverName);
        	connection = new Socket(addr, PORT);   // Connect to server with new socket
        	
        	//Create objectinput and objectoutput streams, flushing the outputstream to avoid deadlock
        	myWriter = new ObjectOutputStream(connection.getOutputStream());
        	myWriter.flush();
        	myReader = new ObjectInputStream(connection.getInputStream());
        	
        	//Get e, n, and cypher fields from the server and print them out
        	e = (BigInteger) myReader.readObject();
        	System.out.println("Client recieved E value: " + e);
        	
        	n = (BigInteger) myReader.readObject();
        	System.out.println("Client recieved N value: " + n);
        	
        	cType = (String)myReader.readObject();
        	System.out.println("Client recieved cypher type: " + cType);
        	
        	//Create cypher object based on cType, if the type is not valid, exit
        	if(cType.toLowerCase().equals("sub"))
			{
        		cyph = new Substitute();
			}
        	else if(cType.toLowerCase().equals("add"))
			{
        		cyph = new Add128();
			}
        	else
        	{
        		System.out.println("Invalid cypher type, only Add128 and Substitution allowed.");
        		System.exit(0);
        	}
        	
        	//Get the key from the server and make a positive BigInteger object from it, then print it out
        	key = new BigInteger(1, cyph.getKey());
        	System.out.println("Symmetric key is " + key);
        	
        	//Encrypt the key and output the result, then send it to the server
        	eKey = key.modPow(e, n);
        	System.out.println("RSA encrypted key is " + eKey);
        	myWriter.writeObject(eKey);
        	myWriter.flush();
        	
        	//Get user name, encode it, and send it to the server
        	myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        	eName = cyph.encode(myName);
        	myWriter.writeObject(eName);   // Send name to Server.  Server will need this to announce sign-on and sign-off of clients
        	myWriter.flush();
        	
        	this.setTitle(myName);      		// Set title to identify chatter
        	Box b = Box.createHorizontalBox();  // Set up graphical environment for user
        	outputArea = new JTextArea(8, 30);
        	outputArea.setEditable(false);
        	b.add(new JScrollPane(outputArea));

        	outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        	inputField = new JTextField("");  // This is where user will type input
        	inputField.addActionListener(this);

        	prompt = new JLabel("Type your messages below:");
        	Container c = getContentPane();

        	c.add(b, BorderLayout.NORTH);
        	c.add(prompt, BorderLayout.CENTER);
        	c.add(inputField, BorderLayout.SOUTH);

        	Thread outputThread = new Thread(this);  // Thread is to receive strings
        	outputThread.start();                    // from Server

        	addWindowListener(new WindowAdapter()
        	{
                    public void windowClosing(WindowEvent e)
                    { 
                    	try 
                    	{
                    		myWriter.writeObject(cyph.encode("CLIENT CLOSING"));
                    		myWriter.flush();
                    	} 
                    	catch (IOException e1) 
                    	{
                    		e1.printStackTrace();
                    	}
                    	System.exit(0);
                     }
        	});

        	setSize(500, 200);
        	setVisible(true);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try 
             {
            	//Get encoded message from server and print it out
            	byte[] encMsg = (byte[]) myReader.readObject();
            	System.out.println("Byte array recieved from server: " + Arrays.toString(encMsg));
            	//Decrypt message and print its byte array to console
                String currMsg = cyph.decode(encMsg);
                System.out.println("Byte array of decoded message: " + Arrays.toString(currMsg.getBytes()));
                //Print decoded string and finally append it to the chat client GUI
                System.out.println("Decoded message: " + currMsg);
			    outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                //System.out.println(e +  ", closing client!");
                break;
             }
        }
        try 
    	{
    		myWriter.writeObject(cyph.encode("CLIENT CLOSING"));
    		myWriter.flush();
    	} 
    	catch (IOException e1) 
    	{
    		e1.printStackTrace();
    	}
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");
        try 
        {	
        	//Insert username and colon in the front of our message, then print it out
        	currMsg = myName + ":" + currMsg;
        	System.out.println("Original message: " + currMsg);
        	
        	//Fill a byte array with the current message's bytes,print it to console, then encrypt it
        	byte[] b = currMsg.getBytes();
        	System.out.println("Byte array of message: " + Arrays.toString(b));
        	b = cyph.encode(currMsg);
        	
        	//Print encrypted message and send it to the server
        	System.out.println("Byte array of encrypted message: " + Arrays.toString(b));
			myWriter.writeObject(b);
			myWriter.flush();
		} 
        catch (IOException e1) 
        {
        	System.out.println("Message not sent");
		}   // Add name and send it to server
    }

    public static void main(String [] args)
    {
         SecureChatClient SR = new SecureChatClient();
         SR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}


