package sim.app.evolutiongame.modules.player.PotentialPartnerDiscovery;

import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class PotentialPartnerDiscovery extends PlayerModule {
    
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