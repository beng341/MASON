package sim.app.evolutiongame.modules.FindStrategy;

import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import static sim.app.evolutiongame.modules.PlayGame.PlayGame.args;

/**
 * 
 * Finds a random strategy for this player to use. 
 * Uses the player's "payoff_matrix" and stores "strategy" as a result.
 * @author Ben Armstrong
 */
public class RandomStrategy extends FindStrategy{
    
    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        HashMap<String, Object> arguments = getArguments(args);
        int[][] my_matrix = (int[][]) p.getVariable("payoff_matrix");
        
        //faster to just clear the map than create a new one
        arguments.clear();
        arguments.put("strategy", state.random.nextInt(my_matrix.length));
        saveResults(arguments);
    }
}
