package sim.app.evolutiongame;

import ec.util.MersenneTwisterFast;

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
    
    private MersenneTwisterFast random;
    
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
        random = new MersenneTwisterFast();
    }
    public Player(int[][] payoff, int strategy)
    {
        this.payoff = payoff;
        this.strategy = strategy;
        random = new MersenneTwisterFast();
    }
    public Player(Player parent)
    {
        this.payoff = parent.payoff;
        this.strategy = parent.strategy;
        random = new MersenneTwisterFast();
    }
    
    /**
     * Gets the payoff played when this Player is matched against an opponent
     * that plays strategy opp.
     * @param opp An int representing the strategy played by the other Player.
     * @return The payoff this player gets against strategy opp.
     */
    public int getPayoff(int opp)
    {
        //TODO: Add some validation here?
        
        return payoff[getStrategy()][opp];
    }
    
    public int getStrategy()
    {
        if(strategy == -1)
            strategy = findStrategy();
        return strategy;
    }
    
    /**
     * Be careful with using this method. Make sure we don't change a Player's 
     * strategy when we really should create a new Player.
     * @param strategy 
     */
    public void setStrategy(int strategy)
    {
        this.strategy = strategy;
    }
    
    /**
     * Decides what strategy this agent will play. If pre-play communication is
     * ever used this might need to be given some arguments or re-factored.
     * @return For now, just pick a random strategy if one has not been assigned.
     */
    private int findStrategy()
    {
        return random.nextInt(payoff.length);
    }
    
    public String toString()
    {
        return "Strategy " + this.strategy;
    }
}
