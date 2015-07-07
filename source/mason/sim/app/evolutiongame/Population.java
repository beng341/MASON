package sim.app.evolutiongame;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.modules.PlayGame.PlayGame;
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
    
    /**
     * True if data should be printed to the console at each step.
     */
    boolean printData = false;
    
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
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }
    public void setPlayers(ArrayList<Player> players)
    {
        this.players = players;
    }
    
    
    @Override
    public void start()
    {
        super.start();
        
        //this should have a list of all methods that get called on every player
        //at every step
        //the list is decided by the configuration file that I should create
        //Method[] methods = getMethods();
        
        PayoffMatrices.setGame(gameNumber);
        
        field.clear();
        
        int i = 0;
        this.players = new ArrayList<>();
        int[][] matrix;
        int strategy;
        Player p;
        schedule.scheduleRepeating(new Observer(), 1, 1);
        while(i++ < numPlayers)
        {
            //initializes a player to a random strategy and schedule it
            matrix = PayoffMatrices.getPayoffMatrix(random.nextBoolean());
            strategy = random.nextInt(matrix.length);
            p = new Player(matrix, strategy, this);
            players.add(p);
            p.setStoppable(schedule.scheduleRepeating(p));
            
            field.setObjectLocation(p, 
                    new Double2D(field.getWidth()*0.5 + random.nextDouble()-0.5,
                            field.getHeight()*0.5 + random.nextDouble()-0.5));
        }
        
        HashMap<String, ArrayList<String>> modules = findModuleImplementations();
        readModulesFromFile(this);
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
     * ad infinitum.
     * @param p 
     */
    public void addPlayer(Player p){
        players.add(p);
        p.setStoppable(schedule.scheduleRepeating(p));
    }
    public void removePlayer(Player p){
        players.remove(p);
    }
    
    /**
     * Finds a list of all java classes that are in each module subdirectory.
     * Associates with each module the classes under it.
     * Ex: If /modules contains folders "play" and "move", with classes "play1",
     * "play2", "move1", "move2" then this will return a map of "play" -> {play1, play2},
     * "move" -> {move1, move2}.
     * This will likely assume that /modules contains only folders and that each
     * folder within /modules contains only files providing an implementation
     * of that module.
     */
    private static HashMap<String, ArrayList<String>> findModuleImplementations() {
        String path = "source/mason/sim/app/evolutiongame/modules";
        FileFilter folderFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname){
                return pathname.isDirectory();
            }};
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname){
                return pathname.isFile();
            }};
        
        
        File folder = new File(path);
        
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        ArrayList<String> implementations = null;
        String s = folder.getAbsolutePath();
        
        for(File module: folder.listFiles(folderFilter)) {
            for(File implementation: module.listFiles(fileFilter)) {
                if(implementations == null)
                    implementations = new ArrayList<>();
                implementations.add(implementation.getName().replace(".java", ""));
            }
            if(implementations != null) {
                map.put(module.getName(), implementations);
                implementations = null;
            }
            
        }
        
        return map;
    }
    
    private static void readModulesFromFile(Population p) {
        Gson gson = new Gson();
        
        String[] test = new String[]{"Test String", "I hope this works"};
        
        HashMap<String, Object> everythingMap = new HashMap<>();
        
//        HashMap<String, 
        
        everythingMap.put("modules", test);
        everythingMap.put("test", new String[]{"Hello", "Goodbye"});
        
        //example of writing to a file
        System.out.println("Convert Java object to JSON format and save to file.");
        try (FileWriter writer = new FileWriter("modules.json")) {
          writer.write(gson.toJson(everythingMap));
        } catch (IOException e) {
        }
        
        
        //example of reading from a file
        System.out.println("Read JSON from file, convert JSON string back to object");
        Object o;
        try (BufferedReader reader = new BufferedReader(new FileReader("modules.json"))) {
              o = gson.fromJson(reader, HashMap.class);
        } catch (FileNotFoundException e) {
            System.out.println("Finised with JSON examples now.");
        } catch (IOException e) {
            System.out.println("Finised with JSON examples now.");
        }
        
        System.out.println("Finised with JSON examples now.");
    }

    /**
     * Generates a list of all methods that should be run for each player in
     * each time step. Methods should all have the signature:
     *  public static void run(Population state, Player p)
     * The classes that the methods are in should be specified in the
     * configuration file.
     * @return 
     */
    private Method[] getMethods() {
        
        Class c = null;
        try {
            c = Class.forName("sim.app.evolutiongame.modules.PlayGame.PlayGame");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }
        Method m = null;
        try {
            m = c.getMethod("run", Population.class, Player.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //to invoke:
        try {
            //null is because the method is static
            m.invoke(null, this, new Player(new int[0][0], this));
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
