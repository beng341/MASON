package agents;

import sim.util.*;

/**
 * 
 * This class is used to create arrays for quick look up of directional change
 * and orientation.  Consider that an agent is located in a cell surround by 
 * 8 possible cells it can move into with a one-step move:
 * 
 *              _____________________
 *              |      |      |      | 
 *              |  NW  |  N   |  NE  |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  W   |  X   |  E   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  SW  |  S   |  SE  |
 *              |______|______|______|
 *              
 * If agent X is oriented toward N (north), then there are eight possible directions it
 * can move.  We can create a probability matrix for how likely it is to randomly move
 * into one of the 8 adjacent cells:
 * 
 *               _____________________
 *              |      |      |      | 
 *              | .05  |  0   | .05  |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *
 * This is the probability matrix that we are currently using.  An agent has an equal
 * probability of moving either to the NW (northwest) or NE (northeast).  If we wanted 
 * our agent to  only move forward, then our probability matrix would look like this:
 * 
 *              _____________________
 *              |      |      |      | 
 *              | 0    | 1.0  |  0   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *              
 * If we wanted our agent to move back and forth between two cells, we would use the following
 * probability matrix:
 * 
 *              _____________________
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  0   |  0   |
 *              |______|______|______|
 *              |      |      |      | 
 *              |  0   |  1.0 |  0   |
 *              |______|______|______|
 *              
 *A "Brownian motion" particle would result form the probability matrix:
 *
 *               _____________________
 *              |      |      |      | 
 *              | .125 | .125 | .125 |
 *              |______|______|______|
 *              |      |      |      | 
 *              | .125 |  0   | .125 |
 *              |______|______|______|
 *              |      |      |      | 
 *              | .125 | .125 | .125 |
 *              |______|______|______|
 *              
 * To choose a cell with a given probability, we first create that sums the probabilities
 * from N to NW
 *               _____________________
 *              |      |      |      | 
 *              | 1.0  | .125 | .25  |
 *              |______|______|______|
 *              |      |      |      | 
 *              | .875 |  0   | .375 |
 *              |______|______|______|
 *              |      |      |      | 
 *              | .75  | .675 | .5   |
 *              |______|______|______|
 *
 * Now, if we generate a random number in the range [0,1), then we can determine which cell
 * it fits into.  For example, if we get the random number 0.5938189, we see that it is greater
 * than .5 but less than .675, so it lands in the S (south cell) and that is the direction the
 * agent will move.
 * 
 * We can number these cells:
 * 
 *              _____________________
 *              |      |      |      | 
 *              | NW=7 | N=0  | NE=1 |
 *              |______|______|______|
 *              |      |      |      | 
 *              | W=6  |  X   | E=2  |
 *              |______|______|______|
 *              |      |      |      | 
 *              | SW=5 | S=4  | SE=3 |
 *              |______|______|______| 
 *
 * The changes in direction that agent X needs to make to get to each cell are given in the 
 * matrix below, where the pairs are (x,y) directions of change;
 * 
 *              ________________________
 *              |       |       |       | 
 *              | (-1,1)| (0,1) | (1,1) |
 *              |_______|_______|_______|
 *              |       |       |       | 
 *              |(-1,0) |   X   | (1,0) |
 *              |_______|_______|_______|
 *              |       |       |       | 
 *              |(-1,-1)| (1,-1)| (1,-1)|
 *              |_______|_______|_______| 
 *              
 * The method makeMovementProbsSum(double[][] movementProbs) generates a summed matrix of
 * probabilities from which we can quickly generate number 0 to 7 corresponding to the 
 * different directions, which we can look up in an array that holds Int2D pairs of numbers
 * corresponding to the matrix above.
 * 
 * The main complication is that agents are not always oriented to the north.  They can be
 * facing any direction, so we need to rotate the probability matrix to correspon to each
 * of these possible orientations.  Thus, to look up a direction change pair, we need to
 * know both the orientation of the agent and the direction of change.  This matrix is created
 * by makeDirectionLookUp ().  For example, suppose the orientation of an agent is 3, and 
 * the direction of change is 7, what is the pair of direction changes that will be selected?
 * 
 * directionLookUp[3][7]= new Int2D(1,0);, which is cell number 2.
 * 
 * Also, for walk away and hit and run agents, we have them randomly move.  We need to look
 * up their new orientation after they move.  If we assume that they move head first into
 * the next cell, then the orientation should be from the celled just left to the cell
 * just moved to.  For example, if an agent X moves (=1,1), then it new orientation 
 * is NW = 7.  The new orientation is independent of the current orientation, so the
 * look up is simple, as indicated below.
 * 
 * 
 */

