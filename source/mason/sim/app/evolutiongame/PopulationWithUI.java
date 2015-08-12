package sim.app.evolutiongame;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import sim.app.evolutiongame.agents.Player;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.DenseGridPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

/**
 *
 * @author Ben Armstrong
 */
public class PopulationWithUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D fieldPortrayal = new ContinuousPortrayal2D();
    DenseGridPortrayal2D gridPortrayal = new DenseGridPortrayal2D();
    
    
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
    @Override
    public void load(SimState state){
        super.load(state);
        setupPortrayals();
    }
    
    public void setupPortrayals(){
        Population pop = (Population)state;
        
        gridPortrayal.setField(pop.env.grid);
        pop.env.gridPortrayal = gridPortrayal;
        
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.blue);
        colors.add(Color.red);
        colors.add(Color.cyan);
        colors.add(Color.magenta);
        pop.env.colors = colors;
        
        //set player color, if strategy is out of bounds add new random colours.
        for(Player p: pop.getPlayers()){
            while((int)p.getVariable("strategy") >= colors.size())
            {
                colors.add(new Color(pop.random.nextFloat(), pop.random.nextFloat(), pop.random.nextFloat()));
            }
            Color c = colors.get((int)p.getVariable("strategy"));
            gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(c));
        }
        
        //fieldPortrayal.setField(pop.field);
        //fieldPortrayal.setPortrayalForAll(new OvalPortrayal2D());
        
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
        
        //display.attach(fieldPortrayal, "Field");
        display.attach(gridPortrayal, "Environment");
    }
    
    @Override
    public void quit(){
        super.quit();
        if(displayFrame != null)
            displayFrame.dispose();
        displayFrame = null;
        display = null;
    }
    
    @Override
    public Object getSimulationInspectedObject() {
        return state;
    }

    @Override
    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }
}
