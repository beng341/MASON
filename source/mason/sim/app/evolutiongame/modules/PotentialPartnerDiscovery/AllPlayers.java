package sim.app.evolutiongame.modules.PotentialPartnerDiscovery;

import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;

/**
 * A basic method of creating a list of potential partners to play with. Simply
 * allow all possible players as possible partners. This should not be used in
 * practice as it may allow a player to play against itself or to play against
 * someone that has already played this time step.
 * @author Ben Armstrong
 */
public class AllPlayers {
    
    public static Object run(Population state, Player p, Object args) {
        return state.getPlayers();
    }
}
