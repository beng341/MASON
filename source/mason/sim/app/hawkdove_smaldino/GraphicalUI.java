package hawkdove_smaldino;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;

import java.awt.*;

import javax.swing.*;

import hawkdove_smaldino.states.SimStateWithSimController;
import sim.util.*;
/**
 * This class controls all of the GUI for the simulation. Run the main method of 
 * this class to run the simulation with a GUI. Nothing relevant to the actual
 * simulation is located in this class however.
 * -BA
 */
public class GraphicalUI extends GUIState{
	public Display2D display; 
    public JFrame displayFrame;
	static SimStateWithSimController ssc;
    ObjectGridPortrayal2D agentsPortrayal = new ObjectGridPortrayal2D();
    FastValueGridPortrayal2D wallPortrayal = new FastValueGridPortrayal2D("Wall");

    public void setupPortrayals() {
    	AgentsSimulation as = (AgentsSimulation)state;
    	if(as.doGraphics){
    		wallPortrayal.setMap(new sim.util.gui.SimpleColorMap(0, 1, Color.white, Color.black));


    		agentsPortrayal.setField(((AgentsSimulation)state).agentsSpace);        
    		//Bag bag = ((AgentsSimulation)state).agentsSpace.getAllObjects();
    		Bag bag = as.allAgents;
    		for(int i=0;i<bag.numObjs;i++){
    			Agent p = (Agent)bag.objs[i];
    			if(p.type.y == AgentsSimulation.NOT_WALK_AWAY)
    				agentsPortrayal.setPortrayalForObject(p,new sim.portrayal.simple.RectanglePortrayal2D(new Color((float)(1.0-p.mixed_p),(float)0.0,(float)p.mixed_p)));
    			else if(p.type.x == AgentsSimulation.DEFECTOR)
    				agentsPortrayal.setPortrayalForObject(p,new sim.portrayal.simple.RectanglePortrayal2D(Color.magenta));
    			else if(p.type.x == AgentsSimulation.COOPERATOR)
    				agentsPortrayal.setPortrayalForObject(p,new sim.portrayal.simple.RectanglePortrayal2D(Color.cyan));
    			else
    				agentsPortrayal.setPortrayalForObject(p,new sim.portrayal.simple.OvalPortrayal2D(new Color((float)(1.0-p.mixed_p),(float)0.0,(float)p.mixed_p)));  

    		}

    		wallPortrayal.setField(as.wallGrid);
    		as.agentsPortrayal = agentsPortrayal;  
    		display.reset(); 
    		display.repaint(); // call the repaint method
    	}
    }
    
    public void init(Controller c){
        super.init(c);  
        display = new Display2D(500,500,this,1); 
        displayFrame = display.createFrame(); 
        c.registerFrame(displayFrame);   
        displayFrame.setVisible(true);  
        display.setBackdrop(Color.white);
        if(((AgentsSimulation)state).doGraphics){
        	if(((AgentsSimulation)state).wallType != ((AgentsSimulation)state).NO_WALL)
        			display.attach(wallPortrayal, "Wall"); 
        	display.attach(agentsPortrayal,"Agents");
        }
        ssc.setDisplay(display); //pass to display
           }
    
    
    protected GraphicalUI() { // Constructor for this class
        super(new AgentsSimulation(System.currentTimeMillis()));
        ssc = (SimStateWithSimController)state;
                  // Create a simulation in it
 }
    public GraphicalUI(SimState state) { 
        super(state); // Pass the already created simulation to it
        }

    public static String getName() { 
        return "Simulation of the Hawk and Dove Game"; 
               // return a name for what this simulation is about
         }

    public void quit() {
         super.quit(); // Use the already defined quit method
            
         if (displayFrame!=null) displayFrame.dispose(); 
            displayFrame = null;  // when quiting get rid of the display
            display = null;       
    }

    public void start() {
          super.start(); // use the predefined start method
           setupPortrayals(); // add setupPortrayals method below
    }

        
    public void load(SimState state) {
        super.load(state); // load the simulation into the interface
        setupPortrayals(); // call setuuPortrayals
    }
    
    public Object getSimulationInspectedObject() {
        return state; // This returns the simulation
   }
    
    public static void main(String[] args) {
    	GraphicalUI ex = new GraphicalUI(); 
        Console c = new Console(ex);
        ssc.setGUIState(ex); //pass to SimState
		c.setVisible(true); // make the console visible
		ssc.setConsole(c); //Pass to Simstate
        System.out.println("Start Simulation"); 
        }
	
}//end class
