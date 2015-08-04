package sim.app.evolutiongame.modules.player;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public abstract class PlayerModule extends Module{

    @Override
    public abstract void run(Population state, Player p);
    
    @Override
    public void run(Population p)
    {
        throw new UnsupportedOperationException("This method is intended for use"
                + "by Environment Modules only.");
    }
    
}
