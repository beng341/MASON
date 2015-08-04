package sim.app.evolutiongame.modules.player.PlayGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import sim.app.evolutiongame.agents.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;
import sim.app.evolutiongame.modules.player.PlayerModule;
import static sim.app.evolutiongame.modules.player.Reproduction.Reproduction.args;

/**
 * The PlayGame module represents the action of an agent playing a game with zero
 * or more of the other agents in the population.
 * 
 * This class represents the default behaviour of the PlayGame module. It will
 * play a game against one other player, and set the payoff for that player.
 * @author Ben Armstrong
 */
public class PlayGame extends PlayerModule {
    
    public static final String[] args = {"opponents", "payoff_matrix"};
    
    private final ArrayList<Integer> payoffs = new ArrayList<>();
    private int noPayoffCount = 0;
    private int alreadyPlayedCount = 0;

    @Override
    public void run(Population state, Player p) {
        this.p = p;
        
        HashMap<String, Object> arguments = getArguments(args);
        if(null == arguments) {
            return;
        }
        
        Player opponent = ((ArrayList<Player>)arguments.get("opponents")).get(0);
        
        //Set strategy for each player
        if(!opponent.hasVariable("strategy")){
            opponent.runModuleOutOfTurn("FindStrategy");
        }
        if(!p.hasVariable("strategy")){
            p.runModuleOutOfTurn("FindStrategy");
        }
        
//        //get payoff from this player against the opponent
//        int[][] my_matrix = (int[][]) arguments.get("payoff_matrix");
//        int[][] opp_matrix = (int[][]) opponent.getVariable("payoff_matrix");
//
//        //Find each players strategy
//        int my_strategy = (int) p.getVariable("strategy");
//        int opp_strategy = (int) opponent.getVariable("strategy");
//
//        //find and store each players payoff
//        int my_payoff = my_matrix[my_strategy][opp_strategy];
//        int opp_payoff = opp_matrix[opp_strategy][my_strategy];
//        p.storeVariable("payoff", my_payoff);
//        opponent.storeVariable("payoff", opp_payoff);
//
//        //update the last_played time
//        p.storeVariable("last_played", state.schedule.getTime());
//        opponent.storeVariable("last_played", state.schedule.getTime());
        
        //check if we or the selected opponent have played already this timestep
        if(((Double)p.getVariable("last_played")) < state.schedule.getTime() && 
                ((Double)opponent.getVariable("last_played")) < state.schedule.getTime()){
            
            //get payoff from this player against the opponent
            int[][] my_matrix = (int[][]) arguments.get("payoff_matrix");
            int[][] opp_matrix = (int[][]) opponent.getVariable("payoff_matrix");
            
            //Find each players strategy
            int my_strategy = (int) p.getVariable("strategy");
            int opp_strategy = (int) opponent.getVariable("strategy");
            
            //find and store each players payoff
            int my_payoff = my_matrix[my_strategy][opp_strategy];
            int opp_payoff = opp_matrix[opp_strategy][my_strategy];
            p.storeVariable("payoff", my_payoff);
            opponent.storeVariable("payoff", opp_payoff);
            
            //update the last_played time
            p.storeVariable("last_played", state.schedule.getTime());
            opponent.storeVariable("last_played", state.schedule.getTime());
        }
        else {
            //already played, or the first round.
            //If payoff does not exist yet, set it to be zero. This should only
            //happen on the first round.
            if(!p.hasVariable("payoff")){
                p.storeVariable("payoff", 0);
                noPayoffCount++;
            }
            if(!opponent.hasVariable("payoff")){
                opponent.storeVariable("payoff", 0);
                noPayoffCount++;
            }
            alreadyPlayedCount++;
        }
        if(p.hasVariable("payoff"))
            payoffs.add((int) p.getVariable("payoff"));
    }
    
    @Override
    public Object trackStatistics(){
        double avgPayoff = 0;
        for(int payoff: payoffs)
            avgPayoff += payoff;
        avgPayoff = avgPayoff / payoffs.size();
        payoffs.clear();
        
        String noPayoff = "Players that stored 0 for payoff: " + noPayoffCount;
        String alreadyPlayed = "# of players that already played: " + (alreadyPlayedCount);
        
        noPayoffCount = 0;
        alreadyPlayedCount = 0;
        
        return noPayoff + "\n"
                + alreadyPlayed + "\n"
                + "Average Payoff: " + avgPayoff;
    }
}
