
public class Node {
	int[] co_ords = new int[2];
	boolean empty;
	int gCost;
	int hCost;
	Node parent;
	
	public Node(int[] co_ords, boolean empty) {
		this.empty = empty;
		this.co_ords = co_ords;
	}

	public int fCost(){
		return gCost + hCost;
	}
}
