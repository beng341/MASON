package sim.app.evolutiongame.modules.environment;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public abstract class EnvironmentModule extends Module{

    @Override
    public final void run(Population state, Player p){
        throw new UnsupportedOperationException("This method is intended for use"
                + "by Player Modules only.");
    }
    
    @Override
    public abstract void run(Population pop, Environment env);
    
    /**
     * Prevent Environment Modules from overriding this method, they should use the
     * setup(Population p, Environment env) method.
     * @param pop 
     */
    @Override
    public final void setup(Population pop, Player p){}
    @Override
    public void setup(Population pop, Environment env){}

}
