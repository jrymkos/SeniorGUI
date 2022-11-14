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
	
	private Socket soc;
	private PrintWriter out;
	private BufferedReader in;
	private String host = "192.168.0.137"; //Use System.out.println(InetAddress.getLocalHost()); to find host IP
	private int port = 4999;
	
	//Robot Data
	private int Xcord; //Xcord from gps
	private int Ycord; //Ycord from gps
	private int numPeople; //amount of people from oak-d
	
	public void read() throws InterruptedException {
		
		while(true) {
			
			try {
				//Set robot data
				//Xcord = in.readLine();
				//Ycord = in.readLine();
				//numPeople = in.readLine();
				System.out.println(in.readLine());
				
				//If no response (which means the server stopped sending data break loop)
			} catch (IOException e) {
				break;
			}
			
			System.out.println("Requesting data:");
			//Request data again
			out.println("hello");
			out.flush();
			
			Thread.sleep(2000);
		}
	
		System.out.println("DONE");
	}
	
	public boolean connect() throws  InterruptedException, IOException {
		
			soc = new Socket();
			soc.connect(new InetSocketAddress(host, port), 1000);
			soc.setSoTimeout(5000); //5 second timeout
			
			//Setup out and in
			out = new PrintWriter(soc.getOutputStream());
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			
			//See if there is a response from server
			out.println("hello");
			out.flush();
			
			try {
				System.out.println(in.readLine());
				
				//Request data now 
				out.println("hello");
				out.flush();
				return true;
			} catch (IOException e) {
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
	
	public float getX() {
		return Xcord;
	}
	
	public float getY() {
		return Ycord;
	}
	
	public int getPeople() {
		return numPeople;
	}

}
