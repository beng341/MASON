package hawkdove_smaldino.tests;

import java.io.File;

import hawkdove_smaldino.io.LoadScript;
import hawkdove_smaldino.io.LoadSimulationParameters;

import sim.engine.SimState;
import sim.util.Bag;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fn = "simulation" + File.separator + "simulationParameters.txt";
		
		Bag b = LoadSimulationParameters.loadParameters(fn);
		System.out.println("Bag loaded" );
		LoadSimulationParameters.printPramaters(b);
		//Bag data = LoadScript.load("script.txt"); //Load the script
		//LoadScript.print2Bags(data);
	}

}
