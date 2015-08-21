package sim.app.evolutiongame.modules.player.CostOfLiving;

import java.text.DecimalFormat;
import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class EnergyCostOfLiving extends PlayerModule {

    private static final double cost = 0.5;
    private double energyRemoved = 0;
    
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
    
    ArrayList<ArrayList<String>> data;
    @Override
    public Object trackStatistics(){
        if(null == data)
            data = new ArrayList<>();
        else
            data.clear();
        data.add(new ArrayList<>());
        data.add(new ArrayList<>());
        
        data.get(0).add("Energy Removed");
        data.get(1).add(new DecimalFormat("#.#").format(energyRemoved));
        energyRemoved = 0;
        
        return data;
    }
}
