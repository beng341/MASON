package sim.app.evolutiongame.modules.FindOpponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class FindOpponents implements Module {
    Player player;
    /**
     * Finds and saves an ArrayList<Player> containing a single player, randomly
     * chosen from the given list of potential opponents.
     * @param state
     * @param p
     */
    @Override
    public void run(Population state, Player p) {
        this.player = p;
        //1 - get arguments
        HashMap<String, Object> arguments = getArguments();
        if(null == arguments) {
            return;
        }
        ArrayList<Player> players = (ArrayList<Player>)arguments.get("potential_opponents");
        
        //2 - do what the module actually does
        Player result;
        if(players.isEmpty())
            result = null;
        else
            result = players.get(state.random.nextInt(players.size()));
        
        //3 - save the results
        HashMap<String, Object> results = new HashMap<>();
        results.put("opponent", result);
        saveResults(results);
    }

    @Override
    public HashMap<String, Object> getArguments() {
        return null;
    }

    @Override
    public void saveResults(HashMap<String, Object> results) {
        for(String name: results.keySet()){
            player.storeVariable(name, results.get(name));
        }
    }
}
