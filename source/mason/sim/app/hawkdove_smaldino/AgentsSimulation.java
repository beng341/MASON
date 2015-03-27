package hawkdove_smaldino;

import hawkdove_smaldino.control.SimController;
import java.awt.Color;

import hawkdove_smaldino.states.SimStateWithSimController;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.portrayal.Portrayal;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * Main class for the entire simulation model. This class is the base "world" that
 * holds all data that should be global to the simulation such as lists of Agent's
 * playing the game.
 * The majority of the global variables here are parameters for the model. They
 * are associated with getX(), setX(), and optionally domX() methods. domX() would
 * return a String array of possible values for the variable x.
 * start() is executed when the user presses the play button in the console (or
 * when they run main() in this class when not using a GUI). It sets up agent positions
 * and schedules each Agent to continue running its step() method until the simulation
 * is finished.
 * -BA
 */
public class AgentsSimulation extends SimStateWithSimController { //extend the class SimState
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ObjectGrid2D agentsSpace;
	public ObjectGridPortrayal2D agentsPortrayal = new ObjectGridPortrayal2D();
	public int gridWidth = 100;
	public int gridHeight = 100;
	public double startResource = 50.0;
	public double reproduceResource_Level = 100.0;
	public double maxResource = 150;
	public double Energy_Loss= 25;
	public long maxAgents = 5000;
	public int reproductionRadius = 1;
	public boolean randomReproduction = false;
	public double randomDeath = 0.00;
	public String[][] strategies ={{"Coop", "WCoop"},{"Defect", "WDefect"}, {"Mixed","WMixed"}};
	public int dove_n = 800; // default values;
	public int wdove_n = 0;
	public int hawk_n= 800;
	public int whawk_n =0;
	public int mixed_n = 0;
	public int wmixed_n = 0;
	public int[][] population ={{dove_n, wdove_n},{hawk_n, whawk_n}, {mixed_n,wmixed_n}};
	public double dove_pay_vs_dove = 25;
	public double dove_pay_vs_hawk = 0;
	public double hawk_pay_vs_dove = 50;
	public double hawk_pay_vs_hawk = 12.5;
	public double[][] payoffs = {{dove_pay_vs_dove,dove_pay_vs_hawk},{hawk_pay_vs_dove,hawk_pay_vs_hawk}};
	public boolean toroidal = true;
	public boolean mutation = false;
	public double mutationProb = 0.00;
	public double mixed_p = 0.5;
	public boolean mixedUniformRandom = true;
	
	public boolean occupySameSpace = false;
	public boolean aktipisDeath = false;
	public boolean useError = false;
	public double errorRate = 0.000;
	public boolean offSpringGetsHalf = false; //if false, offspring gets 50.
	
	public boolean alwaysWalk = false;
	public double epsteinMutation = 0.0; //probability of a C begetting a D or vice versa.
	public boolean printScreen = true;
	public int printSteps = 100;
	public boolean printToConsol = true;
	public boolean printDataRows = true;
	public long lengthOfSimulations = 1000000;
	public boolean doMutation = false;
	public String dataFileName = "fileName";
	public String dataFolderName = "folderName";
	Observer ob = null;
	public boolean saveDataToFile = false;
	public boolean savePNGToFile = false;
	public int numberOfSimulations; 	// The number of Simulations
	public int simulationCount = 0;
	public long lengthOfSimulationsWithoutMutation=0;
	public SimController sc = null;
	public boolean doGraphics = true;
	public Bag agentBag = new Bag();
	public boolean autoQuit = false;
	public String simulationFolder	= "";  			//folder that contains all of the files for a simulation
	public String parametersFile = "";   //file with simulation parameters
	public String dataFilePrefix;				// prepended to the number of the run
	public String dataFileFileType;					// file type saved	
	public String picFilePrefix = "filenamePic";	// prepended to the number of the run
	public String picFileFileType = "png";					// file type saved
	public Bag recycledAgents = new Bag();
	public boolean stopWhenHomogeneous = true; //stop the run if the population is all defectors or all cooperators. 
	public int simCount = 0;
	
	public boolean mooreNeighborhood = true;
	
	public Bag allAgents = new Bag();
	
	//Variables for Am Nat Revision!
	public boolean multipleInteractions = false; //if true, agents can play multiple times per step. 
	public boolean playWithAll = false; //if true, play simultaneously with all neighbors!
	
	/********* Variables for barriers ***********/
	public IntGrid2D wallGrid;
	final static public int NO_WALL = 0;
	final static public int PLUS_WALL = 1;
	final static public int BARRIER = 2;
	public int wallType = NO_WALL;
	
