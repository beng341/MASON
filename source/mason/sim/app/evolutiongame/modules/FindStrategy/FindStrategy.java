package sim.app.evolutiongame.modules.FindStrategy;

import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class FindStrategy extends Module{
    
    public static final String[] args = {"opponents", "payoff_matrix"};
    
    @Override
    public void run(Population state, Player p)
    {
        (new RandomStrategy()).run(state, p);
    }
 
    
}
