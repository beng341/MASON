package sim.app.evolutiongame;

import java.util.HashSet;
import sim.engine.SimState;

/**
 * Holds a number of Players that will play a game against each other and allow
 * us to observe the outcome of these games.
 * @author Ben Armstrong
 */
public class Population extends SimState
{
    private HashSet<Player> players;

    public Population(long seed)
    {
        super(seed);
        this.players = new HashSet<Player>();
    }
    
    
    public HashSet<Player> getPlayers()
    {
        return this.players;
    }
}
