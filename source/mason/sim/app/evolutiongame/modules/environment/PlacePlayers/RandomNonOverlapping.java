package sim.app.evolutiongame.modules.environment.PlacePlayers;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;

/**
 * Places the players in the given population into random locations on the grid.
 * Ensures that no two players are placed on the same cell.
 * @author Ben Armstrong
 */
public class RandomNonOverlapping extends EnvironmentModule{

    @Override
    public void run(Population pop, Environment env)
    {
        //construct array to hold all players and map to grid squares
        Player[] gridMapping = new Player[env.grid.getHeight() * env.grid.getWidth()];
        int i = 0;
        for(i = 0; i < pop.getPlayers().size(); ++i){
            gridMapping[i] = pop.getPlayers().get(i);
        }
        for(; i < gridMapping.length; ++i){
            gridMapping[i] = null;
        }
        
        //do Fisher-Yates shuffling algorithm
        int index;
        for (i = gridMapping.length - 1; i > 0; i--)
        {
            index = pop.random.nextInt(i + 1);
            if (index != i)
            {
                Player p = gridMapping[i];
                gridMapping[i] = gridMapping[index];
                gridMapping[index] = p;
            }
        }
        
        //place objects onto grid if they aren't null
        //grid(i,j)) = gridMapping[grid.length*i + j]       ?
        for(i = 0; i < gridMapping.length; ++i){
            if(gridMapping[i] != null){
                env.grid.addObjectToLocation(gridMapping[i], i/env.grid.getWidth(), i%env.grid.getWidth());
            }
        }
    }

}
