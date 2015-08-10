package sim.app.evolutiongame.modules.player;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
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
    public final void run(Population p, Environment env)
    {
        throw new UnsupportedOperationException("This method is intended for use"
                + "by Environment Modules only.");
    }
    
    @Override
    public void setup(Population pop, Player p){}
    
    /**
     * Prevent Player Modules from overriding this method, they should use the
     * setup(Population p, Player p) method.
     * @param pop
     * @param env 
     */
    @Override
    public final void setup(Population pop, Environment env){}
}