public class Probabilities implements java.io.Serializable {
	
	public int[][] orientationLookUp;
	public Int2D[][] directionLookUp;
	public double[][] movementProbsSums;
	public Int2D[] randomMovement;
	
	public Probabilities() {
		
	}
	
	public double[][] makeMovementProbsSum(double[][] movementProbs) {
		movementProbsSums = new double[3][3];
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				movementProbsSums[i][j] = 0.0;
		
		double[][] workingArray = new double[3][3];
		double x = 0.0;
		for(int i=0; i<3;i++)
			for(int j=0;j<3;j++) {
				x += movementProbs[i][j];
				workingArray[i][j] = movementProbs[i][j];
			}
		
		if(x <= 0){
			System.err.println("x = 0");
			System.exit(0); // the program quits
		}
			
		for(int i=0; i<3;i++)
			for(int j=0;j<3;j++)
				workingArray[i][j] = workingArray[i][j]/x;
		
		movementProbsSums[0][1] = workingArray[0][1];
		movementProbsSums[0][2] = movementProbsSums[0][1] + workingArray[0][2];
		movementProbsSums[1][2] = movementProbsSums[0][2] + workingArray[1][2];
		movementProbsSums[2][2] = movementProbsSums[1][2] + workingArray[2][2];
		movementProbsSums[2][1] = movementProbsSums[2][2] + workingArray[2][1];
		movementProbsSums[2][0] = movementProbsSums[2][1] + workingArray[2][0];
		movementProbsSums[1][0] = movementProbsSums[2][0] + workingArray[1][0];
		movementProbsSums[0][0] = 1.0;
		
		return movementProbsSums;
	}
	
