package sim.app.evolutiongame.modules.player.Congestion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;
import sim.field.grid.Grid2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Bag;

/**
 * Made to simulate John Conway's game of Life, in as generic a manner as
 * possible. Agents should be either alive or dead. At each time step, they 
 * check the number of players neighbouring cells that containing living players.
 * If the player is alive, the player stays alive if the number of neighbours
 * is contained in neighboursToLive. If the player is dead, it becomes alive
 * if the number of neighbours is found in neighboursToBeBorn.
 * Otherwise, the player dies/stays dead.
 * This Module will update the colour of each agent based on whether it is dead
 * or alive. Use colour at index 0 for players that are alive, colour at index 1
 * for players that are dead.
 * @author Ben Armstrong
 */
public class LifeOrDeathCongestion extends PlayerModule{
    
    private static final ArrayList<Integer> neighboursToLive = new ArrayList<>(Arrays.asList(2,3));
    private static final ArrayList<Integer> neighboursToBeBorn = new ArrayList<>(Arrays.asList(3));
    private int neighbourCount = 0;
    @Override
    public void run(Population state, Player p)
    {
        //mark "alive_at_start" to match up with whether the agent is currently alive
        //if no "alive" variable, choose a random state to start in
        if(!p.hasVariable("alive")){
            p.storeVariable("alive", state.random.nextBoolean());
        }
        p.storeVariable("alive_at_start", p.getVariable("alive"));
        
        Pair<Integer, Integer> location;
        if(!p.hasVariable("location"))
            p.storeVariable("location", Util.findPlayerLocation(state.env.grid, p));
        location = (Pair<Integer, Integer>) p.getVariable("location");
        
        //check what each neighbour says about "alive_at_start"
        //if they don't say anything, then check what they say about "alive"
        //update "alive"
        neighbourCount = 0;
        Bag neighbours = state.env.grid.getMooreNeighbors(location.getFirst(), location.getSecond(), 1, Grid2D.BOUNDED, false, null, null, null);
        for(Object o: neighbours){
            Player neighbour = (Player)o;
            if(neighbour.hasVariable("alive_at_start") && (boolean)neighbour.getVariable("alive_at_start")){
                neighbourCount++;
            }
        }
        
        if((boolean)p.getVariable("alive")){
            if(neighboursToLive.contains(neighbourCount)){
                //already alive, no need to save what's already there
            } else {
                //not the right number of neighbours, die now
                p.storeVariable("alive", false);
                state.env.gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(state.env.colors.get(1)));
            }
        } else {
            if(neighboursToBeBorn.contains(neighbourCount)){
                p.storeVariable("alive", true);
                state.env.gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(state.env.colors.get(0)));
            }
        }
    }
    
    @Override
    public void setup(Population pop, Player p){
        p.storeVariable("alive", pop.random.nextBoolean());
        p.storeVariable("alive_at_start", p.getVariable("alive"));
    }

}
