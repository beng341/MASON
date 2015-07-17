package sim.app.evolutiongame.modules.FindOpponents;

import java.util.ArrayList;
import java.util.Random;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;

/**
 *
 * @author Ben Armstrong
 */
public class FindOpponents {
    
    /**
     * 
     * @param state
     * @param p
     * @param args
     * @return An ArrayList<Player> containing a single player, randomly chosen
     * from the given list of potential opponents stored in args.
     */
    public static Object run(Population state, Player p, Object args) {
        ArrayList<Player> players = (ArrayList<Player>)args;
        if(players.isEmpty())
            return null;
        return players.get((new Random()).nextInt(players.size()));
    }
}
