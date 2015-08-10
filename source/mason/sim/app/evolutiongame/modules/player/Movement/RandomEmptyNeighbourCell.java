package sim.app.evolutiongame.modules.player.Movement;

import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;
import sim.field.grid.DenseGrid2D;

/**
 *
 * @author Ben Armstrong
 */
public class RandomEmptyNeighbourCell extends PlayerModule{

    Util.Pair<Integer, Integer> location;
    
    int movedCount = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        if(p.hasVariable("should_move") && (boolean)p.getVariable("should_move")){
            Util.Pair<Integer, Integer> move_to = findRandomEmptyNeighbourCell(state, p);
            
            DenseGrid2D grid = state.env.grid;
            grid.removeObjectAtLocation(p, location.getFirst(), location.getSecond());
            grid.addObjectToLocation(p, move_to.getFirst(), move_to.getSecond());
            movedCount++;
        }
    }
    
    public void cleanUp(Population pop){
        for(Player p: pop.getPlayers()){
            p.removeVariable("should_move");
        }
    }
    
    @Override
    public Object trackStatistics(){
        String toReturn =  "Players Moved: " + movedCount;
        movedCount = 0;
        return toReturn;
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
