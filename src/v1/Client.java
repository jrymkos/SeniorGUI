package v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Connects to the robot as a client through socket connection
 * @author Jared Rymkos
 */

public class Client  {
	
	private Socket soc= new Socket();
	private PrintWriter out;
	private BufferedReader in;
	private String host = "****"; //Use System.out.println(InetAddress.getLocalHost()); to find host IP
	private int port = 4999;
	
	//Robot Data
	private String buffer = "";
	private char curByte;
	private double Xcord; //Xcord from gps
	private double Ycord; //Ycord from gps
	private int numPeople; //amount of people from oak-d
	
	//Reads data from arduino while connected
	public void read() throws InterruptedException {
		
		//This counts how long it takes for data to get sent
		//If data is not sent in a 10 second period it will disconnect
		int counter = 0;
		
		while(soc.isClosed() == false) {

			try {
				
				while(in.ready()) {
					
					counter = 0; //reset
					
					//Check identifier character
					char identifier = (char) in.read();
					
					if(Character.compare(identifier, 'X') == 0) {
						
						String data = in.readLine();
						Xcord = Double.parseDouble(data);
						System.out.println("X cord is: " + Xcord);
					}
					
					else if(Character.compare(identifier, 'Y') == 0) {
						
						String data = in.readLine();
						Ycord = Double.parseDouble(data);
						System.out.println("Y cord is: " + Ycord);
					}
					
					else {
						System.out.println("Error in identifier byte");
					}
					
					
				}

				//If no response (which means the server stopped sending data break loop)
			}catch (IOException e) {
				System.out.println("Error, no response");
			}
			
			Thread.sleep(100);
			
			//Check if counter has gone too high
			if(counter >= 10000) {
				break;
			}
			counter += 100;
		}
	
		System.out.println("Disconnected");
	}
	
	public boolean connect() throws  InterruptedException {
		
		//Attempt to connect
		soc = new Socket();

		try {
			soc.connect(new InetSocketAddress(host, port), 1000); //1 second timeout for initial connection
			soc.setSoTimeout(5000); //5 second timeout
			
			//Setup out and in
			out = new PrintWriter(soc.getOutputStream());
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 

	}

	public void disconnect() {
		
		try {
			soc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Sends byte to Server
	// 0 = Stop
	// A = Travel to Site A
	// B = Travel to Site B
	// C = Travel to Site C
	public void sendSite(String site) {
		
		if((soc.isClosed() == false) && (soc.isConnected()) == true) {
			
			if(site.equals("Stop")) out.println("0");
			else if(site.equals("Site A")) out.println("A");
			else if(site.equals("Site B")) out.println("B");
			else if(site.equals("Site C")) out.println("C");
			out.flush();
				
		}
		
		else {
			System.out.println("Cant travel");
		}
	}
	
	public boolean isConnected() {
		if((soc.isClosed() == false) && (soc.isConnected() == true)) return true;
		else return false;
	}
	
	public double getX() {
		return Xcord;
	}
	
	public double getY() {
		return Ycord;
	}
	
	public int getPeople() {
		return numPeople;
	}

}
