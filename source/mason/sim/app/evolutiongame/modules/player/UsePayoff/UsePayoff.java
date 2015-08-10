package sim.app.evolutiongame.modules.player.UsePayoff;

import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class UsePayoff extends PlayerModule{

    public static final String[] args = {"payoff"};
    
    @Override
    public void run(Population state, Player p)
    {
        (new EnergyFromPayoff()).run(state, p);
    }

}
