package sim.app.evolutiongame;

import java.util.ArrayList;
import java.util.HashSet;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

/**
 * Holds a number of Players that will play a game against each other and allow
 * us to observe the outcome of these games.
 * @author Ben Armstrong
 */
public class Population extends SimState
{
    /***************************************************************************
     * Variables that are not directly related to simulation parameters:
     **************************************************************************/
    private ArrayList<Player> players;
    /**
     * A list of players that have not yet played this round.
     */
    private ArrayList<Player> unplayedPlayers;
    /**
     * A list of players that have already played during this round.
     */
    private ArrayList<Player> playedPlayers;
    
    /**
     * Used to graphically represent the players. Currently has 250000 cells.
     */
    public Continuous2D field = new Continuous2D(1.0, 500, 500);
    
    
    
    /***************************************************************************
    * Variables for simulation parameters:
    ***************************************************************************/
    /**
     * The base percentage of an agent reproducing after playing a game.
     */
    double birthRate = 0.4;
    /**
     * Modifies the birth rate based off of payoff the agent received after the game.
     * Chance of reproduction = birthRate + payOff * birthRateModifier.
     */
    double birthRateModifier = 0.1;
    /**
     * Chance that an agent will die after playing a game. Death occurs after
     * reproduction.
     */
    double deathRate = 0.5;
    
    long numPlayers = 10000;//should probably have at least 1000 for anything useful
    
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
     * Methods to let user modify birth rate of all players.
     */
    public double getBirthRate(){
        return this.birthRate;
    }
    public void setBirthRate(double rate){
        this.birthRate = rate;
    }
    public Object domBirthRate(){
        return new sim.util.Interval(0.0, 1.0);
    }
    /*
     * Methods to let user modify birth rate of all players.
     */
    public double getBirthRateModifier(){
        return this.birthRateModifier;
    }
    public void setBirthRateModifier(double rate){
        this.birthRateModifier = rate;
    }
    public Object domBirthRateModifier(){
        return new sim.util.Interval(0.0, 1.0);
    }
    /*
     * Methods to let user modify birth rate of all players.
     */
    public double getDeathRate(){
        return this.deathRate;
    }
    public void setDeathRate(double rate){
        this.deathRate = rate;
    }
    public Object domDeathRate(){
        return new sim.util.Interval(0.0, 1.0);
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
        
        field.clear();
        
        int i = 0;
        this.players = new ArrayList<>();
        int[][] matrix;
        int strategy;
        Player p;
        while(i++ < numPlayers)
        {
            //initializes a player to a random strategy
            matrix = PayoffMatrices.getPayoffMatrix(random.nextBoolean());
            strategy = random.nextInt(matrix.length);
            p = new Player(matrix, strategy);
            players.add(p);
            field.setObjectLocation(p, 
                    new Double2D(field.getWidth()*0.5 + random.nextDouble()-0.5,
                            field.getHeight()*0.5 + random.nextDouble()-0.5));
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
