import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.gui.DoubleField;
import acm.gui.IntField;
import acm.gui.TableLayout;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

//ECSE-202: Assignment 3 by Anthony Harissi Dagher (260924250)

public class bSim extends GraphicsProgram implements ItemListener{
		
		private static final int WIDTH = 1200; // Screen width (pixels)
		private static final int HEIGHT = 600; // Screen height (pixels)
		private static final int OFFSET = 200; // Distance from bottom of screen to floor (pixels)
		private static final double SCALE = HEIGHT/100; // Converts units (pixels/meter)
		private static final int NUMBALLS = 60; // Initial number balls to simulate
		private static final double MINSIZE = 1.0; // Minimum ball radius (meters)
		private static final double MAXSIZE = 7.0; // Maximum ball radius (meters)
		private static final double EMIN = 0.2; // Minimum loss coefficient
		private static final double EMAX = 0.6; // Maximum loss coefficient
		private static final double VoMax = 50.0; // Maximum velocity (meters/second)
		private static final double VoMin = 40.0; // Minimum velocity (meters/second) 
		private static final double ThetaMIN = 80.0; // Minimum launch angle (degrees)
		private static final double ThetaMAX = 100.0; // Maximum launch angle (degrees)
		private static final int PanelWidth = 100; // Width of panel

		JComboBox<String> bSimC = new JComboBox<String>();// Defines bSimC as a ComboBox
		bTree myTree = new bTree(); // Sets up the bTree in bSim class, referenced as myTree
		
		// Initiates a few variables for the fields, as well as the toggle button and the text messages
		private DoubleField minVelocityField;
		private DoubleField maxElossField;
		private IntField numballsField;
		private DoubleField minSizeField;
		private DoubleField minThetaField;
		private DoubleField maxVelocityField;
		private DoubleField minElossField;
		private DoubleField maxSizeField;
		private DoubleField maxThetaField;
		static JToggleButton toggle;
		GLabel label = new GLabel("action", WIDTH-OFFSET+OFFSET/5, HEIGHT-OFFSET/18);
	
		// Defines the combo box choices for selection
		public void ComboBoxChoices() {
			
			bSimC.addItem("bSimC");	// Adds initial choice (0)
			bSimC.addItem("Run");	// Adds choice of running program to ComboBox (1)
			bSimC.addItem("Stack");	// Adds choice of stacking balls to ComboBox (2)
			bSimC.addItem("Clear");	// Adds choice of clearing display to ComboBox (3)
			bSimC.addItem("Stop");	// Adds choice of stopping program to ComboBox (4)
			bSimC.addItem("Quit"); // Adds choice of quitting program to ComboBox (5)
			bSimC.setEditable(false); // Prevents any edits from the user during use
			bSimC.addItemListener((ItemListener)this); // Adds an ItemListener to update whenever the user uses a selection
			add(bSimC, NORTH); // Adds this ComboBox on the top of display
		}
		
		// Starts the Java applet (user interface)
		public void init() {
			
			ComboBoxChoices(); // Sets the ComboBox in the display
			
			// Sets up the panel that will incorporate all the sliders
			JPanel general = new JPanel(); // Creates the panel
			JLabel message = new JLabel("Ball Variables"); // Name for the panel
			JLabel space = new JLabel(" "); // Adds space under the title name
			add(message, EAST); // Adds the message
			add(space, EAST); // Adds a space
			general.setSize(PanelWidth, HEIGHT+OFFSET); // Sets the size of the panel
			general.setLayout(new TableLayout(9,3)); // Sets the layout of the panel (ROWS/COLUMNS)
			
			/*The following code sets up the maximum, minimum, and minimum slider values of my variables
			 *and the fields where the user can input a certain value (fields)
			 */
			minVelocityField = new DoubleField(VoMin,1,200);
			maxVelocityField = new DoubleField(VoMax,1,200);
			
			maxElossField = new DoubleField(EMAX,0,1);
			minElossField = new DoubleField(EMIN,0,1);
			
			numballsField = new IntField(NUMBALLS,1,255);
			
			minSizeField = new DoubleField(MINSIZE,1,25);
			maxSizeField = new DoubleField(MAXSIZE,1,25);
			
			minThetaField = new DoubleField(ThetaMIN,1,180);
			maxThetaField = new DoubleField(ThetaMAX,1,180);
			
			/* The following code adds the variable sliders/fields to the panel and a label
			 * informing users of the maximum and minimum values 
			 */
			
			// Velocity
			general.add(new JLabel("MIN VEL"+" 1.0"));
			general.add(minVelocityField); // Adds field to panel
			general.add(new JLabel("200.0"));
			general.add(new JLabel("MAX VEL"+" 1.0"));
			general.add(maxVelocityField); // Adds field to panel
			general.add(new JLabel("200.0"));
		
			// Energy loss
			general.add(new JLabel("MIN LOSS"+" 0.0"));
			general.add(minElossField); // Adds field to panel
			general.add(new JLabel("1.0"));
			general.add(new JLabel("MAX LOSS"+" 0.0"));
			general.add(maxElossField); // Adds field to panel
			general.add(new JLabel("1.0"));
			
			// Number of balls
			general.add(new JLabel("NUMBALLS"+" 1"));
			general.add(numballsField); // Adds field to panel
			general.add(new JLabel("255"));
			
			// Size
			general.add(new JLabel("MIN SIZE"+" 1.0"));
			general.add(minSizeField); // Adds field to panel
			general.add(new JLabel("25.0"));
			general.add(new JLabel("MAX SIZE"+" 1.0"));
			general.add(maxSizeField); // Adds field to panel
			general.add(new JLabel("25.0"));
			
			// Launch angle
			general.add(new JLabel("MIN ANGLE"+" 0.0"));
			general.add(minThetaField); // Adds field to panel
			general.add(new JLabel("180.0"));
			general.add(new JLabel("MAX ANGLE"+" 0.0"));
			general.add(maxThetaField); // Adds field to panel
			general.add(new JLabel("180.0"));
			
			add(general, EAST); // Adds the panel on the right side of the display
			
			toggle = new JToggleButton("Trace"); // Sets up Toggle button
			add(toggle, SOUTH); // Adds button on bottom of display	
		
			// This group of code creates the floor where the ball should land
			GRect floor = new GRect(0,HEIGHT,WIDTH,3); // Defines the rectangles location and size
			floor.setColor(Color.BLACK); // Black rectangle
			floor.setFilled(true); // Filled rectangle
			add(floor); // Adds the customized rectangle (the floor)
			this.resize(WIDTH+OFFSET/2+PanelWidth, OFFSET+HEIGHT); // Resizes the applet
		
		}
		
