package sim.app.evolutiongame.modules.environment.MovePlayers;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;

/**
 *
 * @author Ben Armstrong
 */
public class TestModule extends EnvironmentModule
{

    @Override
    public void run(Population p, Environment env)
    {
        System.out.println("I'm testing!!!");
    }

}
