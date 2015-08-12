package sim.app.evolutiongame.modules.player.FindOpponents;

import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;
import sim.field.grid.DenseGrid2D;
import sim.field.grid.Grid2D;
import sim.util.Bag;

/**
 * A part of the replication of Smaldino's model. This module should search the
 * environment for a neighbouring player that has not yet played. Randomly
 * select one of these players (if there are any) and set it as the result,
 * called "opponent". If no opponent is found, the player is given the variable
 * "should_move" pointing at boolean true. This causes the player to move/not move
 * as is desired.
 *
 * @author Ben Armstrong
 */
public class RandomNeighbour extends PlayerModule
{
    
    public static final String[] args = {};
    
    int numToldToMove = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        DenseGrid2D grid = state.env.grid;
        //get player location
        Pair<Integer, Integer> location;
        if (p.hasVariable("location")){
            location = (Pair<Integer, Integer>) p.getVariable("location");
        } else {
            location = Util.findPlayerLocation(grid, p);
            p.storeVariable("location", location);
            if(location == null){
                //add player to the field, this shouldn't happen when trying to 
                //replicate Smaldino's work.
                state.env.addPlayerToGrid(p);
            }
        }
        
        Bag neighbours = grid.getMooreNeighbors(location.getFirst(), location.getSecond(), 1, Grid2D.BOUNDED, false, null, null, null);
        //randomize the order of the potential opponents and find one that
        //has not played. If they've all played, just don't set an opponent
        neighbours.shuffle(state.random);
        if(neighbours.contains(p))
            neighbours.remove(p);
        for(int i = 0; i < neighbours.size(); ++i){
            if(((Long)((Player)neighbours.get(i)).getVariable("last_played")) < state.schedule.getSteps()){
                Player opponent = (Player) neighbours.get(state.random.nextInt(neighbours.size()));
                p.storeVariable("opponent", opponent);
            }
        }
        if(!p.hasVariable("opponent") && ((Long)p.getVariable("last_played")) < state.schedule.getSteps())
            p.storeVariable("should_move", true);
        
        if(p.hasVariable("opponent") && p.hasVariable("should_move"))
            System.err.println(p + " has an opponent and should move.");
    }
    
    @Override
    public void cleanUp(Population pop){
        
        //We want to ensure that players not standing beside another player have
        //no opponent so they move.
        for(Player player: pop.getPlayers()){
            player.removeVariable("opponent");
        }
    }
    
    ArrayList<ArrayList<String>> data;
    @Override
    public Object trackStatistics(){
        if(null == data)
            data = new ArrayList<>();
        else
            data.clear();
        data.add(new ArrayList<>());
        data.add(new ArrayList<>());
        
        //add first attribute
        data.get(0).add("Told To Move");
        data.get(1).add("" + numToldToMove);
        numToldToMove = 0;
        
        return data;
    }

}
