package sim.app.evolutiongame.modules.PotentialPartnerDiscovery;

import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class PotentialPartnerDiscovery extends Module {
    
    /**
     * A list of arguments required for this module to be run. If the player 
     * wishing to run this module does not have variables with these names, the
     * module will not be run.
     */
    public static final String[] args = {};
    
    @Override
    public void run(Population state, Player p)
    {
        (new AllPlayers()).run(state, p);
    }

}