package hawkdove_smaldino;


import hawkdove_smaldino.control.SimController;
import hawkdove_smaldino.stats.SummaryStats1;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;


public class Observer implements Steppable {
	public final int LARGE_ORDER = Integer.MAX_VALUE; // maximum value of an integer
	public final double INTERVAL = 1.0;
	public HandleData hd = null;
	public Data data = null;
	public String fileName = "data.txt";
	public String folderName = "data";
	public boolean saveDataToFile = false;
	public boolean savePNGToFile = false;
	public Stoppable stop;
	int[][] test = null;
	public SimController sc = null;
	//public SummaryStats1 stats1 = null;
	//public SummaryStats1 stats2 = null;

	public HandleData hdParameters = null; //use a separate file to record parameters.
	public HandleData hdAbbrev = null; //this will take just the last line of data.
	public long lengthOfSimulations = 1000000; 
	
	public int count1 = 0;
	public int count2 = 0;
	SimState state;
	
	public double lsN;
	public double lsT;
	public double lsTsquared;
	public double played;
	public double totalPlayed;
	public double totalParents;
	public double reproduced;
	public double totalReproduced;
	public double sumOffspring;
	public double sumOfSQOffspring;
	public double sumOf_p;
	public double sumfOfSQ_p;
	public double sumOfoffspringXp;
	public double varOffspring;
	public double var_p;

	
	public Observer(SimState state,Observer ob, String fileName, String folderName, 
			boolean saveDataToFile,boolean savePNGToFile, SimController sc, String precision){
		AgentsSimulation as = (AgentsSimulation)state;

		this.fileName = fileName;
		this.folderName = folderName;
		this.saveDataToFile= saveDataToFile;
		this.savePNGToFile = savePNGToFile;
		this.lengthOfSimulations = as.lengthOfSimulations;
		hd = new HandleData(state, folderName, fileName,saveDataToFile,savePNGToFile,sc,precision);
		//parameters file.
		String par = fileName.replace(".txt", "_parameters.txt");
		hdParameters = new HandleData(state, folderName, par, saveDataToFile, savePNGToFile,sc,precision);
		//For writing to an abbreviated data file.
		String abbrev = fileName.replace(".txt", "_abbrev.txt");
		hdAbbrev = new HandleData(state, folderName, abbrev, saveDataToFile, savePNGToFile,sc,precision);

		
		
		data = new Data();
		//test = new int[as.popRecord.length][as.popRecord[0].length];
		/*for(int i=0; i< as.population.length;i++)
			for(int j=0; j< as.population[i].length;j++)
				test[i][j]= as.population[i][j];*/
		this.sc = sc;

		if(as.newParameters){
			ob = null;
			as.newParameters = false;
		}
		if(ob != null){ //transmit data variable values
				
			lsN = ob.lsN;
			lsT = ob.lsT;
			lsTsquared =ob.lsTsquared;
			played = ob.played;
			totalPlayed = ob.totalPlayed;
			reproduced = ob.reproduced;
			totalReproduced = ob.totalReproduced;
			totalParents = ob.totalParents;
			sumOffspring = ob.sumOffspring;
			sumOfSQOffspring = ob.sumOfSQOffspring;
			sumOf_p = ob.sumOf_p;
			sumfOfSQ_p = ob.sumfOfSQ_p;
			sumOfoffspringXp = ob.sumOfoffspringXp;
			varOffspring = ob.varOffspring;
			var_p = ob.var_p;
		}
		else{
			lsN = 0;
			lsT = 0;
			lsTsquared =0;
			played = 0;
			totalPlayed = 0;
			reproduced = 0;
			totalReproduced = 0;
			totalParents = 0;
			sumOffspring = 0;
			sumOfSQOffspring = 0;
			sumOf_p = 0;
			sumfOfSQ_p = 0;
			sumOfoffspringXp = 0;
			varOffspring = 0;
			var_p = 0;
		}
		stop = state.schedule.scheduleRepeating(this,LARGE_ORDER,INTERVAL);
	}
	
	
	

