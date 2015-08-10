package sim.app.evolutiongame.modules.player.Reproduction;

import java.util.ArrayList;
import java.util.HashMap;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;
import sim.field.grid.DenseGrid2D;

/**
 * A Module initially designed for replicating Smaldino's work. If the player has
 * more than 100 energy, a new player is created and 50 energy is subtracted 
 * this player and given to the new player.
 * @author Ben Armstrong
 */
public class EnergyLevelReproduction extends PlayerModule{
    
    public static final String[] args = {"energy"};
    
    int birthCount = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        HashMap<String, Object> arguments = super.getArguments(args);
        double energy = (double) arguments.get("energy");
        
        if(energy > 100){
            Pair<Integer, Integer> location = findRandomEmptyNeighbourCell(state, p);
            if(null != location){
                Player child = new Player(p, state);
                state.addPlayer(child);
                state.env.addPlayerToGridAtLocation(child, location.getFirst(), location.getSecond());
                child.storeVariable("location", location);
                
                child.storeVariable("energy", 50.0);
                p.storeVariable("energy", energy-50.0);
                birthCount++;
            }
        }
    }
    
    @Override
    public Object trackStatistics(){
        String toReturn =  "Births: " + birthCount;
        birthCount = 0;
        return toReturn;
    }
    
    public Pair<Integer, Integer> findRandomEmptyNeighbourCell(Population pop, Player p){
        
        Pair<Integer, Integer> location = Util.findPlayerLocation(pop.env.grid, p);
        int x = location.getFirst();
        int y = location.getSecond();
        
        ArrayList<Pair<Integer, Integer>> cells = new ArrayList<>();
        
        for(int i = -1; i < 2; ++i){
            for(int j = -1; j < 2; ++j){
                
                if(x+i < 0 || x+i >= pop.env.grid.getWidth() ||
                        y+j < 0 || y+j >= pop.env.grid.getHeight() ||
                        (i == 0 && j == 0))
                    continue;
                
                if(null != pop.env.grid.getObjectsAtLocation(x+i, y+j) &&
                        pop.env.grid.getObjectsAtLocation(x+i, y+j).isEmpty()){
                    cells.add(new Pair<>(x+i, y+j));
                }
            }
        }
        
        if(cells.isEmpty())
            return null;
        else
            return cells.get(pop.random.nextInt(cells.size()));
    }

}
