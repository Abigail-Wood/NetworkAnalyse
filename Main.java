package networkAnalyse;

import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Map;

// The main application class, including GUI elements.
public class Main implements ActionListener {
	// The current network.
	private Network network;
	
	// The file name of the current network
	private String inputFileName;
	
	// A file chooser, re-utilised to retain current working directory.
	private JFileChooser fc;
	
	// The main application window 
	private JFrame frame;
	
	// Labels referring to basic network properties; updated when network changes.
	private JLabel networkName;
	private JLabel totalNodes;
	private JLabel totalEdges;
	
	// Text area for writing network information, user interaction events, errors.
	private JTextArea textArea;
	
	// Menu items, used in GUI construction and ActionPerformer
	private JMenuItem open;
	private JMenuItem exit;
	private JMenuItem addInteract;
	private JMenuItem findNodeDegree;
	private JMenuItem averageDegree;
	private JMenuItem hubs;
	private JMenuItem degreeDistribution;
	
	// Constructor that creates a network and main window.
	private Main() {
		network = new Network();
		inputFileName = "File to be selected.";
		
		// File chooser initially viewing the current user directory.
		fc = new JFileChooser(System.getProperty("user.dir"));

		frame = new JFrame("Network Analyse");
		frame.setLocationRelativeTo(null);
		frame.setSize(450, 260);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		
		// Uses a simple BoxLayout, with all components set as LEFT_ALIGNMENT.
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// Creates the menu bar and items for display, and sets action listeners.
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		open = new JMenuItem("Open Network File...");
		open.addActionListener(this);
		fileMenu.add(open);
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		fileMenu.add(exit);

		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		addInteract = new JMenuItem("Add Interaction");
		addInteract.addActionListener(this);
		editMenu.add(addInteract);

		JMenu analyseMenu = new JMenu("Analyse");
		menuBar.add(analyseMenu);
		findNodeDegree = new JMenuItem("Find Node Degree");
		findNodeDegree.addActionListener(this);
		analyseMenu.add(findNodeDegree);
		averageDegree = new JMenuItem("Average Degree");
		averageDegree.addActionListener(this);
		analyseMenu.add(averageDegree);
		hubs = new JMenuItem("Find Hubs");
		hubs.addActionListener(this);
		analyseMenu.add(hubs);
		degreeDistribution = new JMenuItem("Degree Distribution");
		degreeDistribution.addActionListener(this);
		analyseMenu.add(degreeDistribution);

		// Creates labels for network properties.
		networkName = new JLabel("Network: ");
		networkName.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		contentPane.add(networkName);

		totalNodes = new JLabel("Total Nodes: ");
		totalNodes.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		contentPane.add(totalNodes);

		totalEdges = new JLabel("Total Edges: ");
		totalEdges.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		contentPane.add(totalEdges);
		
		// Creates a text area to print into, with vertical scrollbar.
		textArea = new JTextArea("", 5, 20);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		JScrollPane scrollPane = new JScrollPane(textArea); 
		scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		contentPane.add(scrollPane);

		// Initialise labels to begin.
		updateMainWindow();
		
		// Display the window.
		frame.setVisible(true);
	}
	
	// Updates labels in main window to reflect current network properties.
	private void updateMainWindow() {
		networkName.setText("Network: " + inputFileName);
		totalNodes.setText("Total Nodes: " + Integer.toString(network.nodes.size()));
		totalEdges.setText("Total Edges: " + Integer.toString(network.edges.size()));
	}
	
