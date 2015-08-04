package sim.app.evolutiongame.agents;

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
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DenseGrid2D;

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
    private DenseGrid2D grid;
    public Environment(Population pop){
        variables = new HashMap<>();
        grid = new DenseGrid2D((int)pop.getGridWidth(), (int)pop.getGridHeight());
        placePlayersRandomly(pop);
    }
    
    @Override
    public void step(SimState state)
    {
        Population pop = (Population)state;
        
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: pop.environmentModules.entrySet()){
            this.runModule(entry.getValue(), pop);
        }

    }
    
    /**
     * Places all players in the population randomly on the grid with no two
     * players on the same grid.
     * @param pop 
     */
    private void placePlayersRandomly(Population pop){
        //construct array to hold all players and map to grid squares
        Player[] gridMapping = new Player[this.grid.getHeight() * this.grid.getWidth()];
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

    /**
     * Used to run a specific module and set the result as per normal. An example
     * usage might be to run a module that will find the strategy a player should
     * use and sets the strategy as the module's result.
     * @param module
     * @param pop
     * @return true if all variables are found and module is run, false otherwise
     */
    public boolean runModule(Util.Pair<Module, Method> module, Population pop){
        boolean runModule = true;
        
        //check for existence of all required variables
        for(String name: pop.requiredEnvironmentVariables.get(module.getFirst())){
            if(!this.hasVariable(name)){
                runModule = false;
                break;
            }
        }
        
        if(runModule) {
            Method currentMethod = module.getSecond();
            this.invokeMethod(currentMethod, module.getFirst(), pop);
        }
        return runModule;
    }
    
    public Object invokeMethod(Method m, Module module, Population pop) {
        try {
            return m.invoke(module, pop);
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
}
