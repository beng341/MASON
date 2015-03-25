package hawkdove_smaldino; 

import sim.engine.*;
import sim.util.*;


public class Data {

	//public String[] fileheaders ={"time", "N", "Coop","Defect","WCoop","WDefect","simulations"};
	public String[] fileheaders ={"run", "sim", "time", "N", "Dove","Hawk","WCoop","WDefect"};
	public Bag data = null; 
	public int runCount = 0;
	public int simCount = 0;

	/* computational variance formula 

    	var = Sum(x^2)/n -(Sum(x)/2)^2
	 */


	public  Bag getData(final SimState state){
		final AgentsSimulation as = (AgentsSimulation)state;  // gets the state of the simulation
		double c=0,d=0,wc=0,wd=0;
		data = new Bag(); 
		data.add((double)(runCount+1)); //run count
		data.add((double)simCount);		//sim count
		
		data.add((double)((AgentsSimulation)state).schedule.getSteps());
		//Bag agents = as.agentsSpace.getAllObjects();
		Bag agents = as.allAgents;
		for(int i=0; i<agents.numObjs;i++){
			Agent a = (Agent)agents.objs[i];
			if(a.type.x == AgentsSimulation.COOPERATOR && a.type.y ==AgentsSimulation.NOT_WALK_AWAY)
				c++;
			else if(a.type.x == AgentsSimulation.COOPERATOR && a.type.y ==AgentsSimulation.WALK_AWAY)
				wc++;
			else if(a.type.x == AgentsSimulation.DEFECTOR && a.type.y ==AgentsSimulation.NOT_WALK_AWAY)
				d++;
			else if(a.type.x == AgentsSimulation.DEFECTOR && a.type.y ==AgentsSimulation.WALK_AWAY)
				wd++;
		}
		data.add((double)agents.numObjs);
		data.add(c);
		data.add(d);
		data.add(wc);
		data.add(wd);
		//data.add((double)as.simCount);//1.0); //for each simulation
		//for(int i=0;i<data.numObjs;i++)
			//System.out.println(data.objs[i]);
		return data;
	}

}







