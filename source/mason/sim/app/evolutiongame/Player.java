package sim.app.evolutiongame;

import ec.util.MersenneTwisterFast;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;



/**
 * The base class for a player that plays a game. Games will be two-player so
 * each Player will always be matched up against another Player. The match ups
 * will be done by the GameRound at every step in the simulation.
 * @author Ben Armstrong
 */
public class Player implements Steppable
{
    private int[][] payoffMatrix;
    
    /**
     * Contains the most recent payoff earned by this player. This is always
     * updated at the same time as lastPlayed so we can know whether this value
     * is still current.
     */
    private int payoff;
    /**
     * tracks the last time this player played so that it does not play twice in
     * one time step (unless it is allowed to, then it can...)
     */
    private double lastPlayed;
    /**
     * Contains the Population object controlling the simulation. Use this to 
     * access things like the schedule or random number generator.
     */
    private Population pop;
    
    /**
     * The index of the strategy that this player will play. Initialized to -1 
     * so we can tell when it's not been set.
     */
    private int strategy = -1;
    
    
    private double birthRate;
    private double birthRateModifier;
    private double deathRate;
    
    /**
     * This is what is returned when the agent is put on the schedule.
     * Call stoppable.stop() to remove this agent from the schedule.
     */
    private Stoppable stoppable;
    public void setStoppable(Stoppable s){this.stoppable = s;}
    
    /**
     * The id that the next player should be assigned. This should be incremented
     * after each assignment. Unless there are over 2 billion different players
     * in any given run it should be fine to use an int rather than a long.
     */
    private static int id_count = 0;
    
    /**
     * The id of this player. No other player should have this id.
     */
    private int id;
    
    /**
     * 
     * @param payoff Matrix representing payoff functions. This player gets the 
     * payoff at p[i][j] if it plays strategy i and the other player does
     * strategy j.
     */
    public Player(int[][] payoff, Population pop)
    {
        this.payoffMatrix = payoff;
        this.strategy = findStrategy();
        this.pop = pop;
        this.id = id_count++;
    }
    public Player(int[][] payoff, int strategy, Population pop)
    {
        this.payoffMatrix = payoff;
        this.strategy = strategy;
        this.pop = pop;
        this.id = id_count++;
    }
    public Player(Player parent, Population pop)
    {
        this.payoffMatrix = parent.payoffMatrix;
        this.strategy = parent.strategy;
        this.pop = pop;
        this.id = id_count++;
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
        
        return payoffMatrix[getStrategy()][opp];
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
        return pop.random.nextInt(payoffMatrix.length);
    }
    
    public int lastPlayed(){
        return 0;
    }
    
    /**
     * Finds all players this player is currently eligible to play against. This
     * should take into account lastPlayed time, spatial location, and simulation
     * parameters.
     * @return 
     */
    private ArrayList<Player> findOpponents(){
        
        //for now, just choose from all possible players effectively making the
        //lastPlayed time useless
        
        return pop.getPlayers();
    }
    
    /**
     * Has this player play a game against one or more of the potential opponents.
     * Depending upon simulation parameters, a player might be able to play against
     * exactly one opponent or multiple opponents.
     * Sets the payoff value earned by this player and the other players that
     * played.
     * 
     * Be sure to update lastPlayed for all relevant players in this method.
     * @param opponents 
     */
    private void playGameAgainst(ArrayList<Player> opponents){
        
        if(opponents.size() == 0)
            return;
        
        //default behaviour will be to pick a random opponent and play them
        Player opp = opponents.get(pop.random.nextInt(opponents.size()));
        opp.playWith(this);
        playWith(opp);
    }
    
    /**
     * Sets payoff and lastPlayed time for this player playing against given
     * opponent.
     * @param opponent 
     */
    public void playWith(Player opponent){
        lastPlayed = pop.schedule.getTime();
        payoff = payoffMatrix[getStrategy()][opponent.strategy];
    }
    
    /**
     * Using the given payoff amount received this round, the player will do stuff...
     * This might be modifying energy levels, killing itself or other things.
     * @param payoff 
     */
    private void usePayoff(int payoff){
        
    }
    
    /**
     * Using the agents most recent payoff and possibly its energy level, the agent
     * will try to reproduce. Any created agents are added to the population.
     */
    private void tryToReproduce(int payoff){
        
        //default behaviour will copy probabilisticReproduction from GameRound
        //reproduce with probability (birthRate + birthRateModifier*payoff)
        if(pop.random.nextDouble() <= (birthRate+birthRateModifier*payoff))
            pop.addPlayer(new Player(this, pop));
    }
    
    /**
     * Using the agents death rate and possibly energy level and most recent payoff
     * the agent will check to see if it should die.
     * If an agent dies it is removed from the population and performs no more
     * actions.
     */
    private void tryToDie(){
        if(pop.random.nextDouble() <= (deathRate)){
            pop.removePlayer(this);
            if(stoppable != null)
                stoppable.stop();
            else
                System.out.println("Stoppable is null");
        }
    }
    
    /**
     * Gets the "variable" of the specified name from the global variable list 
     * in this Player's Population. The result should be cast to it's proper 
     * type.
     * This approach is not great for security but I suspect it is the
     * fastest and certainly is the most general approach I came up with.
     * @param name Name of the variable desired. This is prepended with the id 
     * of this player.
     * @return 
     */
    public Object getVariable(String name) {
        name = id + "_" + name;
        
        return this.pop.getPlayerVariable(name);
    }
    
    /**
     * Invokes a method given by the user and catches any errors.
     * @param m
     * @param args
     * @return 
     */
    public Object invokeMethod(Method m, Object args) {
        try {
            return m.invoke(null, pop, this, null);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public void step(SimState state){
        
        pop = (Population)state;
        this.birthRate = pop.birthRate;
        this.birthRateModifier = pop.birthRateModifier;
        this.deathRate = pop.deathRate;
//        //to invoke:
//        try {
//            //first null is because the method is static
//            m.invoke(null, null, new Player(new int[0][0], null));
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Method currentMethod;
        Object result = null;
        if(pop.playerMethods.containsKey("PotentialPartnerDiscovery")){
            currentMethod = pop.playerMethods.get("PotentialPartnerDiscovery");
            result = this.invokeMethod(currentMethod, null);
        }
        
        //find potential opponents (currently does not take into account the last
        //played time)
        ArrayList<Player> potentialOpponents = findOpponents();
        
        if(result != null) {
            potentialOpponents = (ArrayList<Player>)result;
        }
        
        //if not played yet this round, play now
        //doing this will update lastPlayed, as well as set the payoff value
        if(lastPlayed < pop.schedule.getTime())
            playGameAgainst(potentialOpponents);
        
        //maybe do something with the payoff gained
            //modify energy levels, change strategy, etc.
        usePayoff(payoff);
        
        tryToReproduce(payoff);
        
        tryToDie();
        
    }
    
    
    
    public String toString()
    {
        return "Strategy " + this.strategy;
    }
}
