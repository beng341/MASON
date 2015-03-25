package hawkdove_smaldino.states;

import hawkdove_smaldino.control.SimController;
import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import hawkdove_smaldino.stats.SummaryStats1;
import sim.display.*;
import sim.engine.SimState;
import java.text.DateFormat;
import java.util.Date;



public class SimStateWithSimController extends SimState {
	public GUIState guis = null;
	public Console c = null;
	public Display2D display = null;
	public SimController sc= null;
	public boolean autoSimulation = false;
	public String script = "script.txt";
	//public SummaryStats1 stats1 = null; //for stats with mutation
	//public SummaryStats1 stats2 = null; //for stats without mutation
	long start=0, stop=0, elapsed;
	public String precision = "%.4f";
	public int precisionNum = 4;
	public boolean doMutation = true;
	public boolean newParameters = false;
	

	
	public SimStateWithSimController(MersenneTwisterFast random,
			Schedule schedule) {
		super(random, schedule);
		// TODO Auto-generated constructor stub
	}

	public SimStateWithSimController(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}

	public SimStateWithSimController(MersenneTwisterFast random) {
		super(random);
		// TODO Auto-generated constructor stub
	}
	
	public void setGUIState (GUIState ex){
		guis = ex;
	}
	
	public void setConsole (Console c){
		this.c = c;
	}
	
	public void setDisplay (Display2D display){
		this.display = display;
	}
	
	
	
	public int getprecision(){	return precisionNum;}
    public void setprecision(int i){ 
    	precisionNum = i;
    	switch(i) {
    	case 0: precision = "%.0f"; break;
    	case 1: precision = "%.1f"; break;
    	case 2: precision = "%.2f"; break;
    	case 3: precision = "%.3f"; break;
    	case 4: precision = "%.4f"; break;
    	case 5: precision = "%.5f"; break;
    	case 6: precision = "%.6f"; break;
    	case 7: precision = ""; break;// empty string is maximum
    	default: precision = "%.4f"; 
    	}
    }
    public Object domprecision() 
    { 
    return new String[] { "%.0f", "%.1f","%.2f", "%.3f","%.4f","%.5f","%.6f","Maximum" };
    }
	
    
	public SimController checkAutomatedControl(){
		if(autoSimulation){
			if(sc ==null){
				System.out.print("Auto Simulation Starting:  ");
				 Date now = new Date();
				 System.out.println(DateFormat.getInstance().format(now));
				sc = new  SimController(c,display,guis,this,script,"simulation");
				sc.loadScript();
				sc.loadNewSimulationParameters();
				sc.simulationCount++;
				start = System.currentTimeMillis();
			}
			else if(sc.simulationCount < sc.numberOfSimulations){
				sc.simulationCount++;
				doMutation = true;
			}
			else if(sc.runCount < sc.numberOfruns){
				sc.runCount++;
				sc.simulationCount = 1;
				doMutation = true;
				
		    	stop = System.currentTimeMillis();
				double freq = (stop - start) * .001;
		    	System.out.print("Run #: " + sc.runCount);
		    	System.out.println(" Time (sec): " + freq);
		    	start = System.currentTimeMillis();
		    	
				if(sc.runCount < sc.numberOfruns){ // check again					
			/*		stats1.doStats(sc.numberOfSimulations);
					stats2.doStats(sc.numberOfSimulations);
					if(sc.printToConsol){
						stats1.printTablesToConsol("Summary Statistics for Mutation ON", false);
						stats2.printTablesToConsol("Summary Statistics for Mutation OFF", true);
					}
			//		if(sc.saveDataToFile){
			//			stats1.printTablesToFile("Summary Statistics for Mutation ON",false);
			//			stats2.printTablesToFile("Summary Statistics for Mutation OFF",true);
			//		}
					stats1 = null;
					stats2 = null;*/
					newParameters = true;
					sc.loadNewSimulationParameters();
				}
				else {
				/*	stats1.doStats(sc.numberOfSimulations);
					stats2.doStats(sc.numberOfSimulations);
					if(sc.printToConsol){
						stats1.printTablesToConsol("Summary Statistics for Mutation ON", false);
						stats2.printTablesToConsol("Summary Statistics for Mutation OFF",true);
					}
		//			if(sc.saveDataToFile){
		//				stats1.printTablesToFile("Summary Statistics for Mutation ON", false);
		//				stats2.printTablesToFile("Summary Statistics for Mutation OFF",true);
		//			}
					stats1 = null;
					stats2 = null;*/
					sc.stop = true;
					autoSimulation = false;
					System.out.print("Auto Simulation Ends:  ");
					Date now = new Date();
					System.out.println(DateFormat.getInstance().format(now));

				}
			}
			else
				sc.stop = true;
		}
		return sc;
	}
	
	
	public void checkReleaseAutomatedControl(){
		if(!autoSimulation)  {
			if(sc != null){
				sc.releaseControl();
				sc = null; //let garbage collection remove the SimControler
			}
		}
		if(sc != null && (sc.stop || !(sc.runCount < sc.numberOfruns))){
			sc.releaseControl();
			sc = null; //let garbage collection remove the SimControler
			autoSimulation = false;
		}
	}
	

}
