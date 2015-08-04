package sim.app.evolutiongame.modules.environment.MovePlayers;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class MovePlayers extends EnvironmentModule{
    
    public static final String[] args = {"test", "test_2"};

    @Override
    public void run(Population p)
    {
        System.out.println("The earth is moving!!!");
    }

}
