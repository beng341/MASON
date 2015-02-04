package sim.app.evolutiongame;

import java.util.ArrayList;
import java.util.HashSet;
import sim.engine.SimState;

/**
 * Holds a number of Players that will play a game against each other and allow
 * us to observe the outcome of these games.
 * @author Ben Armstrong
 */
public class Population extends SimState
{
    private ArrayList<Player> players;
    
    long numPlayers = 100000;//should probably have at least 1000 for anything useful
    
    int strategyDistribution = RANDOMCHOICES;
    /** Choose this to have agents that randomly choose a strategy */
    public static int RANDOMCHOICES = 0;
    /** Choose this to have agents that are assigned a random strategy that will not change */
    public static int RANDOMAGENTS = 1;
    
    //start with rock paper scissors for basic testing
    public int[][] payoff = new int[][]
        {
            {1, 2, 0},
            {0, 1, 2},
            {2, 0, 1}
        };
    //a modified rock paper scissors game found in chapter 3
//    public int[][] payoff = new int[][]
//        {
//            {1, 5, 0},
//            {0, 1, 5},
//            {5, 0, 4}
//        };
    
    /*
     * Methods to let user set up the number of players 
     */
    public long getNumPlayers(){
        return this.numPlayers;
    }
    public void setNumPlayers(long num){
        this.numPlayers = num;
    }
    public Object domNumPlayers(){
        return new sim.util.Interval(0, 100000);
    }
    /*
     * Methods to let user decide player strategies.
     */
    public int getStrategyDistribution(){
        return this.strategyDistribution;
    }
    public void setStrategyDistribution(int strat){
        this.strategyDistribution = strat;
    }
    public Object domStrategyDistribution(){
        return new String[]{"Random Actions", "Fixed Actions"};
    }
    
    
    @Override
    public void start()
    {
        super.start();
        
        int i = 0;
        this.players = new ArrayList<>();
        while(i++ < numPlayers)
        {
            players.add(new Player(payoff, random.nextInt(payoff.length)));
        }
        
        schedule.scheduleRepeating(new GameRound());
    }
    public Population(long seed)
    {
        super(seed);
    }
    public static void main(String[] args) {
        doLoop(Population.class, args);
        System.exit(0);
    }
    
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }
    public void setPlayers(ArrayList<Player> players)
    {
        this.players = players;
    }
    
    /**
     * 
     * @return The maximum number of possible strategies. This concept really 
     * makes the most sense for symmetric games only.
     */
    public int getMaxNumStrategies()
    {
        return Math.max(payoff.length, payoff[0].length);
    }
}