	// Prints text into text area, moves to the bottom.
	private void writeTextArea(String s) {
		textArea.append(s + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	/* Presents an open file dialog to the user, opens the file as a network,
	 * prints user actions; alternatively, provides a cancellation option. 
	 * Any errors are thrown as IOExceptions. */
	private void openNetworkDialog() throws IOException {
		Path filePath = null;
		String s = null;
		fc.setDialogTitle("Open Network File");
		int returnVal = fc.showOpenDialog(frame);
		switch (returnVal)
		{
		case JFileChooser.APPROVE_OPTION:
			File file = fc.getSelectedFile();
			filePath = file.toPath();
			inputFileName = file.getName();
			network.readFile(filePath);
			s = "Network file " + inputFileName + " opened.";
			writeTextArea(s);
			break;

		case JFileChooser.CANCEL_OPTION:
			inputFileName = "";
			s = "Open Network File cancelled.";
			writeTextArea(s);
			break;

		case JFileChooser.ERROR_OPTION:
			break;
		}
	}
	
	/* Presents a dialog to the user for adding a single interaction, adds the
	 * specified edge to the network. Allows the cancellation of the dialog.
	 * Errors become IllegalArgumentExceptions.
	 */
	private void addInteractDialog() throws IllegalArgumentException {
		String interaction = JOptionPane.showInputDialog(
				frame,
				"To add an interaction, add nodes 1 and 2 below:\n "
						+ "e.g. P12459, P60879 \n",
						"Add Interaction",
						JOptionPane.PLAIN_MESSAGE);
		if (interaction == null) {
			return; // Allows cancellation/closure of dialog.
		}
		network.addInteraction(interaction);
		updateMainWindow();
		writeTextArea("New interaction added for " + interaction);
	}
	
	/* Prints a dialog to the user for printing the degree of a node.
	 * Errors become IllegalArgumentExceptions.
	 */
	private void printDegreeDialog() throws IllegalArgumentException {
		String nodeName = JOptionPane.showInputDialog(
				frame,
				"Enter the name of a node to calculate its degree:\n "
						+ "e.g. P60879 \n",
						"Find Node Degree",
						JOptionPane.PLAIN_MESSAGE);
		if (nodeName == null) {
			return; // Allows cancellation/closure of dialog.
		}
		int d = network.nodeDegree(nodeName);
		writeTextArea("Node degree for " + nodeName + " is: " + d);
	}
	
	/* Prints a dialog to the user for saving the degree distribution.
	 * Errors become IOExceptions.
	 */
	private void degreeDistributionDialog() throws IOException {
		File outputFile = null;
		fc.setDialogTitle("Degree Distribution");
		int returnVal = fc.showSaveDialog(frame);
		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION:
			outputFile = fc.getSelectedFile();
			break;
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
			return;
		}
		Map<Integer, Integer> degreeDistribution = network.degreeDistribution();
		Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "utf-8"));
		writer.write("Network: " + inputFileName + "\n" 
				+ "Degree" + "\t" + "No. of nodes with this degree" + "\n");		
		int maxDegree = network.maxDegree(); 
		for (int degree = 1; degree <= maxDegree; ++degree) {
			writer.write(degree + "\t" + degreeDistribution.getOrDefault(degree, 0) + "\n");
		} 		
		writer.close();
		writeTextArea("Degree distribution file created at " + outputFile.getName());
		return;
	}
	
	/* Responds to any action performed by the user (menu selections).
	 * Catches IOException and IllegalArgumentException.
	 */
	public void actionPerformed(ActionEvent e){
		try {
			JMenuItem source = (JMenuItem)(e.getSource());
			if(source == open){
				network = new Network(); // resets network.
				openNetworkDialog();
				updateMainWindow();
			} else if(source == exit){
				System.exit(0);
			} else if(source == addInteract){
				addInteractDialog();
			} else if(source == findNodeDegree){
				printDegreeDialog();
			} else if(source == averageDegree){
				double average = network.averageDegree();
				writeTextArea("Average degree is: " + String.format("%.3f", average));
			} else if(source == hubs){
				int maxD = network.maxDegree();
				ArrayList<Node> hubsList = network.listHubsOfDegree(maxD);
				writeTextArea("Highest degree (hub degree): " + maxD);
				String s = "Hub nodes are: ";
				for(Node hub : hubsList) {
					s += hub.toString();
				}
				writeTextArea(s);
			} else if(source == degreeDistribution){
				degreeDistributionDialog();
			}
		} catch(IOException ex){
			writeTextArea("IO error: " + ex.getMessage());
			updateMainWindow();
		} catch(IllegalArgumentException x) {
			writeTextArea("Invalid input: " + x.getMessage());
		}
	}
	
	// Creates the main window, presents first open-file dialog, updates window. 
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main mywindow = new Main();
				try {
					mywindow.openNetworkDialog();
				} catch (IOException ex) {
					mywindow.writeTextArea("IO Exception: " + ex.getMessage());
					mywindow.updateMainWindow();
				}
				mywindow.updateMainWindow();
			}
		});
	}
}