	public Int2D[][] makeDirectionLookUp (){
		directionLookUp = new Int2D[8][8];
		
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				directionLookUp[i][j] = null;
		
		directionLookUp[0][0]= new Int2D(0,1); //north
		directionLookUp[0][1]= new Int2D(1,1); //northwest
		directionLookUp[0][2]= new Int2D(1,0); //east
		directionLookUp[0][3]= new Int2D(1,-1); //southeast
		directionLookUp[0][4]= new Int2D(0,-1); //south
		directionLookUp[0][5]= new Int2D(-1,-1); //southwest
		directionLookUp[0][6]= new Int2D(-1,0); //west
		directionLookUp[0][7]= new Int2D(-1,1); //northwest
		
		directionLookUp[1][0]= new Int2D(1,1); //north
		directionLookUp[1][1]= new Int2D(1,0); //northeast
		directionLookUp[1][2]= new Int2D(1,-1); //east
		directionLookUp[1][3]= new Int2D(0,-1); //southeast
		directionLookUp[1][4]= new Int2D(-1,-1); //south
		directionLookUp[1][5]= new Int2D(-1,0); //southwest
		directionLookUp[1][6]= new Int2D(-1,1); //west
		directionLookUp[1][7]= new Int2D(0,1); //northwest
		
		directionLookUp[2][0]= new Int2D(1,0); //north
		directionLookUp[2][1]= new Int2D(1,-1); //northeast
		directionLookUp[2][2]= new Int2D(0,-1); //east
		directionLookUp[2][3]= new Int2D(-1,-1); //southeast
		directionLookUp[2][4]= new Int2D(-1,0); //south
		directionLookUp[2][5]= new Int2D(-1,1); //southwest
		directionLookUp[2][6]= new Int2D(0,1); //west
		directionLookUp[2][7]= new Int2D(1,1); //northwest
		
		directionLookUp[3][0]= new Int2D(1,-1); //north
		directionLookUp[3][1]= new Int2D(0,-1); //northeast
		directionLookUp[3][2]= new Int2D(-1,-1); //east
		directionLookUp[3][3]= new Int2D(-1,0); //southeast
		directionLookUp[3][4]= new Int2D(-1,1); //south
		directionLookUp[3][5]= new Int2D(0,1); //southwest
		directionLookUp[3][6]= new Int2D(1,1); //west
		directionLookUp[3][7]= new Int2D(1,0); //northwest
		
		directionLookUp[4][0]= new Int2D(0,-1); //north
		directionLookUp[4][1]= new Int2D(-1,-1); //northeast
		directionLookUp[4][2]= new Int2D(-1,0); //east
		directionLookUp[4][3]= new Int2D(-1,1); //southeast
		directionLookUp[4][4]= new Int2D(0,1); //south
		directionLookUp[4][5]= new Int2D(1,1); //southwest
		directionLookUp[4][6]= new Int2D(1,0); //west
		directionLookUp[4][7]= new Int2D(1,-1); //northwest
		
		directionLookUp[5][0]= new Int2D(-1,-1); //north
		directionLookUp[5][1]= new Int2D(-1,0); //northeast
		directionLookUp[5][2]= new Int2D(-1,1); //east
		directionLookUp[5][3]= new Int2D(0,1); //southeast
		directionLookUp[5][4]= new Int2D(1,1); //south
		directionLookUp[5][5]= new Int2D(1,0); //southwest
		directionLookUp[5][6]= new Int2D(1,-1); //west
		directionLookUp[5][7]= new Int2D(0,-1); //northwest
		
		directionLookUp[6][0]= new Int2D(-1,0); //north
		directionLookUp[6][1]= new Int2D(-1,1); //northeast
		directionLookUp[6][2]= new Int2D(0,1); //east
		directionLookUp[6][3]= new Int2D(1,1); //southeast
		directionLookUp[6][4]= new Int2D(1,0); //south
		directionLookUp[6][5]= new Int2D(1,-1); //southwest
		directionLookUp[6][6]= new Int2D(0,-1); //west
		directionLookUp[6][7]= new Int2D(-1,-1); //northwest
		
		directionLookUp[7][0]= new Int2D(-1,1); //north
		directionLookUp[7][1]= new Int2D(0,1); //northeast
		directionLookUp[7][2]= new Int2D(1,1); //east
		directionLookUp[7][3]= new Int2D(1,0); //southeast
		directionLookUp[7][4]= new Int2D(1,-1); //south
		directionLookUp[7][5]= new Int2D(0,-1); //southwest
		directionLookUp[7][6]= new Int2D(-1,-1); //west
		directionLookUp[7][7]= new Int2D(-1,0); //northwest

		return directionLookUp;
	}
	
	public int[][] makeOrientationLookUp() {
		orientationLookUp = new int[3][3];
		for(int i =0;i<3;i++)
			for(int j=0;j<3;j++)
				orientationLookUp[i][j]=0;
		orientationLookUp[1][2] = 0;//north
		orientationLookUp[2][2] = 1;
		orientationLookUp[2][1] = 2;
		orientationLookUp[2][0] = 3;
		orientationLookUp[1][0] = 4;
		orientationLookUp[0][0] = 5;
		orientationLookUp[0][1] = 6;
		orientationLookUp[0][2] = 7;
		
		
		return orientationLookUp;
	}
	
	public Int2D[] makeRandomMovement(){
		randomMovement = new Int2D[8];
		
		randomMovement[0]= new Int2D(0,1); //north
		randomMovement[1]= new Int2D(1,1); //northwest
		randomMovement[2]= new Int2D(1,0); //east
		randomMovement[3]= new Int2D(1,-1); //southeast
		randomMovement[4]= new Int2D(0,-1); //south
		randomMovement[5]= new Int2D(-1,-1); //southwest
		randomMovement[6]= new Int2D(-1,0); //west
		randomMovement[7]= new Int2D(-1,1); //northwest
		
		return randomMovement;
	}
	
}