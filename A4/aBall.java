import java.awt.Color;
import acm.graphics.GOval;

//ECSE-202: Assignment 3 by Anthony Harissi Dagher (260924250)

public class aBall extends Thread {

	//Initializing variables that will be used throughout the aBall class
	GOval myBall;
	double Xi, Yi, Vo, theta, bSize, bLoss, Vx, Vy, X, Y;
	Color bColor;
	volatile static boolean runs;
	volatile static boolean bSimRun;
	static bSim link;
	
	// Specifies the parameters required for the simulation, mostly from A-1
	private static final double G = 9.8; // Gravitational pull constant
	private static final double Pi = 3.141592654; // Pi value for trigonometric equations
	private static final double k = 0.0001; // k parameter for terminal velocity equation
	private static final double TICK = 0.1; // Represents the clock ticking speed (seconds)
	private static final double ETHR = 0.01; // If either velocities < ETHR, stop simulation
	private static final double HEIGHT = 600; // Screen height (pixels)
	private static final double SCALE = HEIGHT/100; // Converts units (pixels/meter)
	private static final double PD = 1; // Trace point size

	/**
	 * Parameters for the aBall constructor
	 * @param Xi double The initial X position of the center of the ball
	 * @param Yi double The initial Y position of the center of the ball
	 * @param Vo double The initial velocity of the ball at launch
	 * @param theta double Launch angle
	 * @param bSize double The radius of the ball in simulation units
	 * @param bColor  Color The initial color of the ball
	 * @param bLoss double Fraction [0,1] of the energy lost on each bounce 
	 */
	
	public aBall(double Xi, double Yi, double Vo, double theta, 
				 double bSize, Color bColor, double bLoss, bSim link) {
		
		// Sets the simulation parameters
		this.Xi = Xi;
		this.Yi = Yi;
		this.Vo = Vo;
		this.theta = theta;
		this.bSize = bSize;
		this.bColor = bColor;
		this.bLoss = bLoss;
		this.link = link;
		
		// Instantiation of the GOval instance
		myBall = new GOval(Xi*SCALE-bSize*SCALE, HEIGHT-Yi*SCALE, 2*bSize*SCALE, 2*bSize*SCALE);
		myBall.setFilled(true); // Filled oval
		myBall.setFillColor(bColor); // Oval that will be the color of bColor (random)
	}
	
	// Allows GOval to be accessible outside of aBall when called by "getBall"
	public GOval getBall() {
		return myBall;
	}	
	/**
	 * Implements the simulation loop from Assignment 1
	 * @param void
	 * @return void
	 */
	
	// Executes the code beneath it
	public void run() {
		
		// The following variables are initialized prior to the execution of the code
		double Ylast = 0; // The ball's final Y location
		double Xlast = 0; // The ball's final X location
		double time = 0; // Time of simulation
		double OffsetX = 0; // Variable used to calculate the sum of X distance

		// Initializing variables to correspond with physics motion equations
		double Vox = this.Vo*Math.cos(theta*Pi/180); // Horizontal velocity
		double Voy = this.Vo*Math.sin(theta*Pi/180); // Vertical velocity
		double Vt = G/(4*Pi*bSize*bSize*k); // Terminal velocity
		double KEx = 0.5*Vx*Vx*(1-bLoss); // Horizontal energy w/ loss
		double KEy = 0.5*Vy*Vy*(1-bLoss); // Vertical energy w/ loss
		
		runs = true; // There will be ball movement
		bSimRun = true; // This boolean allows me to control the while loop within bSim
		// The following code serves as the simulation loop
		while (bSimRun) {
			// The following code defines the location and speed of the ball for each parabola
			Xlast=X;
			Ylast=Y;
			X = (Vox*Vt/G*(1-Math.exp(-G*time/Vt)))+OffsetX;
			Y = (bSize + Vt/G*(Voy+Vt)*(1-Math.exp(-G*time/Vt))-Vt*time);
			Vy = (Y-Ylast)/TICK;
			Vx = (X-Xlast)/TICK;
			
			// The following code updates the ball's velocity only upon impact
			if (Vy < 0 && Y <= bSize) {
				
				KEx = 0.5*Vx*Vx*(1-bLoss); // Calculates horizontal kinetic energy w/ loss for the simulation
				KEy = 0.5*Vy*Vy*(1-bLoss); // Calculates vertical kinetic energy w/ loss for the simulation
				Y = bSize; // Sets the balls location to the floor
				OffsetX = X; // Sums up the previous traveled distance for X
				time = 0; // Resets the time for the new parabola
				Voy = Math.sqrt(2*KEy); // Updates the vertical speed after energy loss on impact
				Vox = Math.sqrt(2*KEx); // Updates the horizontal speed after energy loss
			
				// The following if statement is applied for balls heading to the left
				if (X < 0) {
					Vox = -Vox; // Modifies X velocity direction for parabolas going from right to left
					// The following if statement terminates the code if the speed is negligible, therefore, the energy is negligible
					if(Vox > -ETHR || Voy < ETHR) {
						break;
					}
				}
				
				// The following if statement is applied when the ball is heading to the right
				if(X > 0) {	
					// The following if statement terminates the code if the speed is negligible, therefore, the energy is negligible
					if(Vox < ETHR || Voy < ETHR) {
						break;
					}
				}
			}
			// Display update
			time+=TICK; // Updates the time
			double setX = (Xi+SCALE*X-SCALE*2*bSize); // Changes X location on screen
			double setY = (HEIGHT-SCALE*bSize-SCALE*Y); // Changes Y location on screen
			myBall.setLocation(setX,setY); // Places each instance of the ball at correct spot according to their parabola
			
			if(link != null) {
				if(bSim.traceOn() == true) { // Checks if the traceOn method is true before tracing
					trace(X+Xi/SCALE-bSize,Y); // Plots the corresponding trace points	
				}
			}
			
			try {
				Thread.sleep(50); // Pause for 50 milliseconds so that the display catches up with the code
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		runs = false; // There is no more movement
	}	
	// MoveTo method that will assist bTree to re-organize the balls when called on
	void moveTo(double x, double y) {
		
		this.myBall.setLocation(x, y);
	}
	// Trace balls method to create the dots following the balls
	public void trace(double x, double y) {
		double ScrX = x*SCALE;
		double ScrY = HEIGHT - y*SCALE;
		GOval pt = new GOval(ScrX, ScrY, PD, PD);
		pt.setColor(bColor);
		pt.setFilled(true);
		link.add(pt);
	}

}
