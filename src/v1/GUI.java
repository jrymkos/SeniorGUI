package v1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;
import javax.bluetooth.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;


public class GUI {
	
	private JFrame frame;
	private JPanel home_panel;
	private JPanel sensor_panel;
	private JPanel console_panel;
	private JPanel map_panel;
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
	
	//test bluetooth
	private LocalDevice localDevice; // local Bluetooth Manager
	private DiscoveryAgent discoveryAgent; // discovery agent
	
	
	//Constructor
	public GUI() {
		
		//Configure Drop Boxes
		String[] site_options = { "Site A", "Site B" };
		final JComboBox<String> site_decision = new JComboBox<String>(site_options);
		site_decision.setBackground(Color.WHITE);
		site_decision.setFocusable(false);
		site_decision.setMaximumSize(new Dimension(200, site_decision.getMinimumSize().height));
		
		
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
		travel_button.setAlignmentX(Component.CENTER_ALIGNMENT);
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
		clear_button.setPreferredSize(new Dimension(20, 40));
		clear_button.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        
        //Create Waypoints from coordinates
        Set<Waypoint> waypoints = new HashSet<Waypoint>(Arrays.asList(
                new DefaultWaypoint(new GeoPosition(28.6048112,-81.1900596)),
                new DefaultWaypoint(new GeoPosition(30.6048112,-81.1900596))));
        
        //Create painter with all waypoints
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);
        
        //Create painter with Route-painters and waypoint painters 
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(waypointPainter);
        
        //Configure Map
      	JXMapViewer mapViewer = new JXMapViewer();
        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        tileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        GeoPosition geo = new GeoPosition(28.6048112,-81.1900596);
        mapViewer.setZoom(19); //max zoom
        mapViewer.setAddressLocation(geo);
        
        //Add painters to map
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
              
        //Enable mobility
        MouseInputListener input = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(input);
        mapViewer.addMouseMotionListener(input);
        //mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        
		//Configure panels
		home_panel = new JPanel();
		home_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		home_panel.setLayout(new GridLayout(0, 1));
		home_panel.add(connect_button);
		home_panel.add(disconnect_button);
		home_panel.add(launch_button);
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
		BoxLayout boxlayout = new BoxLayout(console_panel, BoxLayout.Y_AXIS);
		console_panel.setLayout(boxlayout);
		console_panel.add(console_scroll);
		console_panel.add(clear_button);
		
		map_panel = new JPanel();
		map_panel.setLayout(new GridBagLayout());
		BoxLayout maplayout = new BoxLayout(map_panel, BoxLayout.Y_AXIS);
		map_panel.setLayout(maplayout);
		map_panel.add(mapViewer);
		map_panel.add(site_decision);
		map_panel.add(travel_button);
		
		//Configure Tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Home", home_panel);
		tabbedPane.addTab("Sensors", sensor_panel);
		tabbedPane.addTab("Console", console_panel);
		tabbedPane.addTab("Map", map_panel);
		
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
	
	//Ignore for now
	public void bluetooth() throws BluetoothStateException {
		
		System.out.println("test");
	    localDevice = null;
	    discoveryAgent = null;
	    // Retrieve the local device to get to the Bluetooth Manager
	    localDevice = LocalDevice.getLocalDevice();                   
	    // Servers set the discoverable mode to GIAC
	    localDevice.setDiscoverable(DiscoveryAgent.GIAC);                   
	    // Clients retrieve the discovery agent
	    discoveryAgent = localDevice.getDiscoveryAgent();      
	    
	};
	
	public static void main(String[] args) {
		new GUI();
	}

}
