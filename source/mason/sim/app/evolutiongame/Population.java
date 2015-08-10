package sim.app.evolutiongame;

import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.agents.Observer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Janitor;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DenseGrid2D;
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
    private HashMap<Integer, Player> playerMap;
    private ArrayList<Player> players;
    public Environment env;
    
    /**
     * Used to graphically represent the players. Currently has 250000 cells.
     */
    public DenseGrid2D grid;
    
    /**
     * A list of methods that are run once in each step() method of each player.
     * The name of the implementation of the module that is in use maps to the
     * actual module class and to the run method of the class.
     */
    public LinkedHashMap<String, Util.Pair<Module, Method>> playerModules;
    public LinkedHashMap<String, Util.Pair<Module, Method>> environmentModules;
    
    /**
     * A list of names of variables that a player must have before running the
     * module.
     */
    public LinkedHashMap<Module, String[]> requiredPlayerVariables;
    public LinkedHashMap<Module, String[]> requiredEnvironmentVariables;
    
    /**
     * List of module implementations to run in the runModuleOutOfTurn method.
     * An example entry would be:
     *      "FindStrategy" -> (RandomStrategy.class, run())
     */
    public LinkedHashMap<String, Util.Pair<Module, Method>> preferredModules;
    
    /***************************************************************************
    * Variables for simulation parameters:
    ***************************************************************************/
    /**
     * The base percentage of an agent reproducing after playing a game.
     */
    double birthRate = 0.40;
    /**
     * Modifies the birth rate based off of payoff the agent received after the game.
     * Chance of reproduction = birthRate + payOff * birthRateModifier.
     */
    double birthRateModifier = 0.1;
    /**
     * Chance that an agent will die after playing a game. Death occurs after
     * reproduction.
     */
    double deathRate = 0.50;
    
    /**
     * True if data should be printed to the console at each step.
     */
    boolean printData = true;
    
    long numPlayers = 1000;//should probably have at least 1000 for anything useful
    private long gridHeight = 100;
    private long gridWidth = 100;
    
    int strategyDistribution = RANDOMCHOICES;
    /** Choose this to have agents that randomly choose a strategy */
    public static int RANDOMCHOICES = 0;
    /** Choose this to have agents that are assigned a random strategy that will not change */
    public static int RANDOMAGENTS = 1;
    
    int gameNumber = PrisonersDilemma;
    /** Game numbers that correspond to payoff matrices found in PayoffMatrices */
    public static int RockPaperScissors = 0;
    public static int ModifiedRockPaperScissors = 1;
    public static int EntryDeterrence = 2;
    public static int PrisonersDilemma = 3;
    
    public boolean getPrintData(){
        return this.printData;
    }
    public void setPrintData(boolean print){
        this.printData = print;
    }
    
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
    * Methods to set grid height and width.
    */
    public long getGridWidth(){
        return this.gridWidth;
    }
    public void setGridWidth(long num){
        this.gridWidth = num;
    }
    public Object domGridWidth(){
        return new sim.util.Interval(0, 500);
    }
    public long getGridHeight(){
        return this.gridHeight;
    }
    public void setGridHeight(long num){
        this.gridHeight = num;
    }
    public Object domGridHeight(){
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
                "Entry Deterrence Game", "Prisoners Dilemma"};
    }
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }
    public Player getPlayer(int id){
        return this.playerMap.get(id);
    }
    public HashMap<Integer, Player> getPlayerMap(){
        return this.playerMap;
    }
    
    
    @Override
    public void start()
    {
        super.start();
        
        //get list of methods that each player will run at every step
        this.playerModules = Config.playerModulesToRun;
        this.environmentModules = Config.environmentModulesToRun;
        this.preferredModules = Config.preferredPlayerModules;
        
        LinkedHashMap<String, String[]> args = Config.playerArguments;
        
        this.requiredPlayerVariables = new LinkedHashMap<>();
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: this.playerModules.entrySet()){
            this.requiredPlayerVariables.put(entry.getValue().getFirst(), args.get(entry.getKey()));
        }
        
        args = Config.environmentArguments;
        this.requiredEnvironmentVariables = new LinkedHashMap<>();
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: this.environmentModules.entrySet()){
            this.requiredEnvironmentVariables.put(entry.getValue().getFirst(), args.get(entry.getKey()));
        }
        
        PayoffMatrices.setGame(gameNumber);
        
        int i = 0;
        this.playerMap = new HashMap<>();
        this.players = new ArrayList<>();
        int[][] matrix;
        Player p;
        schedule.scheduleRepeating(new Observer(), 1, 1);
        schedule.scheduleRepeating(new Janitor(), 1, 1);
        while(i++ < numPlayers)
        {
            //initializes a player to a random strategy and schedule it
            matrix = PayoffMatrices.getPayoffMatrix(random.nextBoolean());
            p = new Player(matrix, this);
            
            playerMap.put(p.id, p);
            players.add(p);
            p.setStoppable(schedule.scheduleRepeating(p));
            
            //run setup modules
            for(Pair<Module, Method> pair: Config.playerSetupModules.values()){
                p.runModule(pair);
            }
            
        }
        //must set up after adding players to population
        this.env = new Environment(this);
        schedule.scheduleRepeating(env);
        for(Pair<Module, Method> env_modules: Config.environmentSetupModules.values()){
            this.env.runModule(env_modules);
        }
    }
    public Population(long seed)
    {
        super(seed);
    }
    public static void main(String[] args) {
        doLoop(Population.class, args);
        System.exit(0);
    }
    
    /**
     * Adds a new Player to the population and schedules it to keep playing
     * ad infinitum. Player will begin to play during the next time step.
     * @param p 
     * @return  
     */
    public Player addPlayer(Player p){
        playerMap.put(p.id, p);
        players.add(p);
        p.setStoppable(schedule.scheduleRepeating(p));
        return p;
    }
    public void removePlayer(Player p){
        playerMap.remove(p.id);
        players.remove(p);
    }
    
    @Override
    public String toString() {
        if(this.playerMap != null)
            return "Population of " + this.playerMap.size() + " Players.";
        else
            return "Empty Population";
    }
}
