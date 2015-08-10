package sim.app.evolutiongame.modules.player.CostOfLiving;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.environment.EnvironmentModule;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class EnergyCostOfLiving extends PlayerModule {

    private static final double cost = 1.5;
    private int energyRemoved = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        if(p.hasVariable("energy")){
            double energy = (double) p.getVariable("energy");
            p.storeVariable("energy", energy-cost);
            energyRemoved += cost;
        } else {
            p.storeVariable("energy", state.random.nextInt(50)+1-cost);
            energyRemoved += cost;
        }
    }
    
    @Override
    public Object trackStatistics(){
        String toReturn = "Energy Removed: " + energyRemoved;
        energyRemoved = 0;
        return toReturn;
    }
}
