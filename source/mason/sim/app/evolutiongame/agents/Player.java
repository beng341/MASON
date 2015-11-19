package sim.app.evolutiongame.agents;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;



/**
 * The base class for a player that plays a game. Games will be two-player so
 * each Player will always be matched up against another Player. The match ups
 * will be done by the GameRound at every step in the simulation.
 * @author Ben Armstrong
 */
public class Player implements Steppable
{
    private final int[][] payoffMatrix;
    /**
     * Contains the Population object controlling the simulation. Use this to 
     * access things like the schedule or random number generator.
     */
    private Population pop;
    
    private HashMap<String, Object> variables;
    
    /**
     * List of Modules this Player will do during it's step() method.
     */
    public LinkedHashMap<String, Util.Pair<Module, Method>> modules;
    
    /**
     * This is what is returned when the agent is put on the schedule.
     * Call stoppable.stop() to remove this agent from the schedule.
     */
    private Stoppable stoppable;
    public void setStoppable(Stoppable s){this.stoppable = s;}
    
    /**
     * The id that the next player should be assigned. This should be incremented
     * after each assignment. Unless there are over 2 billion different players
     * in any given run it should be fine to use an int rather than a long.
     */
    private static int id_count = 0;
    
    /**
     * The id of this player. No other player should have this id.
     */
    public int id;
    
    /**
     * 
     * @param payoff Matrix representing payoff functions. This player gets the 
     * payoff at p[i][j] if it plays strategy i and the other player does
     * strategy j.
     * @param pop
     */
    public Player(int[][] payoff, Population pop)
    {
        this.payoffMatrix = payoff;
        this.pop = pop;
        this.id = id_count++;
        this.variables = new HashMap<>();
        initializeConstantVariables();
    }
    public Player(Player parent, Population pop)
    {
        this.payoffMatrix = parent.payoffMatrix;
        this.pop = pop;
        this.id = id_count++;
        this.variables = new HashMap<>();
        if(parent.hasVariable("strategy"))
            this.storeVariable("strategy", parent.getVariable("strategy"));
        initializeConstantVariables();
    }

    /**
     * Store variables that either should never change throughout the life of 
     * this player, or that should always exist and be available regardless of if
     * they change or not.
     */
    private void initializeConstantVariables()
    {
        this.storeVariable("payoff_matrix", this.payoffMatrix);
        this.storeVariable("last_played", -1l);
    }
    
    /**
     * Remove this player from the population and from the schedule so that it 
     * never performs another action.
     */
    public void die() 
    {
        pop.removePlayer(this);
        stoppable.stop();
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
    
    public void removeVariable(String name){
        this.variables.remove(name);
    }
    
    /**
     * Invokes a method given by the user and catches any errors.
     * @param m
     * @return 
     */
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
     * Used to run the in use implementation of a module.
     * An example usage might be to run a module that will find the strategy a
     * player should use and sets the strategy as the module's result.
     * 
     * This is useful for limited communication between players. A player can 
     * tell another player to perform a specific action, and could theoretically
     * give new values to the input variables of the module to be run.
     * 
     * Note that all modules
     * 
     * @param moduleName The name of the module that the implementation to run 
     * extends. ie. Pass "PotentialPartnerDiscovery" to run the "AllPlayers"
     * implementation.
     * @return true if the module is run, false otherwise
     */
    public boolean runModuleOutOfTurn(String moduleName){
        
        if(pop.preferredModules.containsKey(moduleName)){
            this.invokeMethod(pop.preferredModules.get(moduleName).getSecond(), pop.preferredModules.get(moduleName).getFirst());
            return true;
        }
        return false;
    }
    
    /**
     * Used to run a specific module and set the result as per normal. An example
     * usage might be to run a module that will find the strategy a player should
     * use and sets the strategy as the module's result.
     * @param module
     * @return true if all variables are found and module is run, false otherwise
     */
    public boolean runModule(Util.Pair<Module, Method> module){
        boolean runModule = true;
            
        //check for existence of all required variables
        if(pop.requiredPlayerVariables.containsKey(module.getFirst())){
            for(String name: pop.requiredPlayerVariables.get(module.getFirst())){
                if(!this.hasVariable(name)){
                    runModule = false;
                    break;
                }
            }
        }
        
        if(runModule) {
//            try {
//                module.getFirst().run(pop, this);
//            } catch (UnsupportedOperationException ex) {}
            Method currentMethod = module.getSecond();
            this.invokeMethod(currentMethod, module.getFirst());
        }
        return runModule;
    }
    
    public void step(SimState state){
        
        pop = (Population)state;
        
        //Iterate over each module, in the order that is specified. Run each
        //module only after checking that the proper variables all exist.
        //Each module will take care by itself to fetch the arguments it needs
        //and to set the results it should.
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: modules.entrySet()){
            this.runModule(entry.getValue());
        }
//        for(Util.Pair<Module, Method> entry: this.modules){
//            this.runModule(entry);
//        }
//        
    }
    
    
    
    public String toString()
    {
        if(this.hasVariable("strategy")){
            return "Player " + this.id + " - Strategy: " + this.getVariable("strategy");
        }
        return "Player " + this.id;
    }
}
