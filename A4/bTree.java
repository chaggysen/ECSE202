// ECSE 202: Assignment 3 - Anthony Harissi Dagher (260924250)

/* A large percentage of this code is inspired by
 * Professor Ferrie's bTree example from class.
 * It has been rewritten to accept bSim and aBall
 */
public class bTree {
	
	// Initializing a few variables and parameters/constants that will be used
	private static final double DELTASIZE = 0.1; // Maximum difference between the ball's set
	private static final int HEIGHT = 600; // Screen height (pixels)
	bNode root = null;
	double lastSize;
	public double newX;
	public double newY;
	
	public class bNode {
		aBall data;
		bNode left;
		bNode right;
	}
	
	/* addNode is a method that will "add a new a node by
	 * descending to the leaf node using a while loop in place of recursion"
	 * - Professor Ferrie's bTree class example
	 */
	public void addNode(aBall newBalls) {
		
		bNode current; // Initializes current variable
		
		// Checks if the tree is empty, if so, will add a node
		if (root==null) {
			
			root = createNode(newBalls); // Adds node to root
		}
		
		// Check if the tree is not empty, if so, on to the leaf nodes
		else {
			
			current = root;// Sets the current node to the root;
			
			while (true) {
				
				// If the ball size is smaller the root (selected node), data goes to left
				if(newBalls.bSize < current.data.bSize) {
					// Add node whenever left side is null
					if(current.left == null) {
						 current.left = createNode(newBalls); // Creates the node for this data
						 break;
					}
					
					// If there is a node, continue on tree
					else { 
						current = current.left;
					}
				}
				
				// If the ball size isn't smaller, data goes to right node
				else {
					// Add node whenever right side is null
					if(current.right == null) {
						current.right = createNode(newBalls);
						break;
					}
					// If there is a node, continue on tree
					else {
						current = current.right;
					}
				}
			}
		}
	}
	
	/* This method creates "a single instance of a bNode"
	 * - Professor Ferrie's bTree class example */
	bNode createNode(aBall newBalls) {
		
		bNode node = new bNode();	// New bNode object gets created
		node.data = newBalls;	// Defines what data corresponds to, in this case newBalls			
		node.left = null;	// Set left node to null							
		node.right = null;	// Set right node to null							
		return node;	// Returns the default created node when called on							
	}
	
	/* Creating the in order traversal method as an example from 
	 * Professor Frank Ferrie's binary tree class
	 */
	public void inorder() {	
		
		traverse_inorder(root); // Calls on traverse_inorder method to run on root
	}
	
	// Traverse in order method used from Professor Frank Ferrie's examples
	public void traverse_inorder(bNode root) {
		
		// This will print the following nodes in order: left, root then right
		if(root.left != null) traverse_inorder(root.left);	// Left first
		System.out.println(root.data);
		if(root.right != null) traverse_inorder(root.right); // Right third
	}
	
	// Stacks the balls that are ordered from smallest to largest
	public void stackBalls() {
		
		lastSize = 0;
		newX = 0;
		newY = 0;
		stack(root); // Calling on stack method to run its code on root	
	}
	
	private void stack(bNode root) {
		
		// Begin the left side of the in order traversal
		if(root.left != null) stack(root.left);
		
		/* The following code was implicitly suggested in 
		 * the assignment sheet to verify ball size difference */
		 
		// If the ball is not the approximately the same size, move it to a new stack
		if((root.data.bSize - lastSize) > DELTASIZE) {
			
			newY = HEIGHT; // Sets the Y position for the first ball of each new stack
			newX = newX + lastSize*2; // Sets the X position for each new stack
		}
		
		// If the ball is approximately the same size, add it above previous ball
		else {
			
			newY = newY - lastSize*2*bSim.getScale(); // Sets the Y position to place ball on top of previous one
		}
		
		// Updates the position of the balls according the to moveTo method in aBall, with new values
		root.data.moveTo(newX*bSim.getScale(), newY - root.data.bSize*2*bSim.getScale());
		lastSize = root.data.bSize; // Updating the lastSize variable to the latest ball size
				
		// Complete the in order traversal with the right side of tree
		if(root.right != null) stack(root.right);
	}
	
	// The isRunning method checks if there are still balls moving
	public boolean isRunning() {
		
		return traverseRun(root); // Calls on the traverseRun method
	}
		
	// Detects if there are any ball instances running
	private boolean traverseRun(bNode root) {
			
		
		boolean running = root.data.runs; // Defines "running" as the boolean "run" from the aBall class
		
		if (running == false && root.left != null) { // Checks if not running and if the left node is null
			running = traverseRun(root.left); // Applies onto left of tree if true/false
		}
		
		if (running == false && root.right != null ) { // Checks if not running and if the right node is null
			running = traverseRun(root.right); // Applies onto right of tree if true/false
		} 
		
		return running; // Returns value (boolean) of running
	}
	public void clearTree() {
		root = null; // This method removes all the values from the tree when I want to clear the display in bSim
	}
}
	