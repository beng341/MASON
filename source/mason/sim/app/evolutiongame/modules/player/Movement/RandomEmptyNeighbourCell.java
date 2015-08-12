package sim.app.evolutiongame.modules.player.Movement;

import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;
import sim.field.grid.DenseGrid2D;
import sim.field.grid.Grid2D;
import sim.util.Bag;

/**
 *
 * @author Ben Armstrong
 */
public class RandomEmptyNeighbourCell extends PlayerModule{

    Util.Pair<Integer, Integer> location;
    
    int movedCount = 0;
    int noSpaceToMove = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        Pair<Integer, Integer> location;
        if (p.hasVariable("location")){
            location = (Util.Pair<Integer, Integer>) p.getVariable("location");
        } else {
            location = Util.findPlayerLocation(state.env.grid, p);
        }
        
        if(null == location)
            return;
        
        //can't move if there are no empty spaces
        Bag neighbours = state.env.grid.getMooreNeighbors(location.getFirst(), location.getSecond(), 1, Grid2D.BOUNDED, false, null, null, null);
        if((neighbours.size() == 8 && !neighbours.contains(p)) ||
                neighbours.size() == 9 && neighbours.contains(p)){
            return;
        }
        
        if(p.hasVariable("should_move") && (boolean)p.getVariable("should_move")){
            Util.Pair<Integer, Integer> move_to = findRandomEmptyNeighbourCell(state, p);
            if(null == move_to){
                noSpaceToMove++;
                return;
            }
            
            DenseGrid2D grid = state.env.grid;
            grid.removeObjectAtLocation(p, location.getFirst(), location.getSecond());
            grid.addObjectToLocation(p, move_to.getFirst(), move_to.getSecond());
            p.storeVariable("location", move_to);
            
            //moving should stop us from being able to play
            p.storeVariable("last_played", state.schedule.getSteps());
            movedCount++;
        }
    }
    
    public void cleanUp(Population pop){
        for(Player p: pop.getPlayers()){
            p.removeVariable("should_move");
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
        data.get(0).add("Movements Made");
        data.get(1).add("" + movedCount);
        movedCount = 0;
        
        //add second attribute
        data.get(0).add("No Space To Move");
        data.get(1).add("" + noSpaceToMove);
        movedCount = 0;
        
        return data;
    }
    
    public Util.Pair<Integer, Integer> findRandomEmptyNeighbourCell(Population pop, Player p){
        
        location = Util.findPlayerLocation(pop.env.grid, p);
        int x = location.getFirst();
        int y = location.getSecond();
        
        ArrayList<Util.Pair<Integer, Integer>> cells = new ArrayList<>();
        
        for(int i = -1; i < 2; ++i){
            for(int j = -1; j < 2; ++j){
                
                if(x+i < 0 || x+i >= pop.env.grid.getWidth() ||
                        y+j < 0 || y+j >= pop.env.grid.getHeight() ||
                        (i == 0 && j == 0))
                    continue;
                
                if(pop.env.grid.getObjectsAtLocation(x+i, y+j) == null ||
                        pop.env.grid.getObjectsAtLocation(x+i, y+j).isEmpty()){
                    cells.add(new Util.Pair<>(x+i, y+j));
                }
            }
        }
        
        if(cells.isEmpty())
            return null;
        else
            return cells.get(pop.random.nextInt(cells.size()));
    }

}
