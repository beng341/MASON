package sim.app.evolutiongame.modules.FindOpponents;

import java.util.ArrayList;
import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class FindOpponents extends Module {
    
    /**
     * A list of arguments required for this module to be run. If the player 
     * wishing to run this module does not have variables with these names, the
     * module will not be run.
     */
    public static final String[] args = {"potential_opponents"};
    
    /**
     * Finds and saves an ArrayList<Player> containing a single player, randomly
     * chosen from the given list of potential opponents.
     * @param state
     * @param p
     */
    @Override
    public void run(Population state, Player p) {
        this.p = p;
        //1 - get arguments
        HashMap<String, Object> arguments = getArguments(args);
        if(null == arguments) {
            return;
        }
        ArrayList<Player> players = (ArrayList<Player>)arguments.get("potential_opponents");
        
        //2 - do what the module actually does
        ArrayList<Player> result = new ArrayList<>();
        if(players.isEmpty())
            result = null;
        else
            result.add(players.get(state.random.nextInt(players.size())));
        
        //3 - save the results
        HashMap<String, Object> results = new HashMap<>();
        results.put("opponents", result);
        saveResults(results);
    }
}
