package ie.gmit.sw;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser
{
	// variables
	private String username;
	private String ip;
	private int port;
	private String downloadDir;
	
	public XMLParser(){
		parse();
	}

	//Resources
	//https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	public void parse()
	{
		try
		{
			File fXmlFile = new File("user.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("client-config");
			
			Node root = nList.item(0);

			if (root.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element e = (Element) root;
				username = e.getAttribute("username");
				ip = e.getElementsByTagName("server-host").item(0).getTextContent();
				port = Integer.parseInt(e.getElementsByTagName("server-port").item(0).getTextContent());
				downloadDir = e.getElementsByTagName("download-dir").item(0).getTextContent();
			}
	    } 
		catch (Exception e) 
		{
	    	e.printStackTrace();
	    }
	}

	//GETTERS
	public String getUsername() {
		return username;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getDownloadDir() {
		return downloadDir;
	}
}
