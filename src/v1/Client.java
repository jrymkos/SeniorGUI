package v1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Connects to the robot as a client through socket connection
 * @author Jared Rymkos
 */

public class Client {
	
	private Socket s;
	
	public void connect() throws UnknownHostException, IOException {
		
		System.out.println("ayo");
		
		s = new Socket("192.168.122.101", 4998); //host and port
		PrintWriter pr = new PrintWriter(s.getOutputStream());
		pr.println("hello");
		pr.flush();
		pr.print(8);

	
	}
	
	public void disconnect() {
		
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
