package sim.app.evolutiongame.modules.PlayGame;

import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 * The PlayGame module represents the action of an agent playing a game with zero
 * or more of the other agents in the population.
 * 
 * This class represents the default behaviour of the PlayGame module. It will
 * play a game against one other player, and set the payoff for that player.
 * @author Ben Armstrong
 */
public class PlayGame extends Module {
    
    public static final String[] args = {"potential_opponents"};

    public static Object run(Population state, Player p, Object args) {
        
        return null;
    }

    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        this.arguments = new String[]{"potential_opponents"};
        //1 - get arguments
        HashMap<String, Object> args = getArguments();
        if(null == args) {
            return;
        }
    }
}