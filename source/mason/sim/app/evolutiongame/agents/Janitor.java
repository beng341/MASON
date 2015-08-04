package sim.app.evolutiongame.agents;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.Config;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Ben Armstrong
 */
public class Janitor implements Steppable {

    @Override
    public void step(SimState state)
    {
        Population pop = (Population)state;
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: Config.cleanupMethods.entrySet()){
            Object o = this.invokeMethod(entry.getValue().getSecond(), entry.getValue().getFirst(), pop);
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
