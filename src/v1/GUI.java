package v1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class GUI {
	
	private JFrame frame;
	private JPanel home_panel;
	private JPanel sensor_panel;
	private JPanel console_panel;
	private JButton connect_button;
	private JButton disconnect_button;
	private JButton launch_button;
	private JButton travel_button;
	private JButton clear_button;
	private JLabel connection_label;
	private JLabel power_label;
	private JLabel battery_label;
	private JLabel lidar1_label;
	private JLabel motor1_label;
	private JLabel motor2_label;
	private JLabel temperature_label;
	private JLabel balance_label;
	private JTextArea console_text;
	private JScrollPane console_scroll;
	private JTabbedPane tabbedPane;
	
	//Variables
	private boolean con_status = false;
	private int battery_percent = 0;
	private double lidar1 = 0; //In meters?
	private double balance = 0; //In degrees
	private int temperature = 0; //In celsius?
	private double motor1 = 0; //voltage?
	private double motor2 = 0; //voltage?
	
	
	//Constructor
	public GUI() {
		
		//Configure Drop Boxes
		String[] site_options = { "Site A", "Site B" };
		final JComboBox<String> site_decision = new JComboBox<String>(site_options);
		site_decision.setBackground(Color.WHITE);
		site_decision.setFocusable(false);
		
		String[] power_options = { "Off", "On" };
		final JComboBox<String> power_decision = new JComboBox<String>(power_options);
		power_decision.setBackground(Color.WHITE);
		power_decision.setFocusable(false);
				
		
		//Configure Buttons
		connect_button = new JButton("Connect to Robot");
		connect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Attempting to Connect\n");
				
				//Attempt to connect to robot if successful:
				con_status = true;
				connection_label.setText("Connection Status = " + con_status);
				
				//Set battery percent
				
				
				//If attempt unsuccessful
			}
		});
		
		disconnect_button = new JButton("Disconnect from Robot");
		disconnect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Attempting to Disconnect\n");
				
				//Attempt to disconnect to robot if successful:
				con_status = false;
				connection_label.setText("Connection Status = " + con_status);
				battery_percent = 0;

				
				//If attempt unsuccessful
			}
		});
		
		launch_button = new JButton("Launch BES");
		launch_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Attempting to Launch BES\n");
				
				//Check if established connection
				if(con_status == false) {
					
					console_text.append("Connection not established terminating launch\n");
					return;
				}

				
				//If attempt unsuccessful
			}
		});
		
		travel_button = new JButton("Travel");
		travel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Attempting to travel to " + site_decision.getSelectedItem().toString() + "\n");
				
				//Check if established connection
				if(con_status == false) {
					
					console_text.append("Connection not established terminating travel\n");
					return;
				}

				
				//If attempt unsuccessful
			}
		});
		
		clear_button = new JButton("Clear");
		clear_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.setText("");

			}
		});
			
		//Configure Label/Outputs
		connection_label = new JLabel("Connection Status = " + con_status);
		battery_label = new JLabel("Battery Percentage = " + battery_percent + "%");
		power_label = new JLabel("Power to BES:");
		lidar1_label = new JLabel("LIDAR 1: " + lidar1 + " meters");
		motor1_label = new JLabel("Motor 1: " + motor1 + " volts");
		motor2_label = new JLabel("Motor 2: " + motor2 + " volts");
		balance_label = new JLabel("Balance: " + balance + " degrees");
		temperature_label = new JLabel("Temperature: " + temperature + " celsius");
		
		//Configure Text Boxes
		console_text = new JTextArea("");
		console_text.setEditable(false);
		console_scroll = new JScrollPane(console_text);
		console_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		console_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		//Configure panels
		home_panel = new JPanel();
		home_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		home_panel.setLayout(new GridLayout(0, 1));
		home_panel.add(connect_button);
		home_panel.add(disconnect_button);
		home_panel.add(launch_button);
		home_panel.add(travel_button);
		home_panel.add(site_decision);
		home_panel.add(power_label);
		home_panel.add(power_decision);
		home_panel.add(connection_label);
		home_panel.add(battery_label);
		
		sensor_panel = new JPanel();
		sensor_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		sensor_panel.setLayout(new GridLayout(0, 1));
		sensor_panel.add(lidar1_label);
		sensor_panel.add(motor1_label);
		sensor_panel.add(motor2_label);
		sensor_panel.add(balance_label);
		sensor_panel.add(temperature_label);
		
		console_panel = new JPanel();
		console_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		console_panel.setLayout(new GridLayout(0, 1));
		console_panel.add(console_scroll);
		console_panel.add(clear_button);
		
		//Configure Tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Home", home_panel);
		tabbedPane.addTab("Sensors", sensor_panel);
		tabbedPane.addTab("Console", console_panel);
		
		//Configure Frame
		frame = new JFrame();
		frame.add(tabbedPane, BorderLayout.CENTER);
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
