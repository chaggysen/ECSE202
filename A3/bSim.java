import java.awt.Color;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

//ECSE-202: Assignment 3 by Anthony Harissi Dagher (260924250)

public class bSim extends GraphicsProgram {
		
		private static final int WIDTH = 1200; // Screen width (pixels)
		private static final int HEIGHT = 600; // Screen height (pixels)
		private static final int OFFSET = 200; // Distance from bottom of screen to floor (pixels)
		private static final double SCALE = HEIGHT/100; // Converts units (pixels/meter)
		private static final double NUMBALLS = 60; // # balls to simulate
		private static final double MINSIZE = 1.0; // Minimum ball radius (meters)
		private static final double MAXSIZE = 7.0; // Maximum ball radius (meters)
		private static final double EMIN = 0.2; // Minimum loss coefficient
		private static final double EMAX = 0.6; // Maximum loss coefficient
		private static final double VoMax = 50.0; // Maximum velocity (meters/second)
		private static final double VoMin = 40.0; // Minimum velocity (meters/second) 
		private static final double ThetaMIN = 80.0; // Minimum launch angle (degrees)
		private static final double ThetaMAX = 100.0; // Maximum launch angle (degrees)
		
		// Executes the code beneath it
		public void run() {
			
			this.resize(WIDTH, OFFSET+HEIGHT); // Resizes the applet
			
			RandomGenerator generate = new RandomGenerator(); //Creates instance for random generation of values
			bTree myTree = new bTree(); // Sets up the bTree in bSim class, referenced as myTree
			generate.setSeed((long)424242); // Creates identical results for each run, for evaluation purposes
			
			// This group of code creates the floor where the ball should land
			GRect floor = new GRect(0,HEIGHT,WIDTH,3); // Defines the rectangles location and size
			floor.setColor(Color.BLACK); // Black rectangle
			floor.setFilled(true); // Filled rectangle
			add(floor); // Adds the customized rectangle (the floor)
			
			// Runs the for loop until the amount of balls reaches the limit defined by the "NUMBALLS" parameter 
			for (int amountBalls=0; NUMBALLS > amountBalls; amountBalls++) {
				
				// Generates random values for the balls, and adds them to the applet
				double bSize = generate.nextDouble(MINSIZE, MAXSIZE); // Defines the limits for the ball size
				Color bColor = generate.nextColor(); // Allows the ball to be any random color
				double bLoss = generate.nextDouble(EMIN, EMAX); // Defines the limits for the loss coefficient
				double Vo = generate.nextDouble(VoMin, VoMax); // Defines the limits for the initial velocity
				double theta = generate.nextDouble(ThetaMIN, ThetaMAX); // Defines the limits for the launch angle
				double Xi = (WIDTH/2); // Sets the initial X position
				double Yi = (bSize*getScale()); // Sets the initial Y position
			
				aBall newBalls = new aBall(Xi, Yi, Vo, theta, bSize, bColor, bLoss); // Associates the constructor to these random variables
				add(newBalls.getBall()); // Adds the balls to the display
				myTree.addNode(newBalls);// Adds the myTree addNode method to newBalls
				newBalls.start(); // Runs the thread
			}
			// While loop checks if balls are running or not, only runs once stopped
			while(myTree.isRunning()) {
				
				// Adds label for user, prompting the user to click
				GLabel userPrompt = new GLabel("CR to continue", WIDTH-OFFSET+OFFSET/5, HEIGHT-OFFSET/15);
				userPrompt.setColor(Color.MAGENTA); // Pink text to be visible
				add(userPrompt); // Adds the customized label
				// The code below only runs after the user clicks mouse
				waitForClick(); // Waits for user to click mouse
				myTree.stackBalls(); // Runs stackBall method from bTree
				userPrompt.setLabel("All Stacked!"); // Adds the final message, informing user that the balls are stacked
			}
		}
		// Sets getter for SCALE parameter
		public static double getScale() {
			return SCALE;
		}
}