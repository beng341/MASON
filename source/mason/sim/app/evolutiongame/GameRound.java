package sim.app.evolutiongame;

import ec.util.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.HashSet;
import sim.app.evolutiongame.Util.Pair;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

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
    private MersenneTwisterFast random;
    private double birthRate;
    private double birthRateModifier;
    private double deathRate;
    
    public GameRound()
    {
        super();
        this.birthRate = 0.0;
        this.deathRate = 0.0;
        this.birthRateModifier = 0.0;
    }
    
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
        ArrayList<Player> players = pop.getPlayers();
        ArrayList<Player> newPlayers = new ArrayList<>();
        this.random = pop.random;
        this.birthRate = pop.birthRate;
        this.birthRateModifier = pop.birthRateModifier;
        this.deathRate = pop.deathRate;
        
        //print a bunch of stuff about the current state of the population
        System.out.println("\nWelcome to a new generation.");
        ArrayList<Integer>strategyCount = new ArrayList<>();
        for(int i = 0; i < PayoffMatrices.getMaxNumStrategies(); ++i)
            strategyCount.add(0);
        for(Player p: players)
        {
            if(strategyCount.get(p.getStrategy()) == null)
                strategyCount.set(p.getStrategy(), 0);
            strategyCount.set(p.getStrategy(), strategyCount.get(p.getStrategy())+1);
        }
        for(int i = 0; i < strategyCount.size(); ++i)
        {
            //System.out.println("The proportion of players at strategy " + i + " is " + (strategyCount.get(i)*1.0)/players.size());
            System.out.println("    The numbers of players at strategy " + i + " is " + strategyCount.get(i));
        }
        
        Player p1;
        Player p2;
        ArrayList<Player> resultOfMatch;
        pop.field.clear();
        //for now, have every player matched against another player (or, one unmatched if an odd number of players)
        while(players.size() > 1)
        {
            //have each player play against a random opponent
            p1 = players.remove(random.nextInt(players.size()));
            p2 = players.remove(random.nextInt(players.size()));
            resultOfMatch = runMatch(p1, p2);
            newPlayers.addAll(resultOfMatch);
            
            //have each player put in a random location
            for(Player p: resultOfMatch)
            {
                pop.field.setObjectLocation(p,
                        new Double2D(pop.field.getWidth()*0.5 + random.nextDouble()-0.5,
                                pop.field.getHeight()*0.5 + random.nextDouble()-0.5));
            }
            
        }
        if(players.size() == 1)//make sure we don't lose a player if there are an odd number of players
            newPlayers.add(players.get(0));
        
        pop.setPlayers(newPlayers);
    }
    
    /**
     * Has Player 1 play against Player 2 and determines how the outcome will 
     * affect the population.
     * @param p1
     * @param p2
     * @return A list of players to add to the population. Note that at this point
     * Player 1 and Player 2 have been removed from the population.
     */
    private ArrayList<Player> runMatch(Player p1, Player p2)
    {
        int payoff1 = p1.getPayoff(p2.getStrategy());//find payoff to player 1
        int payoff2 = p2.getPayoff(p1.getStrategy());//find payoff to player 2
        
        return reproduce(p1, p2, payoff1, payoff2);
    }
    
    /**
     * For testing purposes, right now we will just convert the winner to the 
     * strategy of the loser.
     * @param p1
     * @param p2
     * @param payoff1
     * @param payoff2
     * @return An ArrayList containing the winner and the loser converted to the 
     * strategy of the winner.
     */
    private ArrayList<Player> toAdd = new ArrayList<>();//apparently initializing to double capacity makes it faster
    private ArrayList<Player> reproduce(Player p1, Player p2, int payoff1, int payoff2)
    {
        toAdd.clear();//should be faster than new
        //conversionReproduction(p1, p2, payoff1, payoff2);
        probabilisticReproduction(p1, p2, payoff1, payoff2);
        
        return toAdd;
    }
    
    /**
     * TODO: Check if this conforms with Weibull.
     * Takes birthrate and deathrate into account. Currently, each player will have
     * a single child with probability birthrate + payoff * birthRateModifier.
     * As well, the player itself will be added back to the population with
     * probability 1-deathrate.
     * This has a relatively large number of calls to random so may be quite slow.
     * @return 
     */
    public ArrayList<Player> probabilisticReproduction(Player p1, Player p2, int payoff1, int payoff2)
    {
        double nextRand = random.nextDouble();
        double checkAgainst = (birthRate + birthRateModifier*payoff1);
        //check for player 1 child
        if(nextRand <= checkAgainst)
            toAdd.add(new Player(p1, null));
        
        //check if player 1 lives
        nextRand = random.nextDouble();
        if(nextRand > deathRate)
            toAdd.add(p1);
        
        nextRand = random.nextDouble();
        checkAgainst = (birthRate + birthRateModifier*payoff2);
        //check for player 2 child
        if(nextRand <= checkAgainst)
            toAdd.add(new Player(p2, null));
        
        //check if player 2 lives
        nextRand = random.nextDouble();
        if(nextRand > deathRate)
            toAdd.add(p2);
        
        return toAdd;
    }
    
    /**
     * Converts the losing player to the strategy of the winning player.
     * @return 
     */
    public ArrayList<Player> conversionReproduction(Player p1, Player p2, int payoff1, int payoff2)
    {
        if(payoff1 > payoff2)
            p2.setStrategy(p1.getStrategy());
        else
            p1.setStrategy(p2.getStrategy());
        toAdd.add(p1);
        toAdd.add(p2);
        return toAdd;
    }
    
    /**
     * Kills the parents and adds a number of players equal to the payoff of
     * each original player with the strategy of the player that earned that payoff.
     * @return 
     */
    public ArrayList<Player> utilityReproduction(Player p1, Player p2, int payoff1, int payoff2)
    {
        for(int i = 0; i < payoff1; ++i)
            toAdd.add(new Player(p1, null));
        for(int i = 0; i < payoff2; ++i)
            toAdd.add(new Player(p2, null));
        
        return toAdd;
    }
}
