package sim.app.evolutiongame.modules.environment.RandomDeath;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class RandomDeath extends EnvironmentModule{
    
    @Override
    public void run(Population pop, Environment env)
    {
        System.out.println("I'm killing!!!");
    }

}
