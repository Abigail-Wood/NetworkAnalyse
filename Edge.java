package networkAnalyse;

// Edge class representing an undirected network edge (includes self-edges).
public class Edge {
	private Node one;
	private Node two;
	
	// Constructor that creates an Edge object connecting two nodes.
	public Edge(Node one, Node two){
		this.one = one;
		this.two = two;
	}
	
	// An easier-to-read string representation of an edge.
	@Override
	public String toString() {
		return one + "-" + two;
	}
}