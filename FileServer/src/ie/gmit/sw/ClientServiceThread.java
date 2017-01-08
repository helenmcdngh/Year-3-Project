package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class ClientServiceThread extends Thread 
{
	Socket clientSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	BlockingQueue<Request> queue;
	
	ClientServiceThread(Socket s, BlockingQueue<Request> queue)
	{
		this.clientSocket = s;
		this.queue = queue;
	}
	
	public void run()
	{
		// Connecting with client
		try 
		{
			queue.put(new Request("1", clientSocket.getInetAddress().getHostName(), new Date()));
		} 
		catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
		
		try{
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			Request r = null;
			
			do
			{
				do
				{
					r = (Request) in.readObject();
					Thread.sleep(1000);
				}while (r == null);
				
				//Add Requests 
				switch(r.getCommand())
				{
					// client option 1 connect to server
					case "1":
						sendMessage("Connected to the file server");
						queue.add(new Request("1", clientSocket.getInetAddress().getHostName(), new Date()));
						break;
					// client option 2 displays
					case "2":
						sendFileList();
						break;
					// client option 3 send file 
					case "3":
						sendFile();
						break;
					default:
						System.out.println("Command failure");
				}
			}
			while(!clientSocket.isClosed());
		}
		catch (Exception e)
		{
			System.out.println("Connection Lost, please try to reconnect to client");
			try 
			{
				queue.put(new Request("4", clientSocket.getInetAddress().getHostName(), new Date()));
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	// send message to client
	private void sendMessage(String msg)
	{
		try
		{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	// send file list
	private void sendFileList() throws InterruptedException
	{
		File folder = new File(".");
		File [] fileList = folder.listFiles();
		
		String files = "";
		
		for (int i = 0; i < fileList.length; i++) 
		{
			files += (fileList[i].getName()) + " "; 
		}
		sendMessage(files);
		
		//Records in file 
		queue.put(new Request("2", clientSocket.getInetAddress().getHostName(), new Date()));
	}
	
	// send actually file 
	private void sendFile() throws FileNotFoundException, InterruptedException
	{
		String message = null;
		do
		{
			try 
			{
				message = (String) in.readObject();
				Thread.sleep(100);
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}while(message == null);
		
		File get = new File(message);
		
		
		if (get.exists() && get.isFile())
		{ 
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(get)));
			String file = "";
			String next = null;
			
			try 
			{
				while((next = br.readLine()) != null)
				{
					// write file name
					file += next + "\n";
				}
				// Send file
				sendMessage(file); 
				br.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{ 
			sendMessage("File doesn't exist");
		}
		
		DownloadRequest r = new DownloadRequest("2", clientSocket.getInetAddress().getHostName(), new Date());
		r.setFilename(message);
		queue.put(r);
	}
}