		/* This method will return true if the trace button is selected, 
		 * which will control the trace method in aBall.
		 */
		public static boolean traceOn() {
			return toggle.isSelected(); // isSelected returns true if the button is pushed, false if not
		}
		
		// This method holds the main simulation code
		public void doSim() {
			
			RandomGenerator generate = RandomGenerator.getInstance(); // Creates instance for random generation of values
			generate.setSeed((long) 424242); // Seed to have the same values as the professor
			
			// Runs the for loop until the amount of balls reaches the limit defined by the "NUMBALLS" parameter 
			for (int amountBalls=0; amountBalls < numballsField.getValue(); amountBalls++) {
				
				// Generates random values for the balls, and adds them to the applet
				double bSize = generate.nextDouble(minSizeField.getValue(), maxSizeField.getValue()); // Defines the limits for the ball size
				double Xi = (WIDTH/2); // Sets the initial X position
				double Yi = (bSize*getScale()); // Sets the initial Y position
				Color bColor = generate.nextColor(); // Allows the ball to be any random color
				double bLoss = generate.nextDouble(minElossField.getValue(), maxElossField.getValue()); // Defines the limits for the loss coefficient
				double Vo = generate.nextDouble(minVelocityField.getValue(), maxVelocityField.getValue()); // Defines the limits for the initial velocity
				double theta = generate.nextDouble(minThetaField.getValue(), maxThetaField.getValue()); // Defines the limits for the launch angle
				
				aBall newBalls = new aBall(Xi, Yi, Vo, theta, bSize, bColor, bLoss, this); // Associates the constructor to these random variables
				add(newBalls.getBall()); // Adds the balls to the display
				myTree.addNode(newBalls);// Adds the myTree addNode method to newBalls
				newBalls.start(); // Runs the thread
			}
		}
		
		// This method stacks the balls, even if the run simulation wasn't fully finished
		public void doHist() {
			
			aBall.bSimRun=false; // Stops the simulation
			myTree.stackBalls(); // Stacks the balls
		}
		
		// The following method associates an action to each of the ComboBox choices
		public void itemStateChanged(ItemEvent e) {
				
			JComboBox<?> source = (JComboBox<?>)e.getSource();
			
			if (source == bSimC) {
				
				if (bSimC.getSelectedIndex()==1) {
					/*The following if statement is necessary since ItemListeners 
					 * perform the task twice (at selection and at de-selection), whereas in our case
					 * we only want the balls to be created once, at selection.
					 */
					if(e.getStateChange()== ItemEvent.SELECTED) { 
						label.setLabel("Simulation"); // Updates the message
						add(label); // Adds updated label
						doSim(); // If user selects first choice, Run, the ball simulation is executed
					}
				}
				else if (bSimC.getSelectedIndex()==2) {
					label.setLabel("All Stacked"); // Updates the message
					add(label); // Adds updated label
					doHist(); // If user selects second choice, Stack, the balls get stacked in order
				}
				else if (bSimC.getSelectedIndex()==3) {
					removeAll(); // If user selects third choice, Clear, the display is cleared
					myTree.clearTree(); // Calls the clearTree method from bTree
					
					// By removingAll, we lost the floor -> Below is the code to replace the floor
					GRect floor = new GRect(0,HEIGHT,WIDTH,3); // Defines the rectangles location and size
					floor.setColor(Color.BLACK); // Black rectangle
					floor.setFilled(true); // Filled rectangle
					add(floor); // Adds the customized rectangle (the floor)
					label.setLabel("Cleared"); // Updates the message
					add(label); // Adds updated label
				}
				else if (bSimC.getSelectedIndex()==4) {
					label.setLabel("Stopped"); // Updates the message
					add(label); // Adds updated label
					aBall.bSimRun=false; // If the user selects the fourth choice, Stop, the while loop in aBall is halted
				}
				else if (bSimC.getSelectedIndex()==5) {
					System.exit(0); // If the user selects the fifth choice, Exit, the applet closes
				}
			}
		}
		
		// Sets getter for SCALE parameter
		public static double getScale() {	
			return SCALE;
		}
}
