package v1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
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

/**
 * Creates GUI to control the Robot
 * @author Jared Rymkos
 */

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
	private JButton stop_button;
	private JButton clear_button;
	private JLabel connection_label;
	private JLabel power_label;
	private JLabel battery_label;
	private JLabel people_label;
	private JLabel motor1_label;
	private JLabel motor2_label;
	private JLabel speed_label;
	private JLabel balance_label;
	private JTextArea console_text;
	private JTextField launch_text;
	private JScrollPane console_scroll;
	private JTabbedPane tabbedPane;
	
	//Variables
	private boolean con_status = false;
	private int battery_percent = 0;
	private int people = 0; //amount of people identified
	private double balance = 0; //In degrees
	private double speed = 0; //In m/s
	private double motor1 = 0; //voltage?
	private double motor2 = 0; //voltage?
	
	//Define Sites and Map stuff
	private GeoPosition siteA_coords;
	private GeoPosition siteB_coords;
	private GeoPosition siteC_coords;
	private GeoPosition robot_coords;
	private RoutePainter routePainter;
	private WaypointPainter<MyWaypoint> robotPainter;
	private CompoundPainter<JXMapViewer> painter;
	private JXMapViewer mapViewer;
	private MyWaypoint robot_marker;
	private String curRoute = "";
	private MyWaypoint siteA;
	private MyWaypoint siteB;
	private MyWaypoint siteC;
					
	//Constructor
	@SuppressWarnings("unchecked")
	public GUI() {
        
		//Configure Drop Boxes
		String[] site_options = { "Site A", "Site B", "Site C" };
		final JComboBox<String> site_decision = new JComboBox<String>(site_options);
		site_decision.setBackground(Color.WHITE);
		site_decision.setFocusable(false);
		site_decision.setMaximumSize(new Dimension(200, site_decision.getMinimumSize().height));
		
		Client client = new Client();
		
		
		//Configure Buttons
		connect_button = new JButton("Connect to Robot");
		connect_button.setMaximumSize(new Dimension(1000, 100));
		connect_button.setAlignmentX(Component.CENTER_ALIGNMENT);
		connect_button.setFont(new Font ("TimesNewRoman", Font.BOLD, 20));
		connect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if(con_status == true) {
					console_text.append("Already connected\n");
				}
				else {
					console_text.append("Attempting to Connect\n");
					
					//Configure thread for connection
					//Use a new thread to deal with connection
					Thread t1 = new Thread(new Runnable() {
						
						public void run() {
							try {
								
								if(client.connect() == true) {
									console_text.append("Connection Successful\n");
									con_status = true;
									connection_label.setText("Connection Status = " + con_status);
									
									//Set battery percent
									
									//Add robot marker to map
									painter.addPainter(robotPainter);
									
									//Keep printing to console until stopped.
									while(true) {
										String line = client.read();
										if(line.equals("false")) break;
										else console_text.append(line + "\n");
									}; 
									
									//After stopping or losing connection
									console_text.append("Disconnecting\n");
									
									client.disconnect();
									
									//Update
									con_status = false;
									connection_label.setText("Connection Status = " + con_status);
									battery_percent = 0;
									
									//Remove robot/paths from map and restore colors
									painter.removePainter(routePainter);
									painter.removePainter(robotPainter);
									curRoute = "";
									siteA.setGray();
									siteB.setGray();
									siteC.setGray();
									
								}
								
								else {
									console_text.append("Failed to Connect\n");
								}
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					
					t1.start();
					
				}
			}
		});
		
		disconnect_button = new JButton("Disconnect from Robot");
		disconnect_button.setFont(new Font ("TimesNewRoman", Font.BOLD, 20));
		disconnect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				client.disconnect();
				
			}
		});
		
		travel_button = new JButton("Travel");
		travel_button.setAlignmentX(Component.CENTER_ALIGNMENT);
		travel_button.setMaximumSize(new Dimension(200, 200));
		travel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Attempting to travel to " + site_decision.getSelectedItem().toString() + "\n");
				
				//Check if established connection
				if(con_status == false) {
					
					console_text.append("Connection not established terminating travel\n");
					return;
				}
				
				//Show path to Site on Map
				painter.removePainter(routePainter); //first remove old route
				
				if(site_decision.getSelectedItem().toString().equals("Site A")) {
					console_text.append("Showing path from robot to SiteA\n");
					List<GeoPosition> path = Arrays.asList(siteA_coords, robot_coords);
			        routePainter = new RoutePainter(path);
			        siteA.setRed();
			        siteB.setGray();
			        siteC.setGray();
			        client.sendSite(site_decision.getSelectedItem().toString());
				}
				
				else if(site_decision.getSelectedItem().toString().equals("Site B")) {
					console_text.append("Showing path from robot to SiteB\n");
					List<GeoPosition> path = Arrays.asList(siteB_coords, robot_coords);
			        routePainter = new RoutePainter(path);
			        siteB.setRed();
			        siteA.setGray();
			        siteC.setGray();
			        client.sendSite(site_decision.getSelectedItem().toString());
				}
				
				else if(site_decision.getSelectedItem().toString().equals("Site C")) {
					console_text.append("Showing path from robot to SiteC\n");
					List<GeoPosition> path = Arrays.asList(siteC_coords, robot_coords);
			        routePainter = new RoutePainter(path);
			        siteC.setRed();
			        siteB.setGray();
			        siteA.setGray();
			        client.sendSite(site_decision.getSelectedItem().toString());
				}
				painter.addPainter(routePainter);
				curRoute = site_decision.getSelectedItem().toString();
				
				//If attempt unsuccessful
			}
		});
		
		stop_button = new JButton("Stop");
		stop_button.setAlignmentX(Component.CENTER_ALIGNMENT);
		stop_button.setMaximumSize(new Dimension(200, 200));
		stop_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				console_text.append("Stopping Travel \n");
				
				//Send signal to ESP32
				client.sendSite("Stop");
				
				//Clean up Map
				curRoute = "";
				painter.removePainter(routePainter);
				siteA.setGray();
				siteB.setGray();
				siteC.setGray();
	
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
		connection_label.setFont(new Font ("TimesNewRoman", Font.BOLD, 20));
		connection_label.setHorizontalAlignment(JTextField.CENTER);

		battery_label = new JLabel("Battery Percentage = " + battery_percent + "%");
		battery_label.setFont(new Font ("TimesNewRoman", Font.BOLD, 20));
		people_label = new JLabel("Number of people found: " + people);
		motor1_label = new JLabel("Motor 1: " + motor1 + " volts");
		motor2_label = new JLabel("Motor 2: " + motor2 + " volts");
		balance_label = new JLabel("Balance: " + balance + " degrees");
		speed_label = new JLabel("Speed: " + speed + " meters per second");
		
		//Configure Text Boxes
		console_text = new JTextArea("");
		console_text.setFont(new Font ("TimesNewRoman", Font.BOLD, 20));
		console_text.setEditable(false);
		console_scroll = new JScrollPane(console_text);
		console_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		console_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		launch_text = new JTextField("Not Ready to Launch");
		launch_text.setFont(new Font ("TimesNewRoman", Font.BOLD, 30));
		launch_text.setBackground(Color.PINK);
		launch_text.setEditable(false);
		launch_text.setHorizontalAlignment(JTextField.CENTER);
		
        
		//Set Sites
		siteA_coords = new GeoPosition(28.6745596,-81.1835509);
		siteB_coords = new GeoPosition(28.6744996,-81.1834509);
		siteC_coords = new GeoPosition(28.6745996,-81.1833509);
		robot_coords = new GeoPosition(28.6744496,-81.1833509);
		
		siteA = new MyWaypoint("A", Color.GRAY, siteA_coords);
		siteB = new MyWaypoint("B", Color.GRAY, siteB_coords);
		siteC = new MyWaypoint("C", Color.GRAY, siteC_coords);
        Set<MyWaypoint> sites = new HashSet<MyWaypoint>(Arrays.asList(
        		siteA,
                siteB,
                siteC));
        
        //Create painter with all sites
        WaypointPainter<MyWaypoint> sitePainter = new WaypointPainter<MyWaypoint>();
        sitePainter.setWaypoints(sites);
        sitePainter.setRenderer(new FancyWaypointRenderer("house.png"));
        
        //Create robot marker and painter
        robot_marker = new MyWaypoint("", Color.BLUE, robot_coords);
        Set<MyWaypoint> robot = new HashSet<MyWaypoint>(Arrays.asList(
                robot_marker));
        
        //Create painter with robot
        robotPainter = new WaypointPainter<MyWaypoint>();
        robotPainter.setWaypoints(robot);
        robotPainter.setRenderer(new FancyWaypointRenderer("waypoint_white.png"));
		
        //Configure Map
      	mapViewer = new JXMapViewer();
        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        tileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        GeoPosition geo = new GeoPosition(28.6746596,-81.1835509); //28.6746596,-81.1835509 //28.6041894,-81.1906883
        mapViewer.setZoom(19); //max zoom
        mapViewer.setAddressLocation(geo);
        
        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        
        //Add markers
        painter = new CompoundPainter<JXMapViewer>(sitePainter);
        painter.addPainter(sitePainter);
        mapViewer.setOverlayPainter(painter);
              
        //Enable mobility
        MouseInputListener input = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(input);
        mapViewer.addMouseMotionListener(input);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        
		//Configure panels
		home_panel = new JPanel();
		home_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		home_panel.setLayout(new GridLayout(0, 1));
		home_panel.add(connect_button);
		home_panel.add(disconnect_button);
		home_panel.add(connection_label);
		home_panel.add(launch_text);
		//home_panel.add(battery_label);
		
		sensor_panel = new JPanel();
		sensor_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		sensor_panel.setLayout(new GridLayout(0, 1));
		sensor_panel.add(people_label);
		sensor_panel.add(motor1_label);
		sensor_panel.add(motor2_label);
		sensor_panel.add(balance_label);
		sensor_panel.add(speed_label);
		
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
		map_panel.add(stop_button);
		
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
		
		//Configure timer interrupt to update map
		int delay = 500; //in msec
		ActionListener mapUpdater = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				mapViewer.repaint();
				
				//Change launch text
				updateLaunch(client.getLaunch());
				
				
				if(client.isConnected() == true) {
					
					//Update robots coords
					robot_coords = new GeoPosition(client.getX(), client.getY());
					robot_marker.setPosition(robot_coords);
					
					//Update site lines only if the robot is moving to a Site
					if(!(curRoute.equals(""))) {
						
						painter.removePainter(routePainter);
						
						if(curRoute.equals("Site A")) {
							List<GeoPosition> path = Arrays.asList(siteA_coords, robot_coords);
							routePainter = new RoutePainter(path);
						}
						else if (curRoute.equals("Site B")) {
							List<GeoPosition> path = Arrays.asList(siteB_coords, robot_coords);
							routePainter = new RoutePainter(path);
						}
						else if (curRoute.equals("Site C")) {
							List<GeoPosition> path = Arrays.asList(siteC_coords, robot_coords);
							routePainter = new RoutePainter(path);
						}
						
				        painter.addPainter(routePainter);
					}
				}
	
			}
		};
		new javax.swing.Timer(delay, mapUpdater).start();
		
	}
	
	public void updateLaunch(boolean status) {

		if(status) {
			launch_text.setBackground(Color.GREEN);
			launch_text.setText("Ready to Launch");
		}
		
		else {
			launch_text.setBackground(Color.PINK);
			launch_text.setText("Not Ready to Launch");
		}
	}
	
	
	public static void main(String[] args) {
		new GUI();
		
	}

}
