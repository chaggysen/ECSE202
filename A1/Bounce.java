import java.awt.Color;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;


// ECSE-202: Assignment 1 by Anthony Harissi Dagher (260924250)

public class Bounce extends GraphicsProgram {
	
	// The following code is used to define my constant variables
	private static final int WIDTH = 600; // Screen width (pixels)
	private static final int HEIGHT = 600; // Screen height (pixels)
	private static final int OFFSET = 200; // Distance from bottom of screen to floor (pixels)
	private static final double XMAX = 100.0; // Initial screen dimension (x)
	private static final double YMAX = 100.0; // Initial screen dimension (y)
	private static final double SCALE = HEIGHT/XMAX; // Converts units (pixels/meter)
	private static final double G = 9.8; // Gravitational pull constant (m/s^2)
	private static final double Pi = 3.141592654; // Pi value for trigonometric equations
	private static final double TICK = 0.1; // Represents the clock ticking speed (seconds)
	private static final double PD = 1; // Diameter of points following the ball
	private static final double k = 0.0016; // k parameter for terminal velocity equation
	private static final double ETHR = 0.01; // If either Vx or Vy < ETHR, stop simulation
	private static final double Xinit = 5; // Initial ball location (x) 
	private static final boolean TEST = true; // Print information if test true
	
	// Initializes the other variables that will be used
	double Vx;
	double Vy;
	double X;
	double Y;
	
	// The following code runs the program while referencing the above variables
	public void run() {
		
		this.resize(WIDTH, OFFSET+HEIGHT); // Resizes the applet
		
		// The following code will allow user input for some simulation parameters
		double Vo = readDouble ("Enter the Initial Velocity (m/s) [0,100]: "); // Initial velocity
		double bSize = readDouble ("Enter the Ball Radius (m) [0.1, 5.0]: "); // Ball radius
		double loss = readDouble ("Enter the Energy Loss Parameter [0,1]: "); // Energy loss
		double theta = readDouble ("Enter the Launch Angle (degrees) [0, 90]: "); // Launch angle
		
		// This group of code creates the floor where the ball should land
		GRect floor = new GRect(0,SCALE*XMAX,SCALE*YMAX,3);
		floor.setColor(Color.BLACK);
		floor.setFilled(true);
		add(floor); // Adds the customized rectangle (the floor)
										
		// This group of code creates the instance of the ball
		GOval ball = new GOval((Xinit - 2*bSize)*SCALE,(HEIGHT+((2*bSize*SCALE))),2*bSize*SCALE,2*bSize*SCALE);
		ball.setColor(Color.BLUE);
		ball.setFilled(true);
		add(ball); // Adds the customized oval (the ball)	
		ball.setLocation((Xinit),(0)); // Sets the initial location for the ball
		
		// The following variables are initialized prior to the execution of the code
		double Ylast = 0; // The ball's final Y location
		double Xlast = 0; // The ball's final X location
		double time = 0; // Time of simulation
		double OffsetX = 0; // Variable used to calculate the sum of X distance
		
		// Initializing variables to correspond with physics motion equations
		double Vox = Vo*Math.cos(theta*Pi/180); // Horizontal velocity
		double Voy = Vo*Math.sin(theta*Pi/180); // Vertical velocity
		double Vt = G/(4*Pi*bSize*bSize*k); // Terminal velocity
		double KEx = 0.5*Vx*Vx*(1-loss); // Horizontal energy loss
		double KEy = 0.5*Vy*Vy*(1-loss); // Vertical energy loss
		
		
		// The following code serves as the simulation loop
		while (true) {	
			// The following code defines the location and speed of the ball for each parabola
			Xlast=X;
			Ylast=Y;
			X = (Vox*Vt/G*(1-Math.exp(-G*time/Vt)))+OffsetX;
			Y = (bSize + Vt/G*(Voy+Vt)*(1-Math.exp(-G*time/Vt))-Vt*time);
			Vy = (Y-Ylast)/TICK;
			Vx = (X-Xlast)/TICK;
			
			if(TEST) {
				System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy: %.2f\n", 
							   time, X,Y,Vx,Vy); // Prints the location, speed and time
			}
			
			// The following code updates the ball's velocity upon impact
			if (Vy < 0 && Y <= bSize) {
				KEx = 0.5*Vx*Vx*(1-loss); // Calculates horizontal kinetic energy w/ loss for the simulation
				KEy = 0.5*Vy*Vy*(1-loss); // Calculates vertical kinetic energy w/ loss for the simulation
				Y = bSize; // Sets the balls location to the floor
				OffsetX = X; // Sums up the previous traveled distance for X
				time = 0; // Resets the time for the new parabola
				Vox = Math.sqrt(2*KEx); // Updates the horizontal speed after energy loss on impact
				Voy = Math.sqrt(2*KEy); // Updates the vertical speed after energy loss on impact
				
			}
			
			// The following code makes sure that the ball does not bounce forever
			if(Vy < ETHR  && Vx < ETHR) {
				break; // Terminates the code once both speeds are practically null
			}
			
			//Display update
			double setX = (SCALE*X-SCALE*bSize);
			double setY = ((HEIGHT-SCALE*bSize-SCALE*Y));
			ball.setLocation(setX, setY); // Screen units
			ballLocation(X,Y); // Sets the ball's location to the X and Y values
			time+=TICK; // Modifies the time
			pause(60); // Pause to make sure the ball's simulation isn't too fast
		
		}
	}
	
		// The following code creates the points that follow the ball
		public void ballLocation(double X, double Y) {
			double setY = (HEIGHT - (SCALE*Y)); // Setting the points to the ball's Y location
			double setX = (SCALE*X); // Setting the points to the ball's X location
			GRect dots = new GRect(setX, setY, PD, PD); // Defining the points size and location
			dots.setFilled(true);
			dots.setColor(Color.BLACK);
			add(dots); // Adding the trace points to the simulation
		}
}