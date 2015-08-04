package sim.app.evolutiongame.modules.environment;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public abstract class EnvironmentModule extends Module{

    @Override
    public void run(Population state, Player p){
        throw new UnsupportedOperationException("This method is intended for use"
                + "by Player Modules only.");
    }
    
    @Override
    public abstract void run(Population p);

}
