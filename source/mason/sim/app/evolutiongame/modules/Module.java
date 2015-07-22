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
 * This interface is not strictly necessary, its main goal is to suggest to 
 * other programmers the way in which this system is meant to be used. Modules
 * should implement this interface and the accompanying functionality so that
 * their new code is structured in at least a similar manner to the original
 * code.
 * @author armstrob
 */
public interface Module {
    /**
     * Finds all arguments used by this module and maps their name to their 
     * value. This module itself should be aware of which arguments it needs, if
     * any required argument is null, this method should return null signifying
     * that the module should not execute.
     * @return The arguments this module will use, or null if not all of the
     * arguments exist.
     */
    public HashMap<String, Object> getArguments();
    /**
     * 
     * @param state
     * @param p
     */
    public void run(Population state, Player p);
    /**
     * 
     * @param results 
     */
    public void saveResults(HashMap<String, Object> results);
}
