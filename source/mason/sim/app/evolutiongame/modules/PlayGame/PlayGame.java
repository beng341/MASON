package sim.app.evolutiongame.modules.PlayGame;

import java.util.ArrayList;
import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 * The PlayGame module represents the action of an agent playing a game with zero
 * or more of the other agents in the population.
 * 
 * This class represents the default behaviour of the PlayGame module. It will
 * play a game against one other player, and set the payoff for that player.
 * @author Ben Armstrong
 */
public class PlayGame extends Module {
    
    public static final String[] args = {"potential_opponents"};

    public static Object run(Population state, Player p, Object args) {
        
        HashMap<String, Object> args = getArguments();
        
        Player opponent = ((ArrayList<Player>)args.get("opponent")).get(0);
        
        //check if we or the selected opponent have played already this timestep
        if(p.hasVariable("last_played") && 
                ((Double)p.getVariable("last_played")) < state.schedule.getTime() && 
                opponent.hasVariable("last_played") && 
                ((Double)opponent.getVariable("last_played")) < state.schedule.getTime()){
            //get payoff from this player against the opponent
            
            int[][] my_matrix = (int[][]) p.getVariable("payoff_matrix");
            int[][] opp_matrix = (int[][]) opponent.getVariable("payoff_matrix");
            
            if(!opponent.hasVariable("strategy")){
                opponent.runModule("FindStrategy");
            }
            
        } else {
            //already played, payoff should be set so do nothing
        }
    }
    
    private int getPayoff(int[][] myMatrix, int[][] oppMatrix) {
        myMatrix[getStrategy()][opponent.strategy]
    }

    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        this.arguments = new String[]{"potential_opponents"};
        //1 - get arguments
        HashMap<String, Object> args = getArguments();
        if(null == args) {
            return;
        }
    }
}
