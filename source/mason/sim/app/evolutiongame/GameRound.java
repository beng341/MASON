package sim.app.evolutiongame;

import ec.util.MersenneTwisterFast;
import java.util.HashSet;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * This will run one round of the simulation. That means matching players against
 * each other, having them play, and having the relevant effect on the population.
 * 
 * This class exists to give maximal control over how many games are played per
 * time unit, how players are matched, how population is affected, etc..
 * The alternative was to have each Player be Steppable but they would need to be
 * matched against other Steppable Players so only some of them would actually do
 * their real step() and others would just get dragged along and play in some other
 * Player's step() function.
 * @author Ben Armstrong
 */
public class GameRound implements Steppable
{

    /**
     * Matches a number of members of the population against each other.
     * These Players will compete against each other and report the result.
     * The effect on the population is the carried out.
     * @param state 
     */
    @Override
    public void step(SimState state)
    {
        Population pop = (Population)state;
        HashSet<Player> players = pop.getPlayers();
        HashSet<Player> newPlayers = (HashSet<Player>) players.clone();
        MersenneTwisterFast random = pop.random;
        
        Player p1;
        Player p2;
        
        //for now, have every player matched against another player (or, one unmatched if an odd number of players)
        while(players.size() > 1)
        {
            //we need some way of quickly getting a randomly located Player.
            //Perhaps using a HashMap is not the best idea since we need order
            //before we can disrupt that order...
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Has Player 1 play against Player 2 and determines how the outcome will 
     * affect the population.
     * @param p1
     * @param p2
     * @return A list of players to add to the population. Note that at this point
     * Player 1 and Player 2 have been removed from the population.
     */
    private HashSet<Player> runMatch(Player p1, Player p2)
    {
        int payoff1 = p1.getPayoff(p2.getStrategy());
        int payoff2 = p2.getPayoff(p1.getStrategy());
        
        return reproduce(p1, p2, payoff1, payoff2);
    }
    
    /**
     * For testing purposes, right now we will just convert the winner to the 
     * strategy of the loser.
     * @param p1
     * @param p2
     * @param payoff1
     * @param payoff2
     * @return A HashSet containing the winner and the loser converted to the 
     * strategy of the winner.
     */
    private HashSet<Player> reproduce(Player p1, Player p2, int payoff1, int payoff2)
    {
        HashSet<Player> toAdd = new HashSet<>(4);
        if(payoff1 > payoff2)
            p2.setStrategy(p1.getStrategy());
        else
            p1.setStrategy(p2.getStrategy());
        toAdd.add(p1);
        toAdd.add(p2);
        
        return toAdd;
    }
}
