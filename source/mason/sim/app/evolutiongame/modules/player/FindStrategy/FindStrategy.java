package sim.app.evolutiongame.modules.player.FindStrategy;

import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class FindStrategy extends PlayerModule{
    
    public static final String[] args = {"opponents", "payoff_matrix"};
    
    @Override
    public void run(Population state, Player p)
    {
        (new RandomStrategy()).run(state, p);
    }
 
    
}
