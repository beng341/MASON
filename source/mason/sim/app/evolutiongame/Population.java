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
    private Observer obs;
    
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
     * True if data should be printed to the console at each step.
     */
    boolean printData = true;
    boolean saveData = false;
    
    long numPlayers = 1000;//should probably have at least 1000 for anything useful
    long maxPlayers = 5000;
    private long gridHeight = 100;
    private long gridWidth = 100;
    
    int gameNumber = PrisonersDilemma;
    /** Game numbers that correspond to payoff matrices found in PayoffMatrices */
    public static int RockPaperScissors = 0;
    public static int ModifiedRockPaperScissors = 1;
    public static int EntryDeterrence = 2;
    public static int PrisonersDilemma = 3;
    public static int ZeroPayoffGame = 4;
    public static int FivePayoffGame = 5;
    
    public boolean getPrintData(){
        return this.printData;
    }
    public void setPrintData(boolean print){
        this.printData = print;
    }
    
    public boolean getSaveData(){
        return this.saveData;
    }
    public void setSaveData(boolean save){
        this.saveData = save;
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
    public long getMaxPlayers(){
        return this.maxPlayers;
    }
    public void setMaxPlayers(long num){
        this.maxPlayers = num;
    }
    public Object domMaxPlayers(){
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
        return new sim.util.Interval(0, 500);
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
                "Entry Deterrence Game", "Prisoners Dilemma", "Zero Payoff To All", 
                "5 Payoff to All"};
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
        //I was seeing some suspicous behaviour from the random number generator
        //so I just want to ensure it's fully "primed"...
        int j = 0;
        for(int i = 1; i < 25; ++i)
            j = i;
        
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
        obs = new Observer(this);
        schedule.scheduleRepeating(obs, 1, 1);
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
                //p.runModule(pair);
                pair.getFirst().setup(this, p);
            }
            
        }
        //must set up after adding players to population
        this.env = new Environment(this);
        schedule.scheduleRepeating(env);
        for(Pair<Module, Method> env_modules: Config.environmentSetupModules.values()){
            this.env.runModule(env_modules);
        }
    }
    
    @Override
    public void finish(){
        super.finish();
        obs.closeWriter();
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
