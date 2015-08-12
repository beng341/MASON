package sim.app.evolutiongame.modules.player.PlayGame;

import java.text.DecimalFormat;
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
    
    public static final String[] args = {"opponent", "payoff_matrix"};
    
    private final ArrayList<Integer> payoffs = new ArrayList<>();
    
    int gamesPlayed = 0;
    
    @Override
    public void run(Population state, Player p)
    {
        this.p = p;
        
        HashMap<String, Object> arguments = getArguments(args);
        if(null == arguments) {
            return;
        }
        
        Player opponent = (Player)arguments.get("opponent");
        
        //Set strategy for each player
        if(!opponent.hasVariable("strategy")){
            opponent.runModuleOutOfTurn("FindStrategy");
        }
        if(!p.hasVariable("strategy")){
            p.runModuleOutOfTurn("FindStrategy");
        }
        
        //check if we or the selected opponent have played already this timestep
        if(((Long)p.getVariable("last_played")) < state.schedule.getSteps() && 
                ((Long)opponent.getVariable("last_played")) < state.schedule.getSteps()){
            
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
            
            gamesPlayed++;
            
            //update the last_played time
            p.storeVariable("last_played", state.schedule.getSteps());
            opponent.storeVariable("last_played", state.schedule.getSteps());
        } else {
//            //already played, or the first round.
//            //If payoff does not exist yet, set it to be zero. This should only
//            //happen on the first round.
//            if(!p.hasVariable("payoff")){
//                p.storeVariable("payoff", 0);
//                noPayoffCount++;
//            }
//            if(!opponent.hasVariable("payoff")){
//                opponent.storeVariable("payoff", 0);
//                noPayoffCount++;
//            }
//            alreadyPlayedCount++;
        }
        if(p.hasVariable("payoff"))
            payoffs.add((int) p.getVariable("payoff"));
    }
    
    @Override
    public void cleanUp(Population pop){
        for(Player p: pop.getPlayers()){
            p.removeVariable("payoff");
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
        
        data.get(0).add("Games Played");
        data.get(1).add("" + gamesPlayed);
        gamesPlayed = 0;
        
        double avgPayoff = 0;
        for(int payoff: payoffs)
            avgPayoff += payoff;
        avgPayoff = avgPayoff / payoffs.size();
        payoffs.clear();
        
        if(Double.isNaN(avgPayoff)){
            data.get(0).add("Avg. Payoff");
            data.get(1).add("No payoffs");
        } else {
            data.get(0).add("Avg. Payoff");
            data.get(1).add(new DecimalFormat("#.##").format(avgPayoff));
        }
        
        return data;
    }
}
