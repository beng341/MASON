package sim.app.evolutiongame.modules.player.FindOpponents;

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
    
    int neighboursFound = 0;
    int payoffMatrixCount = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        if(p.hasVariable("payoff_matrix"))
            payoffMatrixCount++;
        
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
        if(neighbours.isEmpty()){
            //can't play, remember that p should move
            p.storeVariable("should_move", true);
        } else {
            //randomize the order of the potential opponents and find one that
            //has not played. If they've all played, just don't set an opponent
            neighbours.shuffle(state.random);
            if(neighbours.contains(p))
                neighbours.remove(p);
            for(int i = 0; i < neighbours.size(); ++i){
                if(((Double)((Player)neighbours.get(i)).getVariable("last_played")) < state.schedule.getTime()){
                    Player opponent = (Player) neighbours.get(state.random.nextInt(neighbours.size()));
                    p.storeVariable("opponent", opponent);
                    neighboursFound++;
                }
            }
        }
    }
    
    @Override
    public void cleanUp(Population pop){
        
        //We want to ensure that players not standing beside another player have
        //no opponent so they move.
        for(Player player: pop.getPlayers()){
            player.removeVariable("opponent");
        }
    }
    
    @Override
    public Object trackStatistics(){
        String neighbourCount =  "Neighbours Found: " + neighboursFound;
        String matrixCount = "Matrices Found: " + payoffMatrixCount;
        payoffMatrixCount = 0;
        neighboursFound = 0;
        return neighbourCount + "\n" + matrixCount;
    }

}
