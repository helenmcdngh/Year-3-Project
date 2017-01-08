package ie.gmit.sw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server 
{
	public static void main(String[] args) throws IOException 
	{
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(7777);
		BlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(7);
		
		System.out.println("Server listening on port 7777...");
		
		// Start the RequesterLogger
		RequestLogger rl = new RequestLogger(queue);
    	rl.start();
    	
	    while (true)
	    {
	    	Socket clientSocket = server.accept();
	    	ClientServiceThread clientThread = new ClientServiceThread(clientSocket, queue);
	    	clientThread.start();
	    }
	}

}
