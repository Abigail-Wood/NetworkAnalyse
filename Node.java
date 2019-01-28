
package networkAnalyse;

// Node class representing a single node.
public class Node {

	private String name; // The name of the node
	
	private int degree; // The degree of the node (i.e. number of edges)
	
	// Default constructor that creates a node with an empty (string) name.
	public Node(){
		name = "";
		degree = 0;
	}
	
	// Constructor that creates a named node.
	public Node(String nodename){
		name = nodename;
		degree = 0;
	}
	
	// Getter for the name of a node.
	public String getName() {
		return name;
	}
	
	// Setter for the name of a node.
	public void setName(String newName) {
		name = newName;
	}
	
	// Getter for the degree of a node.
	public int getDegree() {
		return degree;
	}
	
	// Setter for the degree of a node.
	public void setDegree(int newDegree) {
		degree = newDegree;
	}
	
	// An easier-to-read string representation of a node's name.
	@Override
	public String toString(){
		return "<"+name+">";
	}
}