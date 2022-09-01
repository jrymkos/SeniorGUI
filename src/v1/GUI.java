package v1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI {
	
	private JFrame frame;
	private JPanel panel;
	private JButton connect_button;
	private JButton disconnect_button;
	private JLabel connection;
	
	//Variables
	private boolean con_status = false;
	
	//Constructor
	public GUI() {
		
		//Configure Buttons
		connect_button = new JButton("Connect to Robot");
		connect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				System.out.println("Attempting to Connect");
				
				//Attempt to connect to robot if successful:
				con_status = true;
				connection.setText("Connection Status = " + con_status);
				frame.remove(panel);
				
				//If unsuccessful
			}
		});
		
		disconnect_button = new JButton("Disconnect from Robot");
		disconnect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				System.out.println("Attempting to Disconnect");
				
				//Attempt to disconnect to robot if successful:
				con_status = false;
				connection.setText("Connection Status = " + con_status);

				
				//If unsuccessful
			}
		});
		
		//Configure Label/Outputs
		connection = new JLabel("Connection Status = " + con_status);
				
		//Configure panel
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(connect_button);
		panel.add(disconnect_button);
		panel.add(connection);
		
		//Configure Frame
		frame = new JFrame();
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Senior Design GUI");
		frame.pack();
		frame.setSize(1000, 500);
		frame.setResizable(true);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	}
	
	public static void main(String[] args) {
		new GUI();
	}

}
