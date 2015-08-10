package sim.app.evolutiongame.modules.player.SetupStrategyRatios;

import sim.app.evolutiongame.PayoffMatrices;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class EqualCounts extends PlayerModule{

    @Override
    public void run(Population state, Player p)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Set strategies so that there are approximately an equal number of players
     * with each strategy.
     * @param pop
     * @param p 
     */
    @Override
    public void setup(Population pop, Player p){
        int number_strategies = PayoffMatrices.getMaxNumStrategies();
        if(number_strategies > 1){
            p.storeVariable("strategy", p.id%number_strategies);
        } else {
            p.storeVariable("strategy", 0);
        }
    }
}
