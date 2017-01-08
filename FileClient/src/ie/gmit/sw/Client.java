package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client 
{
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException 
	{
		// Scanner console & XMLParser
		Scanner console = new Scanner(System.in);
		XMLParser p = new XMLParser();
		
		// Port number and host, download folder
		final int PORT = p.getPort();
		String host = p.getIp();
		String downloadDir = p.getDownloadDir();
		
		//Sockets to request
		Socket requestSocket = null;
		ObjectOutputStream out = null;
	 	ObjectInputStream in = null;
	 	boolean connected = false;
		
		while(true)
		{
			int option;
			System.out.println("Welcome to my Multi-threaded server application, \n please pic from the following options below:");
			System.out.println("1. Connect to Server");
			System.out.println("2. Print File Listing");
			System.out.println("3. Download File");
			System.out.println("4. Quit");
			System.out.println();
			System.out.print("Select options: ");
			
			//options input and if incorrect it will response
			try
			{
				option = console.nextInt();
			}
			catch(InputMismatchException e)
			{
				System.out.println("Incorrect input please restart the server and client");
				continue;
			}
			
			//Connect to server here 
			if(option == 1)
			{
				if (!connected)
				{
					requestSocket = new Socket(host, PORT);
					out = new ObjectOutputStream(requestSocket.getOutputStream());
					out.flush();
					in = new ObjectInputStream(requestSocket.getInputStream());
					connected = true;
					
					Request r = new Request("1", host, new Date());
					out.writeObject(r);
					out.flush();
					
					String message = null;
					do 
					{
						message = (String)in.readObject();
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}while (message == null);
					
					System.out.println(message);
				}else
				{
					System.out.println("You have already connected to the server");
				}
				
			}// end of connect
			//Print file listing
			else if (option == 2)
			{
				if (connected)
				{
					Request r = new Request("2", host, new Date());
					out.writeObject(r);
					out.flush();
					String message = null;
					do 
					{
						message = (String)in.readObject();
						try 
						{
							Thread.sleep(100);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}while (message == null);
					
					System.out.println("\n File in Server Folder: " + message);
				}else 
				{
					System.out.println("Error try to reconnect, please use option 1");
				}
			}
			//Download File option
			else if (option == 3)
			{
				if (connected)
				{
					Request r = new Request("3", host, new Date());
					out.writeObject(r);
					out.flush();
					
					console.nextLine();
					System.out.print("Please select from the print listing files to download: ");
					String file = console.nextLine();
					out.writeObject(file);
					out.flush();
					
					String message = null;
					do 
					{
						message = (String)in.readObject();
						try 
						{
							Thread.sleep(100);
						} catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}while (message == null);
					
					if(message != "File doesn't exists, please try again")
					{
						// Create a folder for downloads.
						//If folder already there
						//the following doesn't need to run
						new File(downloadDir).mkdir();
						PrintWriter pw = new PrintWriter(downloadDir + "/" + file);
						pw.println(message);
						pw.close();
					}
					else
					{
						System.out.println(message);
					}
				}else 
				{
					System.out.println("Error try to reconnect, please use option 1");
				}
			}
			// Exit server
			else if (option == 4)
			{
				//closes all connections
				System.exit(0);
				requestSocket.close();
				console.close();
			}else
			{
				System.out.println("Sorry please enter one of the options");
			}
		}
	}
}
