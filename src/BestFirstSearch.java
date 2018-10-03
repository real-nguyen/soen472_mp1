import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Stack;

public class BestFirstSearch {
	private String outputPath = "output/puzzleBFS-_.txt";	
	private Puzzle puzzle;	
	private PriorityQueue<Node> openList;
	private ArrayList<Node> closedList;
	
	public BestFirstSearch(Puzzle puzzle, String heuristic) {
		this.puzzle = puzzle;
		if (heuristic == "h1") {
			openList = new PriorityQueue<Node>(new Heuristic_TilesOutOfPlace());			
		}
		else {
			openList = new PriorityQueue<Node>();
		}
		outputPath = outputPath.replaceAll("_", heuristic);
		closedList = new ArrayList<Node>();
	}
	
	public void getSolutionPath() {
		//search and print solution path
		//Source: https://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
		try {
			//will clear file every time
			PrintWriter pw = new PrintWriter(outputPath);
			
			Node initialState = new Node(puzzle);
			openList.offer(initialState);
			boolean solutionFound = search(initialState);
			
			if (solutionFound) {
				//only print solution path if it is found
				pw.println("Solution found!");
				Node solutionPathNode = closedList.get(closedList.size() - 1);
				Stack<String> buffer = new Stack<String>();
				
				while (solutionPathNode != null) {
					Puzzle state = solutionPathNode.getStateRepresentation();
					//prefix must be 0 if initial state, otherwise use letter index of empty tile
					char prefix = solutionPathNode.getParentNode() != null ? state.getEmptyTilePosition() : '0';
					buffer.push(prefix + " " + state);				
					solutionPathNode = solutionPathNode.getParentNode();
				}
				
				while (!buffer.isEmpty()) {
					pw.println(buffer.pop());
				}
			}
			else {
				//otherwise print entire search path
				pw.println("Solution not found :(");
				for (int i = 0; i < closedList.size(); i++) {
					//prefix must be 0 if initial state, otherwise use letter index of empty tile				
					Puzzle state = closedList.get(i).getStateRepresentation();
					char prefix = i == 0 ? '0' : state.getEmptyTilePosition();
					pw.println(prefix + " " + state);
				}
			}		
			
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean search(Node nodeToVisit) {
		if (!openList.isEmpty()) {			
			nodeToVisit = openList.poll();				
			visitNode(nodeToVisit);
			
			if (Puzzle.isSolved(nodeToVisit.getPuzzle())) {
				return true;
			}
			
			ArrayList<Node> childNodes = generateChildNodes(nodeToVisit);
			
			for (Node child : childNodes) {
				addChildNode(nodeToVisit, child);
			}						
			
			return search(nodeToVisit);			
		}
		return false;
	}
	
	private void visitNode(Node n) {
		n.visit();
		closedList.add(n);
	}
	
	private boolean isDuplicateState(Node n) {
		return openList.contains(n) || closedList.contains(n);
	}
	
	private ArrayList<Node> generateChildNodes(Node currentNode) {
		//generate all possible moves from this state and add them as children
		ArrayList<Node> childNodes = new ArrayList<Node>();
		
		Puzzle currentState = currentNode.getStateRepresentation();
		if (currentState.canMoveUp()) {
			childNodes.add(new Node(currentState.moveUp()));
		}
		if (currentState.canMoveUpRight()) {
			childNodes.add(new Node(currentState.moveUpRight()));
		}
		if (currentState.canMoveRight()) {
			childNodes.add(new Node(currentState.moveRight()));
		}
		if (currentState.canMoveDownRight()) {
			childNodes.add(new Node(currentState.moveDownRight()));
		}
		if (currentState.canMoveDown()) {
			childNodes.add(new Node(currentState.moveDown()));
		}
		if (currentState.canMoveDownLeft()) {
			childNodes.add(new Node(currentState.moveDownLeft()));
		}
		if (currentState.canMoveLeft()) {
			childNodes.add(new Node(currentState.moveLeft()));
		}
		if (currentState.canMoveUpLeft()) {
			childNodes.add(new Node(currentState.moveUpLeft()));
		}
		
		return childNodes;
	}
	
	private void addChildNode(Node currentNode, Node nodeToAdd) {
		if (!isDuplicateState(nodeToAdd)) {
			currentNode.addChild(nodeToAdd);
			openList.offer(nodeToAdd);
		}
	}
}