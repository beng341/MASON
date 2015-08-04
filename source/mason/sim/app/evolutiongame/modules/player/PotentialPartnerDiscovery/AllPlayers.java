package sim.app.evolutiongame.modules.player.PotentialPartnerDiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;

/**
 * A basic method of creating a list of potential partners to play with. Simply
 * allow all possible players as possible partners. This should not be used in
 * practice as it may allow a player to play against itself or to play against
 * someone that has already played this time step.
 * @author Ben Armstrong
 */
public class AllPlayers extends PotentialPartnerDiscovery {
    
    @Override
    public void run(Population state, Player p) {
        this.p = p;
        HashMap<String, Object> results = new HashMap<>();
        results.put("potential_opponents", state.getPlayers());
        saveResults(results);
    }
}
