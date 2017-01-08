package ie.gmit.sw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class RequestLogger extends Thread
{
	private BlockingQueue<Request> q;
	private FileWriter fw;
	
	public RequestLogger(BlockingQueue<Request> a)
	{
		this.q = a;
	}
	
	public void run()
	{
		try 
		{
			fw = new FileWriter(new File("log.txt"));
		
			while(true){
				Request r = q.poll();
				
				if (r != null)
				{
					if (r.getCommand() == "1")
					{
						// 
						fw.write("\n[INFO] ]Client Listing requested by" + r.getHost() + " @ " + r.getDate() + "\n");
						fw.flush();
					}
					else if (r.getCommand() == "2")
					{
						fw.write("\n[INFO] File listing requested by " + r.getHost() + " @ " + r.getDate() + "\n");
						fw.flush();
					}
					else if (r.getCommand() == "3")
					{
						fw.write("\n[INFO] File downloaded from " + r.getHost() + " @ " + r.getDate() + "\n");
						fw.flush();
					} 
					else if (r.getCommand() == "4")
					{
						fw.write("\n[INFO] Disconnected " + r.getHost() + " @ " + r.getDate() + "\n");
						fw.close();
					}
					r = null;
				}
				
				else 
				{
					try 
					{
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) 
		{

		}
	}	

}
