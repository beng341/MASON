package sim.app.evolutiongame.modules.environment.PlacePlayers;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;

/**
 * Places the players from the given population onto the environment in random
 * positions. Multiple players may be placed in the same location.
 * @author Ben Armstrong
 */
public class RandomOverlapping extends EnvironmentModule{

    @Override
    public void run(Population pop, Environment env)
    {
        for(int i = 0; i < pop.getPlayers().size(); ++i){
            env.grid.addObjectToLocation(
                    pop.getPlayers().get(i), pop.random.nextInt(env.grid.getWidth()), env.grid.getHeight());
        }
    }

}
