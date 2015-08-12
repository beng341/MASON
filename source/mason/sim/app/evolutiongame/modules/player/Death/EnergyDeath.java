package sim.app.evolutiongame.modules.player.Death;

import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 * Checks if a players energy is below zero, if it is the player is killed.
 * @author Ben Armstrong
 */
public class EnergyDeath extends PlayerModule{
    
    int deathCount = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        if(!p.hasVariable("energy")){
            p.storeVariable("energy", state.random.nextInt(50)+1.0);
        }
        double energy = (double) p.getVariable("energy");
        if(energy < 0){
            p.die();
            deathCount++;
            if(p.hasVariable("location")){
                Pair<Integer, Integer> location = (Pair<Integer, Integer>) p.getVariable("location");
                state.env.grid.removeObjectAtLocation(p, location.getFirst(), location.getSecond());
            } else {
                deathCount = deathCount;
            }
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
        
        data.get(0).add("Deaths");
        data.get(1).add(""+deathCount);
        deathCount = 0;
        
        return data;
    }

}
