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
     * Finds and saves an ArrayList<Player> containing a single player, randomly
     * chosen from the given list of potential opponents.
     * @param state
     * @param p
     */
    @Override
    public void run(Population state, Player p) {
        this.p = p;
        this.arguments = new String[]{"potential_opponents"};
        //1 - get arguments
        HashMap<String, Object> args = getArguments();
        if(null == args) {
            return;
        }
        ArrayList<Player> players = (ArrayList<Player>)args.get("potential_opponents");
        
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
