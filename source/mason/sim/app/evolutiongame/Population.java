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
    
    int gameNumber = RockPaperScissors;
    /** Game numbers that correspond to payoff matrices found in PayoffMatrices */
    public static int RockPaperScissors = 0;
    public static int ModifiedRockPaperScissors = 1;
    public static int EntryDeterrence = 2;
    
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
    
    /*
     * Methods to let user decide what game will be played.
     */
    public int getGameNumber(){
        return this.gameNumber;
    }
    public void setGameNumber(int num){
        this.gameNumber = num;
    }
    public Object domGameNumber(){
        return new String[]{"Rock Paper Scissors", "Modified Rock Paper Scissors",
                "Entry Deterrence Game"};
    }
    
    
    @Override
    public void start()
    {
        super.start();
        PayoffMatrices.setGame(gameNumber);
        
        int i = 0;
        this.players = new ArrayList<>();
        int[][] matrix;
        int strategy;
        while(i++ < numPlayers)
        {
            //initializes a player to a random strategy
            matrix = PayoffMatrices.getPayoffMatrix(random.nextBoolean());
            strategy = random.nextInt(matrix.length);
            players.add(new Player(matrix, strategy));
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
}
