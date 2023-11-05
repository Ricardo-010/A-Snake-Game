
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import za.ac.wits.snake.DevelopmentAgent;

import static java.util.Comparator.comparingInt;

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
            
            int row = Integer.parseInt(temp[2]);
            int col = Integer.parseInt(temp[1]);

			int applex = -1;
			int appley = -1;

            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }

                int[][] board = new int[row][col];
        		Node[][] nodeGrid = new Node[row][col];
                
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
        			passSnakeCo_ords(zombies, board, 0);
        		}

        		//my snake number
        		int mySnake = Integer.parseInt(br.readLine());
        		
        		String snakes;
        		
        		List<String[]> snakeHeads = new ArrayList<>();
        		//draw snakes
        		String[] myHead = new String[2];
        		for (int i = 0; i < nSnakes; i++) {
        			snakes = br.readLine();
        			passSnakeCo_ords(snakes, board, 3);
        			if (snakes.split(" ")[0].equals("dead")) {
        				continue;
        			}
        			if (i == mySnake) {
        				myHead = snakes.split(" ")[3].split(",");
        			}
        			else {
        				snakeHeads.add(snakes.split(" ")[3].split(","));
        			}
        		}
        		assignNodes(board,nodeGrid);
        		
        		//heuristic around snake heads
        		for(int i = 0; i < snakeHeads.size(); i++) {
					heuristicAroundHead(nodeGrid, snakeHeads.get(i), row, col, 0);
        		}
				//heuristic around zombie heads
				for(int i = 0; i < zombieHeads.size(); i++) {
					heuristicAroundHead(nodeGrid, zombieHeads.get(i), row, col, 1);
				}

				//initiate the next apple
				if (applex == -1 && appley == -1){
					applex = new Random().nextInt(49);
					appley = new Random().nextInt(49);

					while (board[applex][appley] == 1){
						applex = new Random().nextInt(49);
						appley = new Random().nextInt(49);
					}
				}
				int conditionX;
				int conditionY;
				if (Integer.parseInt(apple1[1]) < 25) {
					conditionX = 0;
					conditionY = 49;
				}
				else {
					conditionX = 0;
					conditionY = 24;
				}

				//relocates the next apple when needed
				if (board[applex][appley] == 1 || nodeGrid[applex][appley].hCost > 999 || neighbour_of_nxtApple(applex, appley, board, row,col)){
					applex = Math.abs(conditionY - new Random().nextInt(24));
					appley = Math.abs(conditionX - new Random().nextInt(49));

					while (board[applex][appley] == 1
							&& (Integer.parseInt(myHead[0]) - 10 <= applex && applex <= Integer.parseInt(myHead[0]) + 10)
							|| (Integer.parseInt(myHead[1]) - 10 <= appley && appley <= Integer.parseInt(myHead[1]) + 10) && board[applex][appley] == 1
							|| nodeGrid[applex][appley].hCost > 999
							|| neighbour_of_nxtApple(applex, appley, board, row,col)){
						applex = Math.abs(conditionY - new Random().nextInt(24));
						appley = Math.abs(conditionX - new Random().nextInt(49));
					}
				}

				Node headNode = nodeGrid[Integer.parseInt(myHead[1])][Integer.parseInt(myHead[0])];
				headNode.empty = true;
        		
        		Node appleNode;


        		//check if im the closest///////////////////////////////////////////////////////////
        		board[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])] = 3;
        		appleNode = nodeGrid[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])];
        		
				//my dist
        		int myDist = Math.abs(Integer.parseInt(myHead[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(myHead[1]) - Integer.parseInt(apple1[1]));

				//check snakes distances to apple
        		for (int i = 0; i < snakeHeads.size(); i++) {
        			int snakeDist = Math.abs(Integer.parseInt(snakeHeads.get(i)[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(snakeHeads.get(i)[1]) - Integer.parseInt(apple1[1]));

					if (myDist >= snakeDist) {
						nodeGrid[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])].hCost = 1000;
						board[applex][appley] = 3;
						appleNode = nodeGrid[applex][appley];
						break;
					}
        		}
				//check zombie distances to apple
				for (int i = 0; i < zombieHeads.size(); i++) {
					int zombieDist = Math.abs(Integer.parseInt(zombieHeads.get(i)[0]) - Integer.parseInt(apple1[0])) + Math.abs(Integer.parseInt(zombieHeads.get(i)[1]) - Integer.parseInt(apple1[1]));

					if (zombieDist <= 3) {
						nodeGrid[Integer.parseInt(apple1[1])][Integer.parseInt(apple1[0])].hCost = 1000;
						board[applex][appley] = 3;
						appleNode = nodeGrid[applex][appley];
						break;
					}
				}
        		//check if im the closest///////////////////////////////////////////////////////////


        		aStar(board, headNode, appleNode, nodeGrid, row, col);

        		int move = findNxtMove(board, headNode, nodeGrid, row, col);
        		
        		System.out.println(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void assignNodes(int[][] board, Node[][] grid){
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				int[] co_ords = {y, x};
				if(board[y][x] == 1) {
					grid[y][x] = new Node(co_ords,false);
					continue;
				}
				grid[y][x] = new Node(co_ords,true);
			}
		}
	}

	public boolean neighbour_of_nxtApple(int applex, int appley, int[][] board, int row, int col){
		int count = 0;
		for (int i = -12; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i == 0 && j == 0 ) {
					continue;
				}

				int xBound = appley + i;
				int yBound = applex + j;

				if (validPosition(xBound,yBound,row,col)) {
					if (board[yBound][xBound] == 1){
						count = count + 1;
					}
				}
			}
		}
		if (count >= 5){
			return true;
		}
		return false;
	}

    public void heuristicAroundHead(Node[][] board, String[] head, int row, int col, int n){
    	int x = 1;

    	for (int i = -x; i <= x; i++) {
    		for (int j = -x; j <= x; j++) {
    			if (Math.abs(i) == Math.abs(j)) {
    				continue;
    			}
    				
    			int xBound = Integer.parseInt(head[0]) + i;
    			int yBound = Integer.parseInt(head[1]) + j;
    				
    			if (validPosition(xBound,yBound,row,col)) {
    				board[yBound][xBound].hCost = board[yBound][xBound].hCost + 100000;
    			}
    		}
    	}

    	if (n == 0 || n == 1) {
    		for (int i = -x; i <= x; i++) {
        		for (int j = -x; j <= x; j++) {
        			if (i == 0 && j == 0) {
        				continue;
        			}
        				
        			int xBound = Integer.parseInt(head[0]) + i;
        			int yBound = Integer.parseInt(head[1]) + j;
        				
        			if (validPosition(xBound,yBound,row,col)) {
        				board[yBound][xBound].hCost = board[yBound][xBound].hCost + 100000; // was 100 ############# mess around with value
        			}
        		}
        	}
    	}
	}

	public boolean checkIfPath(List<Node> myHeadNeighbours, int[][] board){
		for (int i = 0; i < myHeadNeighbours.size(); i++) {
			if(board[myHeadNeighbours.get(i).co_ords[0]][myHeadNeighbours.get(i).co_ords[1]] == 9) {
				return true;
			}
		}
		return false;
	}

    public int findNxtMove(int[][] board, Node headNode, Node[][] grid, int row, int col) {
		List<Node> myHeadNeighbours = GetNeighbours(headNode, grid, row, col, board);
		int[] newCo_ords = new int[2];
		boolean checkPath = checkIfPath(myHeadNeighbours, board);

		//if there is a path
		if (checkPath){
			for (int i = 0; i < myHeadNeighbours.size(); i++) {
				if(board[myHeadNeighbours.get(i).co_ords[0]][myHeadNeighbours.get(i).co_ords[1]] == 9) {
					newCo_ords[0] = myHeadNeighbours.get(i).co_ords[0];
					newCo_ords[1] = myHeadNeighbours.get(i).co_ords[1];
				}
			}
		}

		//if there isn't a path
		if (!checkPath) {
			for (int i = 0; i < myHeadNeighbours.size(); i++) {
				if(board[myHeadNeighbours.get(i).co_ords[0]][myHeadNeighbours.get(i).co_ords[1]] == 0 && grid[myHeadNeighbours.get(i).co_ords[0]][myHeadNeighbours.get(i).co_ords[1]].hCost < 999) {
					newCo_ords[0] = myHeadNeighbours.get(i).co_ords[0];
					newCo_ords[1] = myHeadNeighbours.get(i).co_ords[1];
				}
			}
		}
	
		if (headNode.co_ords[0] == newCo_ords[0]) {
			if(headNode.co_ords[1] < newCo_ords[1]) {
				return 3;
			}
			return 2;
		}
		else {
			if(headNode.co_ords[0] < newCo_ords[0]) {
				return 1;
			}
			return 0;
		}
	}
    
    public void passSnakeCo_ords(String snake, int[][] board, int startPoint) {
		String[] snakeCoords = snake.split(" ");

		if (startPoint == 3) {
			if(snakeCoords[0].equals("dead")) {
				return;
			}
		}
		for (int i = startPoint; i < snakeCoords.length - 1; i++) {
			drawSnake(board, snakeCoords[i], snakeCoords[i + 1]);
		}
	}
	
	public void drawSnake(int[][] board, String co_ord1, String co_ord2) {
		int[] turn1 = {Integer.parseInt(co_ord1.split(",")[0]),Integer.parseInt(co_ord1.split(",")[1])};
		int[] turn2 = {Integer.parseInt(co_ord2.split(",")[0]),Integer.parseInt(co_ord2.split(",")[1])};
		if (turn1[0] == turn2[0]) {
			for (int i = 0; i < Math.abs(turn1[1] - turn2[1]) + 1; i++) {
				if (turn1[1] < turn2[1]) {
					board[turn1[1] + i][turn1[0]] = 1;
					continue;
				}
				board[turn2[1] + i][turn1[0]] = 1;
			}
		}
		else {
			for (int i = 0; i < Math.abs(turn1[0] - turn2[0]) + 1; i++) {
				if (turn1[0] < turn2[0]) {
					board[turn1[1]][turn1[0] + i] = 1;
					continue;
				}
				board[turn1[1]][turn2[0] + i] = 1;
			}
		}
	}

	public void aStar(int[][] board, Node myHeadNode, Node appleNode, Node[][] grid, int row, int col){
		List<Node> openList = new ArrayList<>();
		List<Node> closedList = new ArrayList<>();
		openList.add(myHeadNode);

		while (openList.isEmpty() == false) {
			openList.sort(comparingInt(Node::fCost));

			Node currentNode = openList.get(0);
			openList.remove(currentNode);
			closedList.add(currentNode);
			
			List<Node> neighbours = GetNeighbours(currentNode, grid, row, col, board);

			//checks if it's a valid neighbour to add to the openList then adds it to openList
			checkVadilityOfNeighbours(neighbours, openList, closedList, currentNode, appleNode);
			
			if(currentNode == appleNode) {
				List<Node> path = new ArrayList<>();
				Node currMoveNode = appleNode;

				while (currMoveNode != myHeadNode) {
					path.add(currMoveNode);
					currMoveNode = currMoveNode.parent;
				}

				board[path.get(path.size() - 1).co_ords[0]][path.get(path.size() - 1).co_ords[1]] = 9;
				break;
			}
		}
	}

	public void checkVadilityOfNeighbours(List<Node> neighbours, List<Node> openList, List<Node> closedList, Node currentNode, Node appleNode){
		for (int i = 0; i < neighbours.size(); i++) {
			if (neighbours.get(i).empty == false || closedList.contains(neighbours.get(i)) == true) {
				continue;
			}

			int costToMoveToNeighbour = currentNode.gCost + calcHeuristic(currentNode, neighbours.get(i));
			if(costToMoveToNeighbour < neighbours.get(i).gCost || openList.contains(neighbours.get(i)) == false) {
				neighbours.get(i).gCost = neighbours.get(i).gCost + costToMoveToNeighbour;
				neighbours.get(i).hCost = neighbours.get(i).hCost + calcHeuristic(neighbours.get(i), appleNode);
				neighbours.get(i).parent = currentNode;

				if (openList.contains(neighbours.get(i)) == false) {
					openList.add(neighbours.get(i));
				}
			}
		}
	}

	public int calcHeuristic(Node currentNode, Node appleNode) {
		int dist_of_X = Math.abs(currentNode.co_ords[0] - appleNode.co_ords[0]);
		int dist_of_Y = Math.abs(currentNode.co_ords[1] - appleNode.co_ords[1]);
		
		if(dist_of_X > dist_of_Y) {
			return 8*dist_of_Y + 4*(dist_of_X - dist_of_Y);//change the 6 to 4 to make its move horizontal and vertical instead of diagonal or vice versa
		}//horizontal and vertical seems to work best
		else {
			return 8*dist_of_X + 4*(dist_of_Y - dist_of_X);//change the 6 to 4 to make its move horizontal and vertical instead of diagonal or vice versa
		}//horizontal and vertical seems to work best
	}
	
	public List<Node> GetNeighbours(Node node, Node[][] grid, int row, int col, int[][] board){
		List<Node> neighbours = new ArrayList<>();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) == Math.abs(j)) {
					continue;
				}
				
				int xBound = node.co_ords[0] + i;
				int yBound = node.co_ords[1] + j;
				
				if (validPosition(xBound,yBound,row,col)) {
					neighbours.add(grid[xBound][yBound]);
				}
			}
		}
		return neighbours;
	}
	
	public boolean validPosition(int xBound, int yBound, int row, int col) {
		if (xBound >= 0 && xBound < row && yBound >= 0 && yBound < col) {
			return true;
		}
		return false;
	}
}
