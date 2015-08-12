package agents.states;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;


public class SimStateWithSaveData extends SimState {
	public String dataFileName = "data.txt";
	public String dataFolderName = "data";
	public boolean saveDataToFile = false;
	public int printSteps =  100;

	
	public SimStateWithSaveData(MersenneTwisterFast random,
			Schedule schedule) {
		super(random, schedule);
		// TODO Auto-generated constructor stub
	}

	public SimStateWithSaveData(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}

	public SimStateWithSaveData(MersenneTwisterFast random) {
		super(random);
		// TODO Auto-generated constructor stub
	}
	
	public int getprintSteps(){
		return printSteps;
	}

	public void setprintSteps(int x){
		if(x > 0)
			printSteps = x;
	}
	
	public String getdataFileName(){
		return dataFileName;
	}

	public void setdataFileName(String s){
		if(s.length() > 0)
			dataFileName = s;
	}

	public String getdataFolderName(){
		return dataFolderName;
	}


	public void setdataFolderName(String s){
		if(s.length() > 0)
			dataFolderName = s;
	}

	public boolean getsaveDataToFile(){
		return saveDataToFile;
	}

	public void setsaveDataToFile(boolean b){
		saveDataToFile = b;
	}


}