	public Observer(SimState state){
		stop = state.schedule.scheduleRepeating(this,LARGE_ORDER,INTERVAL);
		// make the observe self scheduling
	}
	/*
	 * We need to synchronize the step for technical reasons since the next step
	 * could start before the observer is done.
	 */
	public synchronized void step (SimState state){
		final AgentsSimulation as = (AgentsSimulation)state;
		final ObjectGrid2D agentsSpace = as.agentsSpace;
		final Schedule schedule = state.schedule;
		long thestep = schedule.getSteps();
		final Bag agents = as.allAgents;//agentsSpace.getAllObjects(); // get all the agents
		final boolean printToConsol = as.printToConsol;
		final boolean printDataRows = as.printDataRows;
		
		if(agents.isEmpty() || (as.stopWhenHomogeneous && allOneType(as))) // stop if empty or if the population is all defectors or all cooperators.
			{
			doStop(as);
			stop.stop();
			}
		
		if(printToConsol && thestep == 0){ 
			if(sc != null && sc.simulationCount == 1){ //the simcount will have been incremented
				hd.printPramsToConsole(state);
				if(printDataRows)
					hd.printHeadersToConsole(data);
			}
			else {
				hd.printPramsToConsole(state);
				if(printDataRows)
					hd.printHeadersToConsole(data);
			}
		}

		/*if(saveDataToFile && thestep == 0){ 
			if(sc != null && sc.simulationCount ==1){ //the simcount will have been incremented
				hd.printPramsToFile(state);
				if(printDataRows)
					hd.printHeadersToFile(data);
			}
			else if(saveDataToFile && printDataRows){
				hd.printHeadersToFile(data);
			}
		}*/
		
		//Print initial headings to file. 
		if(saveDataToFile && thestep == 0){ 
			if(sc != null && sc.simulationCount ==1){ //the simcount will have been incremented
				//hdParameters
				String x = "Run: " + sc.runCount;
				hdParameters.write.writeStringln(x);
				hdParameters.printPramsToFile(state);
				if(as.simCount == 1){
					hd.printHeadersToFile2(data);
					hdAbbrev.printHeadersToFile2(data);//print headers to the abbreviated file.
				}
			}	
		}
		
		updatePrintSteps(as, thestep);
		final int printSteps = as.printSteps;
		
		//pictures are taken here
		if(savePNGToFile && snapPic(as, thestep)){
			String path;
			int simCount ;
			if(as.autoSimulation){
				path = "run" + (sc.runCount + 1); // make sure that a folder for the pics exists
				simCount = sc.simulationCount;
			}
			else{
				simCount = as.simCount;
				path = "run" + as.simCount; // make sure that a folder for the pics exists
			}
			String fn = as.picFilePrefix + simCount + "-" + thestep + "." +  as.picFileFileType;
			hd.takeSnapshot(path, fn);
		}
		

		if((double)thestep % (double)printSteps == 0 || endOfRun(as)){
			if(as.autoSimulation){
				data.runCount = sc.runCount;
				data.simCount = sc.simulationCount;
			}
			else{
				data.runCount = 0;
				data.simCount = as.simCount;	
			}
			
			data.getData(state);	//this is the number of each type of agent	
			final long lengthOfSimulations = as.lengthOfSimulations;
			if(printDataRows && printToConsol)
				hd.printDataToConsole(data);
			if(printDataRows && saveDataToFile)
				hd.printDataToFile(data);          
		}
		
		//if at the end, print the abbreviated data. 
		if(as.autoSimulation && (thestep == lengthOfSimulations - 1 || endOfRun(as))){
			data.runCount = sc.runCount;
			data.simCount = sc.simulationCount;
			data.getData(state);
			hdAbbrev.printDataToFile(data);
		}
		
		doReset(state);
	}

	public void doReset (final SimState state){
		final AgentsSimulation as = (AgentsSimulation)state;
		//final Bag bag = as.agentsSpace.getAllObjects();
		final Bag bag = as.allAgents;
		for(int i=0;i<bag.numObjs;i++){
			Agent a = (Agent)bag.objs[i];
			if(a.played)
				played++;
			totalPlayed ++;
			a.played = false;
		}
	}

