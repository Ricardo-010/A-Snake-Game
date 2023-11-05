
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;
import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);
            
            //int row = Integer.parseInt(temp[2]);
            //int col = Integer.parseInt(temp[1]);
            int row = 50;
            int col = 50;
            
            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }
                
                int[][] board = new int[row][col];
        		Node[][] grid = new Node[row][col];
                
        		//apple
        		String[] apple1 = line.split(" ");

        		if (apple1[0].equals("-1")) {
        			apple1 = br.readLine().split(" ");
        		}
        		
        		List<String[]> zombieHeads = new ArrayList<>();
        		
        		//draw zombies
        		for (int i = 0; i < 6; i++) {
        			String zombies = br.readLine();
        			zombieHeads.add(zombies.split(" ")[0].split(","));
        			drawSnake(zombies, 9, board);
        		}

        		//my snake number
        		int mySnake = Integer.parseInt(br.readLine());
        		
        		String snakes = new String();
        		
        		List<String[]> snakeHeads = new ArrayList<>();
        		//draw snakes
        		String[] myHead = new String[2];
        		String[] myTail = new String[2];
        		for (int i = 0; i < nSnakes; i++) {
        			snakes = br.readLine();
        			drawSnake(snakes, i + 1, board);
        			if (snakes.split(" ")[0].equals("dead")) {
        				continue;
        			}
        			if (i == mySnake) {
        				myHead = snakes.split(" ")[3].split(",");
        				myTail = snakes.split(" ")[snakes.split(" ").length - 1].split(",");
        			}
        			else {
        				snakeHeads.add(snakes.split(" ")[3].split(","));
        			}
        		}
        		
        		//assign nodes to each grid position to see if its a movable position or not
        		for (int y = 0; y < board.length; y++) {
        			for (int x = 0; x < board[0].length; x++) {
        				int[] co_ords = {y, x};
        				boolean moveable;
        				if(board[y][x] == 1) {
        					moveable = false;
        				}
        				else {
        					moveable = true;
        				}
        				grid[y][x] = new Node(moveable,co_ords,y,x);
        			}
        		}
        		
        		Node headNode = grid[Integer.parseInt(myHead[1])][Integer.parseInt(myHead[0])];
        		headNode.moveable = true;
        		
        		
        		//barrier around snake heads
        		for(int i = 0; i < snakeHeads.size(); i++) {
        			headNeighbours(board, grid, snakeHeads.get(i), row, col, 0);
        		}

        		for(int i = 0; i < zombieHeads.size(); i++) {
            		headNeighbours(board, grid, zombieHeads.get(i), row, col, 1);
            	}
        		
        		
        		Node appleNode;
        		
        		//check if im the closest or if the apples location is to congested///////////////////////////////////////////////////////////
        		board[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])] = 3;
        		appleNode = grid[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])];
        		
        		//my dist
        		int myDist = Math.abs(Integer.parseInt(myHead[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(myHead[1]) - Integer.parseInt(apple1[1]));
        		
        		for (int i = 0; i < snakeHeads.size(); i++) {
        			int snakeDist = Math.abs(Integer.parseInt(snakeHeads.get(i)[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(snakeHeads.get(i)[1]) - Integer.parseInt(apple1[1]));
        			int zombieDist = Math.abs(Integer.parseInt(zombieHeads.get(i)[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(zombieHeads.get(i)[1]) - Integer.parseInt(apple1[1]));
        			
        			if ((myDist >= snakeDist /*&& myDist < 5*/) || ( zombieDist <=5 && myDist < 5)) {
        				int movex = new Random().nextInt(10);
        				int movey = new Random().nextInt(10);
        				
        				/*
        				if (appleNode.x < 25) {
            				if (appleNode.y < 25) {
            					board[45 - movex][45 - movey] = 3;
            					appleNode = grid[45 - movex][45 - movey];
            				}
            				else {
            					board[45-movex][4+movey] = 3;
            					appleNode = grid[45-movex][4+movey];
            				}
            			}
            			else {
            				if (appleNode.y < 25) {
            					board[4+movex][45-movey] = 3;
            					appleNode = grid[4+movex][45-movey];
            				}
            				else {
            					board[4+movex][4+movey] = 3;
            					appleNode = grid[4+movex][4+movey];
            				}
            			}
            			*/
        			 
        				
        				
        				
        				//board[Math.abs(Integer.parseInt(apple1[1])-49)][Math.abs(Integer.parseInt(apple1[0])-49)] = 3;
        				//appleNode = grid[Math.abs(Integer.parseInt(apple1[1])-49)][Math.abs(Integer.parseInt(apple1[0])-49)];
        				break;
        			}
        		}
        		//check if im the closest///////////////////////////////////////////////////////////
        		
        		aStar(board, headNode, appleNode, grid, row, col);
        		
        		if (calcMove(board, headNode, grid, row, col) == -1) {
        			if (appleNode.x < 25) {
        				if (appleNode.y < 25) {
        					appleNode = grid[44][44];
        				}
        				else {
        					appleNode = grid[44][4];
        				}
        			}
        			else {
        				if (appleNode.y < 25) {
        					appleNode = grid[4][44];
        				}
        				else {
        					appleNode = grid[4][4];
        				}
        			}
        			
        			aStar(board, headNode, appleNode, grid, row, col);
        		}
        		
        		int move = calcMove(board, headNode, grid, row, col);
        		
        		System.out.println(move);
        		
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    public void headNeighbours(int[][] grid, Node[][] board, String[] head, int row, int col, int n){
    	int x = 1;

    	for (int i = -x; i <= x; i++) {
    		for (int j = -x; j <= x; j++) {
    			if (Math.abs(i) == Math.abs(j)) {
    				continue;
    			}
    				
    			int xBound = Integer.parseInt(head[0]) + i;
    			int yBound = Integer.parseInt(head[1]) + j;
    				
    			if (xBound >= 0 && xBound < row && yBound >= 0 && yBound < col) {
    				board[yBound][xBound].hCost = board[yBound][xBound].hCost + 200;
    			}
    		}
    	}
    	
    	if (n == 1) {
    		for (int i = -x; i <= x; i++) {
        		for (int j = -x; j <= x; j++) {
        	    	if (Math.abs(i) == Math.abs(j)) {
            			continue;
            		}
        				
        			int xBound = Integer.parseInt(head[0]) + i;
        			int yBound = Integer.parseInt(head[1]) + j;
        				
        			if (xBound >= 0 && xBound < row && yBound >= 0 && yBound < col) {
        				grid[yBound][xBound] = 1;
        			}
        		}
        	}
    	}

    	if (n == 0) {
    		for (int i = -x; i <= x; i++) {
        		for (int j = -x; j <= x; j++) {
        			if (i == 0 && j == 0) {
        				continue;
        			}
        				
        			int xBound = Integer.parseInt(head[0]) + i;
        			int yBound = Integer.parseInt(head[1]) + j;
        				
        			if (xBound >= 0 && xBound < row && yBound >= 0 && yBound < col) {
        				board[yBound][xBound].hCost = board[yBound][xBound].hCost + 1000000; // was 100 ############# mess around with value
        			}
        		}
        	}
    	}
	}
    
    public int calcMove(int[][] board, Node headNode, Node[][] grid, int row, int col) {
		List<Node> neighbours = GetNeighbours(headNode, grid, row, col);
		int[] nextMove = new int[2];
		boolean check = false;
		for (int i = 0; i < neighbours.size(); i++) {
			if(board[neighbours.get(i).x][neighbours.get(i).y] == 9) {
				nextMove[0] = neighbours.get(i).x;
				nextMove[1] = neighbours.get(i).y;
				check = true;
			}
		}
		//check if there is no path
		if (check == false) {
			for (int i = 0; i < neighbours.size(); i++) {
				if(board[neighbours.get(i).x][neighbours.get(i).y] == 0) {
					nextMove[0] = neighbours.get(i).x;
					nextMove[1] = neighbours.get(i).y;
				}
			}
		}
	
		if (headNode.x == nextMove[0]) {
			if(headNode.y < nextMove[1]) {
				return 3;
			}
			return 2;
		}
		else {
			if(headNode.x < nextMove[0]) {
				return 1;
			}
			return 0;
		}
	}
	
	public void drawSnake(String snake, int snakeNum, int[][] board) {
		String[] snakeCoords = snake.split(" ");
		if(snakeNum == 9) {
			//zombies
			for (int i = 0; i < snakeCoords.length - 1; i++) {
				drawLine(board, snakeCoords[i], snakeCoords[i + 1], 1);
			}
		}
		else {
			//snakes
			if(snakeCoords[0].equals("dead")) {
				return;
			}
			for (int i = 3; i < snakeCoords.length - 1; i++) {
				drawLine(board, snakeCoords[i], snakeCoords[i + 1], 1);
			}
		}
	}
	
	public void drawLine(int[][] board, String point1, String point2, int snakeNum) {
		String[] pnt1 = point1.split(",");
		String[] pnt2 = point2.split(",");
		if (pnt1[0].equals(pnt2[0])) {
			int y_1 = Integer.parseInt(pnt1[1]);
			int y_2 = Integer.parseInt(pnt2[1]);
			int x = Integer.parseInt(pnt1[0]);
			
			for (int i = 0; i < Math.abs(y_1 - y_2) + 1; i++) {
				if (y_1 < y_2) {
					board[y_1 + i][x] = snakeNum;
				}
				else {
					board[y_2 + i][x] = snakeNum;
				}
			}
		}
		else {
			int x_1 = Integer.parseInt(pnt1[0]);
			int x_2 = Integer.parseInt(pnt2[0]);
			int y = Integer.parseInt(pnt1[1]);
			
			for (int i = 0; i < Math.abs(x_1 - x_2) + 1; i++) {
				if (x_1 < x_2) {
					board[y][x_1 + i] = snakeNum;
				}
				else {
					board[y][x_2 + i] = snakeNum;
				}
			}
		}
	}

	public void aStar(int[][] board, Node head, Node apple, Node[][] grid, int row, int col){
		System.out.println("??");
		Node appleNode = apple;
		Node headNode = head;
		
		List<Node> open = new ArrayList<Node>();
		HashSet<Node> closed = new HashSet<Node>();
		open.add(headNode);
		while (!open.isEmpty()) {
			Node current = open.get(0);//lowest f cost in open
			for(int i = 1; i < open.size(); i++) {
				if(open.get(i).fCost() < current.fCost() || open.get(i).fCost() == current.fCost() && open.get(i).hCost < current.hCost){
					current = open.get(i);
				}
			}
			open.remove(current);
			closed.add(current);
			
			if(current == apple) {
				draw(findPath(headNode, appleNode), board);
				return;
			}
			List<Node> neighbours = GetNeighbours(current, grid, row, col);
			for (int i = 0; i < neighbours.size(); i++) {
				if (!neighbours.get(i).moveable || closed.contains(neighbours.get(i))) {
					continue;
				}
				
				int moveToNeighbour = current.gCost + getDist(current, neighbours.get(i));
				if(moveToNeighbour < neighbours.get(i).gCost || !open.contains(neighbours.get(i))) {
					neighbours.get(i).gCost = neighbours.get(i).gCost + moveToNeighbour;
					neighbours.get(i).hCost = neighbours.get(i).hCost + getDist(neighbours.get(i), appleNode);
					neighbours.get(i).parent = current;
					
					if (!open.contains(neighbours.get(i))) {
						open.add(neighbours.get(i));
					}
				}
			}	
		}
	}
	
	public void draw(List<Node> path, int[][] board) {
		for(int i = 0; i < path.size(); i++) {
			board[path.get(i).x][path.get(i).y] = 9;
		}
	}
	
	public int getDist(Node a, Node b) {
		int distX = Math.abs(a.x - b.x);
		int distY = Math.abs(a.y - b.y);
		
		if(distX > distY) {
			return 14*distY + 10*(distX - distY);
		}
		else {
			return 14*distX + 10*(distY - distX);
		}
	}
	
	public List<Node> GetNeighbours(Node node, Node[][] grid, int row, int col){
		List<Node> neighbours = new ArrayList<>();
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) == Math.abs(j)) {
					continue;
				}
				
				int xBound = node.x + i;
				int yBound = node.y + j;
				
				if (xBound >= 0 && xBound < row && yBound >= 0 && yBound < col) {
					neighbours.add(grid[xBound][yBound]);
				}
			}
		}
		return neighbours;
	}
	
	public List<Node> findPath(Node headNode, Node appleNode) {
		List<Node> path = new ArrayList<>();
		Node curr = appleNode;
		while (curr != headNode) {
			path.add(curr);
			curr = curr.parent;
		}
		return path;
	}
}
