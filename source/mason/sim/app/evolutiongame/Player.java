package sim.app.evolutiongame;

/**
 * The base class for a player that plays a game. Games will be two-player so
 * each Player will always be matched up against another Player. The match ups
 * will be done by the GameRound at every step in the simulation.
 * @author Ben Armstrong
 */
public class Player
{
    private int[][] payoff;
    
    /**
     * The index of the strategy that this player will play. Initialized to -1 
     * so we can tell when it's not been set.
     */
    private int strategy = -1;
    
    /**
     * 
     * @param payoff Matrix representing payoff functions. This player gets the 
     * payoff at p[i][j] if it plays strategy i and the other player does
     * strategy j.
     */
    public Player(int[][] payoff)
    {
        this.payoff = payoff;
        this.strategy = findStrategy();
    }
    public Player(int[][] payoff, int strategy)
    {
        this.payoff = payoff;
        this.strategy = strategy;
    }
    
    public int getStrategy()
    {
        if(strategy == -1)
            strategy = findStrategy();
        return strategy;
    }
    
    /**
     * Decides what strategy this agent will play. If pre-play communication is
     * ever used this might need to be given some arguments or re-factored.
     * @return For now, always play the first strategy.
     */
    private int findStrategy()
    {
        return 0;
    }
}
