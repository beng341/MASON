package sim.app.evolutiongame.modules.player.UsePayoff;

import java.util.ArrayList;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class EnergyFromPayoff extends PlayerModule {
    
    int amountGiven = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        if(p.hasVariable("payoff")){
            int payoff = (int) p.getVariable("payoff");
            amountGiven += payoff;
            if(p.hasVariable("energy") && (double)p.getVariable("energy") < 150){
                double energy = (double) p.getVariable("energy");
                p.storeVariable("energy", energy+payoff);
            } else if(!p.hasVariable("energy")) {
                p.storeVariable("energy", state.random.nextInt(50)+1.0+payoff);
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
        
        data.get(0).add("Energy Given");
        data.get(1).add(""+amountGiven);
        amountGiven = 0;
        
        return data;
    }

}
