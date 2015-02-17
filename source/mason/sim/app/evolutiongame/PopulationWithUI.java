package sim.app.evolutiongame;

import java.awt.Color;
import javax.swing.JFrame;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 *
 * @author Ben Armstrong
 */
public class PopulationWithUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D fieldPortrayal = new ContinuousPortrayal2D();
    
    
    public PopulationWithUI(SimState state){
        super(state);
    }
    public PopulationWithUI(){
        super(new Population(System.currentTimeMillis()));
    }
    public static String getName(){
        return "Evolutionary Population Simulation";
    }
    
    public static void main(String[] args)
    {
        PopulationWithUI gui = new PopulationWithUI();
        Console c = new Console(gui);
        c.setVisible(true);
    }
    
    @Override
    public void start(){
        super.start();
        setupPortrayals();
    }
    public void load(){
        super.load(state);
        setupPortrayals();
    }
    
    public void setupPortrayals(){
        Population pop = (Population)state;
        
        fieldPortrayal.setField(pop);
        fieldPortrayal.setPortrayalForAll(new OvalPortrayal2D());
        
        display.reset();
        display.setBackdrop(Color.WHITE);
        
        display.repaint();
    }
    
    @Override
    public void init(Controller c){
        super.init(c);
        
        display = new Display2D(600, 600, this);
        display.setClipping(false);
        
        displayFrame = display.createFrame();
        displayFrame.setTitle("Evolutionary Simulation");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        
        display.attach(fieldPortrayal, "field");
    }
    
    @Override
    public void quit(){
        super.quit();
        if(displayFrame != null)
            displayFrame.dispose();
        displayFrame = null;
        display = null;
    }
    
    public Object getSimulationInspectedObject() {
        return state;
    }

    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }
}
