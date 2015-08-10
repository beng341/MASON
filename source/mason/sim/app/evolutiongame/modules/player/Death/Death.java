package sim.app.evolutiongame.modules.player.Death;

import java.util.HashMap;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 * Basic method of handling death. Each player has a given deathrate, if the 
 * random value is less than that rate the player dies.
 * @author Ben Armstrong
 */
public class Death extends PlayerModule{
    
    private int numberOfDeaths = 0;
    
    /**
     * A list of arguments required for this module to be run. If the player 
     * wishing to run this module does not have variables with these names, the
     * module will not be run.
     */
    public static final String[] args = {};
    
    @Override
    public void run(Population state, Player p)
    {
        if(state.random.nextDouble() <= (p.deathRate)){
            p.die();
            numberOfDeaths++;
        }
    }
    
    @Override
    public Object trackStatistics(){
        String result = "Number of Deaths: " + numberOfDeaths;
        numberOfDeaths = 0;
        return result;
    }
}
