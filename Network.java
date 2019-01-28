package networkAnalyse;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

// Network class representing an undirected network.
public class Network {
	List<Node> nodes;
	List<Edge> edges;
	private Map<String, Node> nodeMap; // Maps node name to node
	private Map<String, Edge> edgeMap; 
	/* Maps edge name to edge, with each edge appearing twice (e.g. as AB and BA) 
	 * to prevent repetition when adding new edges. */
	
	// Default constructor that creates an empty network
	public Network() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>(); 
		nodeMap = new HashMap<String, Node>();
		edgeMap = new HashMap<String, Edge>();
	}
	
	// Validates that node name is not empty; trims whitespace from name.
	private String validateNodeName(String nodeName) throws IllegalArgumentException{
		String name = nodeName;
		name = name.trim();
		if (name.length() == 0) {
			throw (new IllegalArgumentException("Node name cannot be empty."));
		}
		return name;
	}
	
	// Checks whether the network has a node with name, creates one if not.
	private Node ensureNode (String name) {
		Node node = nodeMap.get(name);
		if (node == null) {
			node = new Node(name);
			nodes.add(node);
			nodeMap.put(name, node);
		}
		return node;
	}
	
	/* Checks whether an edge already exists using two node names, 
	creates relevant nodes and edge if not. */
	private Edge ensureEdge (String nodeName1, String nodeName2 ) {
		nodeName1 = validateNodeName(nodeName1);
		nodeName2 = validateNodeName(nodeName2);
		String edgeName1 = nodeName1 + nodeName2;
		String edgeName2 = nodeName2 + nodeName1;
		Edge edge = edgeMap.get(edgeName1);	
		if (edge == null) {
			edge = edgeMap.get(edgeName2);
		}
		if (edge == null) {
			Node node1 = ensureNode(nodeName1);
			Node node2 = ensureNode(nodeName2);
			edge = new Edge(node1, node2);
			edges.add(edge);
			edgeMap.put(edgeName1, edge);
			edgeMap.put(edgeName2, edge);
			node1.setDegree(node1.getDegree() + 1);
			node2.setDegree(node2.getDegree() + 1);
		}
		return edge;
	}
	
	/* Reads file from filePath and creates network from edges described. 
	 * Duplicate edges are added once only, and an IllegalArgumentException 
	 * can be thrown if (no tab characters, too many tab characters per line). */ 
	public void readFile(Path filePath) throws IOException { 
		String line = null;
		int lineNumber = 1;
		try {
			BufferedReader reader = Files.newBufferedReader(filePath);
			while ((line = reader.readLine()) != null) {
				String[] splitFile = line.split("\t");
				if (splitFile.length == 2) {
					ensureEdge(splitFile[0], splitFile[1]);
				lineNumber++;
				} else {
					throw(new IllegalArgumentException("File is not in correct format."));
				}
			}
			reader.close();
		} catch (IllegalArgumentException x) {
			throw(new IOException(filePath.getFileName() + ":" + lineNumber + ":" + x.getMessage()));
		}
	}
	
	/* Adds comma-separated node names as nodes and an edge. 
	 * Throws IllegalArgumentException if it's malformed. */
	public void addInteraction(String string) {
		String[] splitString = string.split(",");
		if (splitString.length < 2) {
			throw(new IllegalArgumentException("Less than two node names provided."));
		} else if (splitString.length > 2) {
			throw(new IllegalArgumentException("Add Interaction only accepts two node names."));
		}
		ensureEdge(splitString[0], splitString[1]);
	}
	
	/* Checks whether the node name provided is valid, checks if node is in network, 
	 * gets the degree of the node if it is real. 
	 * Throws IllegalArgumentException if it's malformed. */
	public int nodeDegree(String nodeName) throws IllegalArgumentException {
		nodeName = validateNodeName(nodeName);
		Node node = nodeMap.get(nodeName);
		if (node == null) {
			throw(new IllegalArgumentException("Node " + nodeName + " is not in network."));
		}
		return node.getDegree();
	}
	
	/* Returns the average degree of nodes in the network. This value is 
	 * |edges|*2/|nodes| because self-edges also add 2 to the degree. */
	public double averageDegree() {
		double totalEdges = edges.size();
		double totalNodes = nodes.size();
		return (totalEdges * 2) / totalNodes;
	}
	
	// Returns the highest (maximum) degree of all the nodes in the network.
	public int maxDegree() {
		int highestDegree = 0;
		for (Node node : nodes) {
			int degree = node.getDegree();
			if (degree > highestDegree){
				highestDegree = node.getDegree();
			}
		}
		return highestDegree;
	}
	
	// Returns a list containing all nodes with a given degree.
	public ArrayList<Node> listHubsOfDegree(int degree) {
		ArrayList<Node> hubsList = new ArrayList<Node>();
		for (Node node : nodes) {
			if(degree == node.getDegree()) {
				hubsList.add(node); 
			}
		}
		return hubsList;
	}
	
	/* Creates the degree distribution of the network as a map of degree to 
	 * a count of nodes with that degree. */
	public Map<Integer, Integer> degreeDistribution() {
		HashMap<Integer, Integer> degreeDistribution = new HashMap<Integer, Integer>();
		for(Node node: nodes) {
			Integer degree = node.getDegree();
			Integer nodeCount = degreeDistribution.getOrDefault(degree,0);
			degreeDistribution.put(degree, nodeCount + 1);
		}
		return degreeDistribution;	
	}
}