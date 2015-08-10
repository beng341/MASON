package sim.app.evolutiongame.agents;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.Config;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DenseGrid2D;
import sim.portrayal.grid.DenseGridPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

/**
 * Environment object for the simulation. This is called once per time step and
 * told to run its modules.
 * I am assuming (for the time being at least) that the environment contains a 
 * grid that players are on. The grid will be represented by a DenseGrid2D object
 * that has its size set by the MASON GUI (and thus, is available through the
 * population object).
 * @author Ben Armstrong
 */
public class Environment implements Steppable{

    private HashMap<String, Object> variables;
    public DenseGrid2D grid;
    public DenseGridPortrayal2D gridPortrayal; //kind of breaks MVC but oh well...
    public ArrayList<Color> colors;
    private Population pop;
    public Environment(Population pop){
        this.pop = pop;
        this.variables = new HashMap<>();
        this.grid = new DenseGrid2D((int)pop.getGridWidth(), (int)pop.getGridHeight());
        placePlayersRandomly(pop);
    }
    
    @Override
    public void step(SimState state)
    {
        Population pop = (Population)state;
        
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: pop.environmentModules.entrySet()){
            this.runModule(entry.getValue());
        }

    }

    /**
     * Used to run a specific module and set the result as per normal. An example
     * usage might be to run a module that will find the strategy a player should
     * use and sets the strategy as the module's result.
     * @param module
     * @param pop
     * @return true if all variables are found and module is run, false otherwise
     */
    public boolean runModule(Util.Pair<Module, Method> module){
        boolean runModule = true;
        
        //check for existence of all required variables
        if(pop.requiredEnvironmentVariables.containsKey(module.getFirst())){
            for(String name: pop.requiredEnvironmentVariables.get(module.getFirst())){
                if(!this.hasVariable(name)){
                    runModule = false;
                    break;
                }
            }
        }
            
        
        if(runModule) {
            Method currentMethod = module.getSecond();
            this.invokeMethod(currentMethod, module.getFirst());
        }
        return runModule;
    }
    
    public Object invokeMethod(Method m, Module module) {
        try {
            return m.invoke(module, pop, this);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     * Gets the "variable" of the specified name from the global variable list 
     * in this Player's Population. The result should be cast to it's proper 
     * type.
     * This approach is not great for security but I suspect it is the
     * fastest and certainly is the most general approach I came up with.
     * @param name Name of the variable desired. This is prepended with the id 
     * of this player.
     * @return 
     */
    public Object getVariable(String name) {
        return this.variables.get(name);
    }
    
    public void storeVariable(String name, Object value) {
        this.variables.put(name, value);
    }
    
    public boolean hasVariable(String name) {
        return this.variables.containsKey(name);
    }

    public DenseGrid2D getGrid(){
        return this.grid;
    }
    
    /**
     * Adds a player to a random location in the field, allows the player to be
     * placed in the same location as another player.
     * @param p 
     * @return  
     */
    public Pair<Integer, Integer> addPlayerToGrid(Player p){
        int x = this.pop.random.nextInt(grid.getWidth());
        int y = this.pop.random.nextInt(grid.getHeight());
        grid.addObjectToLocation(p, x, y);
        return new Pair(x,y);
    }

    private void placePlayersRandomly(Population pop)
    {
        //construct array to hold all players and map to grid squares
        Player[] gridMapping = new Player[grid.getHeight() * grid.getWidth()];
        int i = 0;
        for(i = 0; i < pop.getPlayers().size(); ++i){
            gridMapping[i] = pop.getPlayers().get(i);
        }
        for(; i < gridMapping.length; ++i){
            gridMapping[i] = null;
        }
        
        //do Fisher-Yates shuffling algorithm
        int index;
        for (i = gridMapping.length - 1; i > 0; i--)
        {
            index = pop.random.nextInt(i + 1);
            if (index != i)
            {
                Player p = gridMapping[i];
                gridMapping[i] = gridMapping[index];
                gridMapping[index] = p;
            }
        }
        
        //place objects onto grid if they aren't null
        //grid(i,j)) = gridMapping[grid.length*i + j]       ?
        for(i = 0; i < gridMapping.length; ++i){
            if(gridMapping[i] != null){
                grid.addObjectToLocation(gridMapping[i], i/grid.getWidth(), i%grid.getWidth());
            }
        }
    }
    
    public void addPlayerToGridAtLocation(Player p, int x, int y){
        grid.addObjectToLocation(p, x, y);
        
        while((int)p.getVariable("strategy") >= colors.size())
        {
            colors.add(new Color(pop.random.nextFloat(), pop.random.nextFloat(), pop.random.nextFloat()));
        }
        Color c = colors.get((int)p.getVariable("strategy"));
        gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(c));
    }
}
