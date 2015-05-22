package sim.app.evolutiongame;

import java.util.ArrayList;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Ben Armstrong
 */
public class Observer implements Steppable {

    @Override
    public void step(SimState state) {
        
        System.out.printf("Printing data for time %d", state.schedule.getTime()*1.0);
        
        Population pop = (Population)state;
        
        int[] strategyCounts = new int[PayoffMatrices.getMaxNumStrategies()];
        for(Player p: pop.getPlayers()){
            strategyCounts[p.getStrategy()]++;
        }
        
        System.out.printf("Number of agents: %4d",  pop.getPlayers().size());
        
        for(int i = 0; i < strategyCounts.length; ++i){
            System.out.printf("Number of agents of strategy %i is %4d", i, strategyCounts[i]);
        }
    }
    
    
    
    
}
