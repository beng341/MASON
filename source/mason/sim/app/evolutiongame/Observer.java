package sim.app.evolutiongame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 *
 * @author Ben Armstrong
 */
public class Observer implements Steppable {

    @Override
    public void step(SimState state) {
        
        boolean print = ((Population)state).getPrintData();
        
        Population pop = (Population)state;
        
        if(pop.getPlayers().isEmpty()){
            System.out.println("No players remain. Ending Observations.");
            pop.finish();
        }
        
        
        int[] strategyCounts = new int[PayoffMatrices.getMaxNumStrategies()];
        for(Player p: pop.getPlayers()){
            if(!p.hasVariable("strategy")){
                p.runModuleOutOfTurn("FindStrategy");
            }
            strategyCounts[(int)p.getVariable("strategy")]++;
        }
        
        //print everything all at once so separate logging could be added more easily
        if(print) {
            System.out.printf("Printing data for time %f", state.schedule.getTime());
            System.out.print("\n");
            System.out.printf("Number of agents: %4d",  pop.getPlayers().size());
            System.out.print("\n");
            
            for(Map.Entry<String, Util.Pair<Module, Method>> entry: Config.statisticsMethods.entrySet()){
                Object o = this.invokeMethod(entry.getValue().getSecond(), entry.getValue().getFirst(), pop);
                if( null != o)
                    System.out.println(o);
            }

            for(int jk = 0; jk < strategyCounts.length; ++jk){
                System.out.printf("Number of agents of strategy %2d currently at %4d", jk, strategyCounts[jk]);
                System.out.print("\n");
            }
            System.out.print("------------------------------------------------------------");
            System.out.print("\n");
        }
    }
    
    public Object invokeMethod(Method m, Module module, Population pop) {
        try {
            return m.invoke(module);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    
    
}