	public void doStop (SimState state){
		AgentsSimulation as = (AgentsSimulation)state;               
		//Bag bag = as.agentsSpace.getAllObjects();
		Bag bag = as.allAgents;
		for(int i=0;i<bag.numObjs;i++){
				Agent a = (Agent)bag.get(i);
				a.event.stop();
		}
	}

	
	/**
	 * Update the printsteps as time goes on. For the first 
	 */
	public void updatePrintSteps(AgentsSimulation as, long thestep){
		if(as.gridWidth < 50){
			if(thestep < 201)
				as.printSteps = 10;
			else if(thestep < 1001)
				as.printSteps = 50;
			else if(thestep < 5001)
				as.printSteps = 100;
			else if(thestep < 30001)
				as.printSteps = 500;
			else if(thestep < 100001)
				as.printSteps = 1000;
			else
				as.printSteps = 2000;
		}
		else{
			//since graphing on a log scale is valuable, we should tailor 
			// the print steps to yield a smooth curve.
			final int maxPop = (int)as.maxAgents;
			if(thestep < 11)
				as.printSteps = 1;
			else if(thestep < 101)
				as.printSteps = 5;
			else if(thestep < 501)
				as.printSteps = 10;
			else if(thestep < 1001)
				as.printSteps = 50;
			//else if(maxPop < 10000 && thestep < 100001)
			//	as.printSteps = 100;
			//else if(maxPop < 10000 && thestep < 300001)
			//	as.printSteps = 200;
			else if(maxPop < 10001)
				as.printSteps = 100;
			else if(thestep < 30001)
				as.printSteps = 200;
			else if(thestep < 100001)
				as.printSteps = 500;
			else if(thestep < 500001)
				as.printSteps = 1000;
			else 
				as.printSteps = 2000;		
		}
	}
	
	
	public boolean endOfRun(AgentsSimulation as){
		int c = 0;
		int d = 0;
		//final ObjectGrid2D agentsSpace = as.agentsSpace;
		final Bag agents = as.allAgents; //agentsSpace.getAllObjects();
		for(int i = 0; i < agents.numObjs; i++){
			Agent a = (Agent)agents.get(i);
			if(a.type.x == AgentsSimulation.COOPERATOR)
				c++;
			else
				d++;
		}
		if(c == 0 || d == 0)
			return true;
		else return false;
	}
	
	
	/*
	 * Return true if should snap a pic
	 */
	public boolean snapPic(AgentsSimulation as, long thestep){
		final int gw = as.gridWidth;
		/*int c = 0;
		int d = 0;
		final SparseGrid2D agentsSpace = as.agentsSpace;
		final Bag agents = agentsSpace.getAllObjects();
		for(int i = 0; i < agents.numObjs; i++){
			Agent a = (Agent)agents.get(i);
			if(a.type.x == AgentsSimulation.COOPERATOR)
				c++;
			else
				d++;
		}
		final double cd = 100*(c/(c+d)); //percent cooperators
		if(c == 0 && d == 0)
			return true;
		
		if(gw == 25){ 
			if(thestep == 0 || thestep == 50 ||  thestep == 100 || thestep == 500 ||  thestep == 1000)
				return true;
			else if((cd < 66) && Math.IEEEremainder((double)thestep, (double)1000) == 0)
				return true;
			else return false;
		}
		
		if(gw == 50){
			if(thestep == 0 || thestep == 100 || thestep == 500 ||  thestep == 1000 || thestep == 5000 || 
					thestep == 10000)
				return true;
			else if((cd < 66) && Math.IEEEremainder((double)thestep, (double)10000) == 0)
				return true;
			else if(thestep == 50000 || thestep == 100000 || 
					Math.IEEEremainder((double)thestep, (double)500000) == 0)
				return true;
			else return false;
		}*/
		
		if(gw == 100){
			if(thestep == 0 || thestep == 100 || thestep == 200 || thestep == 300 || thestep == 400 ||
					thestep == 500 || thestep == 600 || thestep == 700 ||
					thestep == 1000 || thestep == 2000 || thestep == 3000 || thestep == 5000 || 
					thestep == 10000 || thestep == 20000 || thestep == 30000 || thestep == 50000 ||
					thestep == 100000 || thestep == 300000 || thestep == 1000000)
				return true;
			else return false;
			
		}
		return false;
	}
	
	
	
	/**
	 * Observer methods of agents.
	 */

	/*
	 * Returns true if the population is either all defectors or all cooperators.
	 * (Ignores walkaway status)
	 */
	public boolean allOneType(AgentsSimulation as){
		int c = 0;
		int d = 0;
		//final SparseGrid2D agentsSpace = as.agentsSpace;
		final Bag agents = as.allAgents; //agentsSpace.getAllObjects();
		for(int i = 0; i < agents.numObjs; i++){
			Agent a = (Agent)agents.get(i);
			if(a.type.x == AgentsSimulation.COOPERATOR)
				c++;
			else
				d++;
			if(c > 0 && d > 0)
				break;
		}
		if(c > 0 && d > 0)
			return false;
		else
			return true;
	}







}