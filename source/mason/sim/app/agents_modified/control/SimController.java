package agents.control;

import java.io.File;

import agents.Observer;
import agents.initialize.SetParameters;
import agents.io.*;
import sim.display.Console;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.util.Bag;

/**
 * 
 * @author Jeff Schank
 *
 *This package contains classes that enable a controller to run and save
 *data on a series of simulations.  This will allow the user to set up a
 *series of simulations, walk away and let the computer finish them however
 *lone it takes.  The controller starts with script file that identifies the
 *runs to be executed.  Each run specifies how many simulations are to be
 *run and the parameter values for the simulations.  It also specifies the
 *file name the simulation results are stored in.  If the same file name is
 *given to one or more runs, the results are appended to that file.
 */
public class SimController {
	public Console c = null;
	public Display2D display = null;
	public GUIState guis = null;
	public SimState state;
	public String simulationFolder	= "";  			//folder that contains all of the files for a simulation
	public String parametersFile = "";   //file with simulation parameters
	public String dataFilePrefix;				// prepended to the number of the run
	public String dataFileFileType;					// file type saved	
	public String picFilePrefix;				// prepended to the number of the run
	public String picFileFileType;					// file type saved
	public String autoscript = "script.txt";
	public String dataFileName = "data.txt";
	public String dataFolderName = "data";
	public String projectName ="";
	public int runCount = 0;
	public int numberOfruns;
	public int numberOfSimulations; 	// The number of Simulations
	public int simulationCount = 0;
	public long  lengthOfSimulations; 	// The number of time steps in a Simulation		
	public long lengthOfSimulationsWithoutMutation; // number of simulations to be run after mutation
	public Bag fileNamesOfSimulations ;	// a bag of all the files for simulations to run
	public Bag originalParameters = null;
	public Bag simulatinVariableNames;
	public Bag simulationVariableValues;
	public Bag parameters;				//new parmeters
	public boolean shouldRepeatOriginal;
	public long shouldPauseOriginal;
	public long shouldPauseEnd;
	public int printSteps;
	public boolean stop = false;
	public boolean saveDataToFile = false;
	public boolean printToConsol = true;
	public boolean autoQuit = false;
	public boolean printDataRows = false;
	public boolean doGraphics = false;
	public boolean dataToSingleFile = false;

	public SimController(Console c, Display2D d, GUIState guis, SimState state, String autoscript, String projectName){
		this.c = c;
		this.guis = guis;
		this.state = state;
		this.display = d;
		this.autoscript = autoscript;
		this.projectName =projectName;

	}

	
	public void loadScript(){	
		Bag data = LoadScript.load(autoscript); //Load the script
		//LoadScript.print2Bags(data);
		Bag b1 = (Bag)data.objs[0];
		Bag b2 =(Bag)data.objs[1];
		for(int i=0;i<b1.numObjs;i++ )
			SetParameters.set(this, (String)b1.objs[i], b2.objs[i]);
		for(int i=0;i<b1.numObjs;i++ )
			SetParameters.set(state, (String)b1.objs[i], b2.objs[i]);
		if(c == null)
			System.out.println("c is null");
		c.setShouldRepeat(true);
		c.setWhenShouldEnd(lengthOfSimulations+lengthOfSimulationsWithoutMutation); //changed 8-24-09
		loadFixedSimulationParameters();
	}
	
	public void loadFixedSimulationParameters(){
		String fn = simulationFolder + File.separator + parametersFile;
		parameters = LoadSimulationParameters.loadParameters(fn);
		Bag b1 = (Bag)parameters.objs[0];
		Bag b2 =(Bag)parameters.objs[1];
		//for(int i=0; i<b1.numObjs;i++)
		//	System.out.println(b1.objs[i] + "  " +b2.objs[i]);
		
		this.simulatinVariableNames =(Bag)parameters.objs[2]; //variable names
		this.simulationVariableValues = (Bag)parameters.objs[3];// all the values for the simulations
		numberOfruns =  simulationVariableValues.numObjs;
		saveOriginalParameters(); // save original parameters	
			
		//LoadSimulationParameters.print2Bags(b1, b2);
		for(int i=0;i<b1.numObjs;i++ )
			SetParameters.set(state, (String)b1.objs[i], b2.objs[i]);  //load parameters that
			// will not change into the simulation
	}
	
	public void loadNewSimulationParameters(){
		Bag b1 = simulatinVariableNames;
		Bag b2 =(Bag)simulationVariableValues.objs[runCount];
		if(dataToSingleFile){
			Bag first = (Bag)simulationVariableValues.objs[0];
			Bag last =(Bag)simulationVariableValues.objs[numberOfruns-1];
			dataFileName = dataFilePrefix + ((Double)(first.objs[0])).intValue() +"-"+((Double)(last.objs[0])).intValue()+ "." +  dataFileFileType; //first object is "run" and run #
		}
		else
			dataFileName = dataFilePrefix + ((Double)(b2.objs[0])).intValue() + "." +  dataFileFileType; //first object is "run" and run #
		SetParameters.set(state,"dataFileName",dataFileName); //create the appropriate file name
		for(int i=1;i<b1.numObjs;i++ ) //skip the first objects
			SetParameters.set(state, (String)b1.objs[i], b2.objs[i]);  //load parameters that
			// will change into the simulation
	}

	public void saveOriginalParameters(){
		Bag b1 = (Bag)parameters.objs[0]; 
		Bag b2 = new Bag();
		for(int i=0;i<b1.numObjs;i++ )
			b2.add(SetParameters.getObject(state, (String)b1.objs[i])); //add the object
		originalParameters = new Bag();
		originalParameters.add(b1);
		originalParameters.add(b2);
		/*  get the oberver parameters */
		Object o = SetParameters.getObject(state, "dataFileName");
		dataFileName = (String)o;
		o = SetParameters.getObject(state, "dataFolderName");
		dataFolderName = (String)o;
		o = SetParameters.getObject(state, "printSteps");
		printSteps = (Integer)o;

		shouldRepeatOriginal = c.getShouldRepeat();
		shouldPauseOriginal = c.getWhenShouldPause();
		shouldPauseEnd = c.getWhenShouldEnd();
	}

	public void resestoreOriginalParameters(){
		Bag b1 = (Bag)originalParameters.objs[0];
		Bag b2 =(Bag)originalParameters.objs[1];
		for(int i=0;i<b1.numObjs;i++ )
			SetParameters.set2(state, (String)b1.objs[i], b2.objs[i]);
		c.setShouldRepeat(false);
		c.setWhenShouldPause(shouldPauseOriginal);
		c.setWhenShouldEnd(shouldPauseEnd);
		/*  get the oberver parameters */
		SetParameters.setString(state, "dataFileName",dataFileName);
		SetParameters.setString(state, "dataFolderName",dataFolderName);
		SetParameters.setInt(state, "printSteps",printSteps);
	}

	public void takeControl(){
		c.setShouldRepeat(true);
		c.setWhenShouldEnd(lengthOfSimulations);
		//System.out.println("lengthOfSimulations " + lengthOfSimulations);
	}

	public void releaseControl(){
		guis.finish();
		if(autoQuit)
			c.doQuit();
		c.setShouldRepeat(false);
		c.setWhenShouldEnd(Long.MAX_VALUE);
		resestoreOriginalParameters();

	}

}
