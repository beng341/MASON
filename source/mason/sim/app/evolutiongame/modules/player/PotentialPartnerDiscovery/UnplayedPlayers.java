package sim.app.evolutiongame.modules.player.PotentialPartnerDiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;

/**
 *
 * @author Ben Armstrong
 */
public class UnplayedPlayers extends PotentialPartnerDiscovery {
    
    HashMap<Integer, Player> unPlayed = null;
    
    
    @Override
    public void run(Population state, Player p) {
        this.p = p;
        
        //if unPlayed is null, initialize it to playerMap.keys
        //go through each unPlayedID, if that player has played, remove and add to playedIDs
        //set the rest of the unplayed Players as the result
        
        //initialize sets if not already done (happens at beginning of timestep)
        if(unPlayed == null){
            this.unPlayed = (HashMap<Integer, Player>) state.getPlayerMap().clone();
        } else {
            //remove each newly played player from the unplayed set, add to played.
            for (Iterator<Map.Entry<Integer, Player>> it = unPlayed.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry<Integer, Player> entry = it.next();
                if(state.getPlayer(entry.getKey()) != null
                        && ((Long)state.getPlayer(entry.getKey()).getVariable("last_played")) == state.schedule.getSteps()
                        && state.getPlayer(entry.getKey()).hasVariable("payoff")){
                    it.remove();
                }
            }
        }
        
        
        HashMap<String, Object> results = new HashMap<>();
        results.put("potential_opponents", new ArrayList<>(unPlayed.values()));
        saveResults(results);
    }
    
    @Override
    public void cleanUp(Population p){
        this.unPlayed = null;
    }
}