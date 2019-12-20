#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>

// Defining constants using the C notation
#define G 9.8 // Gravitational pull constant
#define Pi 3.141592654 // Pi constant
#define TICK 0.1 // Time speed
#define k 0.0016 // Terminal velocity
#define ETHR 0.01 // Minimum distance
#define false 0 // Creating a boolean in C
#define TEST !false // Associating this boolean to TEST

// Initializing some variables
double Vx, Vy, Xp, Yp;
double Vo, bSize, loss, theta;

// This code basically translates to the run method of Java
int main(void) {

	// The following code will allow user input for some simulation parameters
	printf(("Enter the Initial Velocity (m/s) [0, 100]: ")); // Initial velocity
	scanf("%lf",&Vo);

	printf(("Enter the Ball Radius (m) [0.1, 5,0]: ")); // Ball radius
	scanf("%lf",&bSize);

	printf(("Enter the Energy Loss Parameter [0.0, 1.0]: ")); // Energy loss
	scanf("%lf",&loss);

	printf(("Enter the Launch Angle (degrees) [0, 90]: ")); // Launch angle
	scanf("%lf",&theta);

	// The following variables are initialized prior to the execution of the code
	double Ylast = 0; // The ball's final Y location
	double Xlast = 0; // The ball's final X location
	double time = 0; // Time of simulation
	double OffsetX = 0; // Variable used to calculate the sum of X distance
		
	// Initializing variables to correspond with physics motion equations
	double Vox = Vo*cos(theta*Pi/180); // Horizontal velocity
	double Voy = Vo*sin(theta*Pi/180); // Vertical velocity
	double Vt = G/(4*Pi*bSize*bSize*k); // Terminal velocity
	double KEx = 0.5*Vx*Vx*(1-loss); // Horizontal energy loss
	double KEy = 0.5*Vy*Vy*(1-loss); // Vertical energy loss

	// The following code serves as the simulation loop, 1 signifying true
	while (1) {	
		// The following code defines the location and speed of the ball for each parabola
		Xlast=Xp;
		Ylast=Yp;
		Xp = (Vox*Vt/G*(1-exp(-G*time/Vt)))+OffsetX+5;
		Yp = (bSize + Vt/G*(Voy+Vt)*(1-exp(-G*time/Vt))-Vt*time);
		Vy = (Yp-Ylast)/TICK;
		Vx = (Xp-Xlast)/TICK;
			
		if(TEST) {
			printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy: %.2f\n", time, Xp,Yp,Vx,Vy); // Prints the location, speed and time
		}
			
		// The following code updates the ball's velocity upon impact
		if (Vy < 0 && Yp <= bSize) {
			KEx = 0.5*Vx*Vx*(1-loss); // Calculates horizontal kinetic energy w/ loss for the simulation
			KEy = 0.5*Vy*Vy*(1-loss); // Calculates vertical kinetic energy w/ loss for the simulation
			Yp = bSize; // Sets the balls location to the floor
			OffsetX = Xp; // Sums up the previous traveled distance for X
			time = 0; // Resets the time for the new parabola
			Vox = sqrt(2*KEx); // Updates the horizontal speed after energy loss on impact
			Voy = sqrt(2*KEy); // Updates the vertical speed after energy loss on impact
		}

		// The following code makes sure that the ball does not bounce forever
		if(Vy < ETHR  && Vx < ETHR) {
			break; // Terminates the code once both speeds are practically null
		}
		time+=TICK;
	}
return 0; // This exits the program without error
}



