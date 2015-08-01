/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.evolutiongame.modules;

import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;

/**
 * This class is not strictly necessary, its main goal is to suggest to 
 * other programmers the way in which this system is meant to be used. Modules
 * should implement this interface and the accompanying functionality so that
 * their new code is structured in at least a similar manner to the original
 * code.
 * @author armstrob
 */
public abstract class Module {
    public Player p;
    
    /**
     * Finds all arguments used by this module and maps their name to their 
     * value. This module itself should be aware of which arguments it needs, if
     * any required argument is null, this method should return null signifying
     * that the module should not execute.
     * @return The arguments this module will use, or null if not all of the
     * arguments exist.
     */
    public HashMap<String, Object> getArguments(String[] arguments){
        HashMap<String, Object> args = new HashMap<>();
        for(String arg: arguments) {
            args.put(arg, p.getVariable(arg));
        }
        return args;
    }
    /**
     * Perform the actual action that the module is meant to do. Should call
     * getArguments() at the beginning and saveResults() at the end, as necessary.
     * @param state
     * @param p
     */
    public abstract void run(Population state, Player p);
    /**
     * Save each element in the given map's value list as its key in the list of
     * variables held in Player.
     * @param results 
     */
    public void saveResults(HashMap<String, Object> results){
        for(String name: results.keySet()){
            p.storeVariable(name, results.get(name));
        }
    }
}
