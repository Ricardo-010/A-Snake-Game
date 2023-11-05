
public class Node {
	int[] co_ords = new int[2];
	boolean moveable;
	int x;
	int y;
	
	int gCost;
	int hCost;
	
	Node parent;
	
	public Node(boolean moveable, int[] co_ords, int x, int y) {
		this.moveable = moveable;
		this.co_ords = co_ords;
		this.x = x;
		this.y = y;
	}
	
	public int fCost(){
		return gCost + hCost;
	}
}
