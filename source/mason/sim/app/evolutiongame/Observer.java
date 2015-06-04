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
        
        boolean print = ((Population)state).getPrintData();
        
        Population pop = (Population)state;
        
        int[] strategyCounts = new int[PayoffMatrices.getMaxNumStrategies()];
        for(Player p: pop.getPlayers()){
            strategyCounts[p.getStrategy()]++;
        }
        
        //print everything all at once so separate logging could be added more easily
        if(print) {
            System.out.printf("Printing data for time %f", state.schedule.getTime());
            System.out.print("\n");
            System.out.printf("Number of agents: %4d",  pop.getPlayers().size());
                System.out.print("\n");

            for(int jk = 0; jk < strategyCounts.length; ++jk){
                System.out.printf("Number of agents of strategy %2d currently at %4d", jk, strategyCounts[jk]);
                System.out.print("\n");
            }
            System.out.print("------------------------------------------------------------");
            System.out.print("\n");
        }
    }
    
    
    
    
}
