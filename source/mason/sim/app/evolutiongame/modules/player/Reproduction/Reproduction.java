package sim.app.evolutiongame.modules.player.Reproduction;

import java.util.HashMap;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;
import sim.app.evolutiongame.modules.player.PlayerModule;

/**
 *
 * @author Ben Armstrong
 */
public class Reproduction extends PlayerModule {
    
    private int numberOfBirths = 0;
    
    /**
     * A list of arguments required for this module to be run. If the player 
     * wishing to run this module does not have variables with these names, the
     * module will not be run.
     */
    public static final String[] args = {"payoff"};
    
    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        //1 - get arguments
//        HashMap<String, Object> arguments = getArguments(args);
//        if(null == arguments) {
//            return;
//        }
//        int payoff = (int) arguments.get("payoff"); 
//        
//        if(state.random.nextDouble() <= (p.birthRate+p.birthRateModifier*payoff)){
//            state.addPlayer(new Player(p, state));
//            numberOfBirths++;
//        }
    }
    
    @Override
    public Object trackStatistics(){
        String result = "Number of Births: " + numberOfBirths;
        numberOfBirths = 0;
        return result;
    }
}