	/****************************************************************************/
	/**                      Variables for movement                         **/
	/****************************************************************************/
	
	public Probabilities probs = new Probabilities();
	public double[][] movementProbsSums = null;
    public int[][] orientationLookUp = null;
    public Int2D[][] directionLookUp = null;
    public Int2D[] randomMovement = null;
    final static public double[][] SPEEDSTER = {{1.0,1.0,1.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
    final static public double[][] ZIGZAG = {{1.0,0.0,1.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};
    final static public double[][] FLR = {{0.0,1.0,0.0},{1.0,0.0,1.0},{0.0,0.0,0.0}};
    final static public double[][] BROWNIAN = {{1.0,1.0,1.0},{1.0,0.0,1.0},{1.0,1.0,1.0}};
    final static public double[][] SIDESTEP = {{0.0,0.0,0.0},{1.0,0.0,1.0},{0.0,0.0,0.0}};
    final static public double[][] VON_NEUMANN = {{0.0,1.0,0.0},{1.0,0.0,1.0},{0.0,1.0,0.0}};
    final static public double[][] CLOSE_TO_HOME = {{0.0,0.0,0.0},{0.0,0.0,0.0},{1.0,0.0,1.0}};
    final static public double[][] CYCLONE = {{0.0,0.0,1.0},{0.0,0.0,1.0},{0.0,0.0,0.0}};
    final static public double[][] TAIL_CHASER = {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,1.0,1.0}};
    
    
    
    
    
    public int moveProb = 0;
    public double[][] movementProbs = ZIGZAG;
	/*	0: movementProbs = STANDARD_WALK; break;
		1: movementProbs = FORWARD_WALK; break;
		2: movementProbs = BROWNIAN;
    
	/****************************************************************************/
	/**                      Variables for game agents                         **/
	/****************************************************************************/
	public final static int COOPERATOR = 0;
	public final static int DEFECTOR = 1; 
	public final static int MIXED = 2;
	public final static int TIT_FOR_TAT = 3;
	public final static int NOT_WALK_AWAY = 0;
	public final static int WALK_AWAY = 1;
	public final static int COOPERATE = 0;
	public final static int DEFECT = 1;
	final public Portrayal dove_nAIVE = new sim.portrayal.simple.RectanglePortrayal2D(Color.blue);
	final public Portrayal COOP_W = new sim.portrayal.simple.OvalPortrayal2D(Color.blue);
	final public Portrayal hawk_nAIVE = new sim.portrayal.simple.RectanglePortrayal2D(Color.red);
	final public Portrayal DEFECT_W = new sim.portrayal.simple.OvalPortrayal2D(Color.red);
	
	/************* GET and SET Methods *******************************/
	public boolean getautoSim(){return autoSimulation;}
	public void setautoSim(boolean b){autoSimulation = b;}
//	public String getscript(){return script;}	
//	public void setscript(String s){script = s;}
	public String getdataFileName(){		return dataFileName; }
	public void setdataFileName(String s){		if(s.length() > 0)	dataFileName = s; }
	public String getdataFolderName(){		return dataFolderName; }
	public void setdataFolderName(String s){		if(s.length() > 0)		dataFolderName = s; }
	public boolean getsaveDataToFile(){		return saveDataToFile;	}
	public void setsaveDataToFile(boolean b){		saveDataToFile = b;	}
//	public boolean getsavePNGToFile(){		return savePNGToFile;	}
//	public void setsavePNGToFile(boolean b){		savePNGToFile = b;	}

//	public boolean getprintDataRows(){		return printDataRows;	}
//	public void setprintDataRows(boolean b){		printDataRows = b;	}

	
	public int getgridWidth (){   	return gridWidth;        }
    public void setgridWidth(int w){    if(w > 0){	gridWidth = w; gridHeight = w;     }  }
    //public int getgridHeight(){   	return gridHeight; }
    //public void setgridHeight(int h){   if(h > 0) gridHeight = h;    }
    public int getDoves(){   return dove_n; }
    public void setDoves(int h){  if(h >= 0) dove_n = h;    }
 
    public int getHawks(){   return hawk_n; }
    public void setHawks(int h){  if(h >= 0) hawk_n = h;    }
   // public int getWalkAwayCooperators(){   return wdove_n; }
  //  public void setWalkAwayCooperators(int h){  if(h >= 0) wdove_n = h;    }
  //  public int getWalkAwayDefectors(){   return whawk_n; }
  //  public void setWalkAwayDefectors(int h){  if(h >= 0) whawk_n = h;    }
//    public int getMixed(){   return mixed_n; }
//    public void setMixed(int h){  if(h >= 0) mixed_n = h;    }
//    public int getWalkAwayMixed(){   return wmixed_n; }
//    public void setWalkAwayMixed(int h){  if(h >= 0) wmixed_n = h;    }
//    public double getmixed_p(){ return mixed_p;}
//	  public void setmixed_p(double h){ if (h >= 0 && h <= 1) mixed_p = h;}
//    public boolean getmixedUniformRandom(){	return mixedUniformRandom;}
//	public void setmixedUniformRandom(boolean h){ mixedUniformRandom = h; }
    public long getPopulation_Size(){   	return maxAgents; }
    public void setPopulation_Size(long h){   if(h > 0) maxAgents = h;    }
	public boolean getToroidal(){	return toroidal;}
	public void setToroidal(boolean h){ toroidal = h; }
	public boolean getoccupySameSpace(){	return occupySameSpace;}
	public void setoccupySameSpace(boolean h){ occupySameSpace = h; }
	public boolean getrandomReproduction(){	return randomReproduction;}
	public void setrandomReproduction(boolean h){ randomReproduction = h; }
	public boolean getoffSpringGetsHalf(){	return offSpringGetsHalf;}
	public void setoffSpringGetsHalf(boolean h){ offSpringGetsHalf = h; }
	public double getEnergy_Loss(){ return Energy_Loss;}
	public void setEnergy_Loss(double h){ if (h >= 0) Energy_Loss = h;}
	public double getMax_Resource(){ return maxResource;}
	public void setMax_Resource(double h){ if (h >= 0) maxResource = h;}
	public double getrandomDeath(){ return randomDeath;}
	public void setrandomDeath(double h){ if (h >= 0) randomDeath = h;}
	public boolean getAktipisDeath(){	return aktipisDeath;}
	public void setAktipisDeath(boolean h){ aktipisDeath = h; }
//	public boolean getmutationON(){	return mutation;}
//	public void setmutationON(boolean h){ mutation = h; }
	String gameValues = "Game Values";
	public String getVALUES(){
		 return gameValues;
	}
	public double getDove_PayOff_vs_Dove(){   	return payoffs[COOPERATE][COOPERATE]; }
   public void setDove_PayOff_vs_Dove(double w){ 	dove_pay_vs_dove = w; payoffs[COOPERATE][COOPERATE] = w; }
   
   public double getDove_PayOff_vs_Hawk (){   	return payoffs[COOPERATE][DEFECT]; }
   public void setDove_PayOff_vs_Hawk (double w){ 	dove_pay_vs_hawk = w; payoffs[COOPERATE][DEFECT] = w; }
   
   public double getHawk_PayOff_vs_Dove (){   	return payoffs[DEFECT][COOPERATE]; }
   public void setHawk_PayOff_vs_Dove(double w){ 	hawk_pay_vs_dove = w; payoffs[DEFECT][COOPERATE] = w; }
   
   public double getHawk_PayOff_vs_Hawk (){   	return payoffs[DEFECT][DEFECT]; }
   public void setHawk_PayOff_vs_Hawk(double w){ 	hawk_pay_vs_hawk = w; payoffs[DEFECT][DEFECT] = w; }
   
   public boolean getuseError(){	return useError;}
   public void setuseError(boolean h){ useError = h; }
   public double getepsteinMutation(){ return epsteinMutation;}
   public void setepsteinMutation(double h){ if (h >= 0 && h <= 1) epsteinMutation = h;}
   public double getreproduceResource_Level(){ return reproduceResource_Level;}
   public void setreproduceResource_Level(double h){ if (h > 0 ) reproduceResource_Level = h;}
   public boolean getmultipleInteractions(){	return multipleInteractions;}
   public void setmultipleInteractions(boolean h){ multipleInteractions = h; }
   public boolean getplayWithAll(){	return playWithAll;}
   public void setplayWithAll(boolean h){ playWithAll = h; }
   //print get and set
   public boolean getprintScreen(){	return printScreen;}
   public void setprintScreen(boolean h){ printScreen = h; }
   public int getprintSteps(){ return printSteps;}
   public void setprintSteps(int h){ if (h > 0) printSteps = h;}
   public boolean getalwaysWalk(){	return alwaysWalk;}
   public void setalwaysWalk(boolean h){ alwaysWalk = h; }
	//Probabilistic Movement
    
    public int getmoveProb(){	return moveProb;}
    public void setmoveProb(int b){ 
    	moveProb = b;
    	switch(b) {
    	case 0: movementProbs = SPEEDSTER; break;
    	case 1: movementProbs = ZIGZAG; break;
    	case 2: movementProbs = BROWNIAN; break;
    	case 3: movementProbs = FLR; break;
    	case 4: movementProbs = SIDESTEP; break;
    	case 5: movementProbs = VON_NEUMANN; break;
    	case 6: movementProbs = CLOSE_TO_HOME; break;
    	case 7: movementProbs = CYCLONE; break;
    	case 8: movementProbs = TAIL_CHASER; break;
    	default: movementProbs = ZIGZAG;
    	}
    }
    public Object dommoveProb() 
    { 
    return new String[] { "Speedster", "Zigzag", "Brownian", "FLR", "Sidestep",
    					"von Neumann", "Close-To-Home", "Cyclone", "Tail Chaser"};
    }
    public boolean getmooreNeighborhood(){	return mooreNeighborhood;}
    public void setmooreNeighborhood(boolean h){ mooreNeighborhood = h; }
  //Walls
    public int getwallType(){	return wallType;}
    public void setwallType(int b){ 
    	wallType = b;
    	switch(b) {
    	case NO_WALL: wallType = NO_WALL; break;
    	case PLUS_WALL: wallType = PLUS_WALL; break;
    	case BARRIER: wallType = BARRIER; break;
    	default: wallType = NO_WALL;
    	}
    }
    public Object domwallType() 
    { 
    return new String[] { "No Wall", "Plus Wall", "Barrier"};
    }
    public boolean getstopWhenHomogeneous(){	return stopWhenHomogeneous;}
	public void setstopWhenHomogeneous(boolean h){ stopWhenHomogeneous = h; }	
	
	public void initAgent(Agent a){
		a.energy = random.nextInt((int)startResource);//following Aktipis (2004)
	 	a.walk = false;
	 	a.played = false;
	 	a.movementProbs = movementProbsSums;
   	   	a.orientationLookUp = orientationLookUp;
   	   	a.directionLookUp = directionLookUp;
   	   	a.randomMovement = randomMovement;  
   	   	a.random = this.random; //give each agent direct access to the random number generator
	}
	
	
	public void createGrids(){
		agentsSpace = new ObjectGrid2D(gridWidth, gridHeight); 
		Wall w = new Wall(gridWidth, gridHeight);
		switch(wallType){
		case NO_WALL:
			wallGrid = new IntGrid2D(gridWidth, gridHeight, 0); break;
		case PLUS_WALL:
			final int length = (int)(.85*gridWidth);
			final int breadth = 6;
			wallGrid = w.makeWall(length, breadth);
			break;
		case BARRIER:
			wallGrid = w.makeBarrier();
			break;
		default: wallGrid = new IntGrid2D(gridWidth, gridHeight, 0); break;
		}
	}
	
	
	public void placeAgents(){
		long n = 0;
		int m = 0;
		int[][] popSum = new int[3][2];
	    for(int j=0; j<population.length;j++)
	    	for(int k=0;k<population[0].length;k++){
	    		n+=population[j][k];
	    		popSum[j][k]+=population[j][k] + m;
	    		m =popSum[j][k];
	    	}
	   int i = 0;
	   while(i < n){
	    	int x = random.nextInt(gridWidth - 1) + 1; 
	   	 	int y = random.nextInt(gridHeight - 1) + 1; 
	   	 	if (locationOK(x, y)){	 
	   	 		Int2D dir = null;
	   	 		if(mooreNeighborhood)
	   	 			dir = randomMovement[random.nextInt(8)];
	   	 		else
	   	 			dir = randomMovement[0];//make all face "north"
	   	 		Agent a = new Agent(dir.x, dir.y); 
	   	 		a.recycledAgents = recycledAgents;
	   	 		initAgent(a);
	   	 		a.orientation = a.lookUpOrientation(dir.x,dir.y);
	   	 		setStrategy(a, popSum, i);//
	   	 		agentsSpace.set(x, y, a);
	   	 		a.loc = new Int2D(x, y);
	   	 		//agentsSpace.setObjectLocation(a, x,y);
	   	 		a.event = schedule.scheduleRepeating(a);
	   	 		allAgents.add(a);
	   	 		i++;
	   	 	}
	    }
		
	}
	
	
	public boolean locationOK(int x, int y){
		if(wallType != NO_WALL && wallGrid.get(x, y) > 0)//don't place on a wall
			return false;
		if(occupySameSpace)
			return true;
		else{
		//Bag bag = agentsSpace.getObjectsAtLocation(x, y);
		Object obj = agentsSpace.get(x,y);
		if(obj == null)
			return true;
		else
			return false;
		}
	}
	
	
	
	
	public void setStrategy(Agent a, int[][] popSum, int i){
		boolean test = false;
	    for(int j=0; j<population.length;j++){
	        for(int k=0;k<population[0].length;k++)
	              if(i<popSum[j][k]){
	                  a.type = new Int2D(j,k);
	                  a.walk = false;
	                  if(j==COOPERATOR)
	                      a.mixed_p = 1.0;
	                  else if(j ==DEFECTOR)
	                      a.mixed_p = 0.0;
	                  else {
	                	  if(mixedUniformRandom)
	                		  a.mixed_p = this.random.nextDouble(); //set mixed to a random number
	                	  else
	                		  a.mixed_p = mixed_p;
	                  }
	              test = true;
	              break; //stop the for k loop
	              }
	          if(test)
	          break; //stop the for j loop
	      }	
	}
	
	public void initProbMovement(){
		Probabilities probs = new Probabilities();
        randomMovement = probs.makeRandomMovement();
        movementProbsSums = probs.makeMovementProbsSum(movementProbs);
        directionLookUp = probs.makeDirectionLookUp();
        orientationLookUp = probs.makeOrientationLookUp();
	}
	
	public void initMovementStrategy(){
		if(!mooreNeighborhood)
			movementProbs = VON_NEUMANN;
		else{
			final int b = moveProb;
			switch(b) {
				case 0: movementProbs = ZIGZAG; break;
				case 1: movementProbs = SPEEDSTER; break;
				case 2: movementProbs = BROWNIAN; break;
				default: movementProbs = ZIGZAG;
			}
		}
	}

	public void initPopPayoff(){
		population[COOPERATOR][NOT_WALK_AWAY] = dove_n;
		population[COOPERATOR][WALK_AWAY] = wdove_n;
		population[DEFECTOR][NOT_WALK_AWAY] = hawk_n;
		population[DEFECTOR][WALK_AWAY] = whawk_n;
		population[MIXED][NOT_WALK_AWAY] = mixed_n;
		population[MIXED][WALK_AWAY] = wmixed_n;
		payoffs[COOPERATE][COOPERATE] = dove_pay_vs_dove;
		payoffs[COOPERATE][DEFECT] = dove_pay_vs_hawk;
		payoffs[DEFECT][COOPERATE] = hawk_pay_vs_dove;
		payoffs[DEFECT][DEFECT] = hawk_pay_vs_hawk;
	}
	
	/**
	 * added
	 */

	/* Methods below */

	public Int2D uniformRandomLocation(){
		int x = random.nextInt(gridWidth); // a random number < //gridWidth
		int y = random.nextInt(gridHeight); //a random number <//gridHeight
		//Bag b = agentsSpace.getObjectsAtLocation(x, y);
		Object obj = agentsSpace.get(x,y);
		while( obj != null){ // make sure not to place an agent in a wall or obstacle 
			x = random.nextInt(gridWidth); // a random number < //gridWidth
			y = random.nextInt(gridHeight); //a random number <//gridHeight
			//b = agentsSpace.getObjectsAtLocation(x, y);
			obj = agentsSpace.get(x,y);
		}
		return new Int2D(x,y);		
	}

	public Int2D newLocation(){ //NOTE: need to allow for toroidal reproduction
		int	x = random.nextInt(gridWidth); // a random number < //gridWidth
		int	y = random.nextInt(gridHeight); //a random number <//gridHeight
		return new Int2D(x,y);
	}

	public AgentsSimulation(long seed) {
	    super(seed);
	    }
	
	/*************** Start Method ********************************************/
	public void start(){
		super.start(); // reuse the SimState start method
		// Now add our own code
		simCount++;//added, probably unnecessary --PS 1/13/10
		checkReleaseAutomatedControl();
		sc = checkAutomatedControl(); // allow the observer and controller to interact
		checkReleaseAutomatedControl();
		recycledAgents.clear();
		
		allAgents.clear();
		initPopPayoff();
	    createGrids();
	  //  initMovementStrategy();
	    movementProbs = BROWNIAN; 
	    initProbMovement();
	    ob = new Observer(this,ob, dataFileName , dataFolderName , saveDataToFile,savePNGToFile, sc,precision);
	    placeAgents();
	    //System.out.println("There should be " + allAgents.numObjs + " agents.");
	}

	public static void main(String[] args) {
		doLoop(AgentsSimulation.class, args); //doLoop is a static method 
		//already defined in SimState
		System.exit(0);//Stop the program when finished.
	}


}