package sim.app.evolutiongame.modules.PlayGame;

import java.util.ArrayList;
import java.util.HashMap;
import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 * The PlayGame module represents the action of an agent playing a game with zero
 * or more of the other agents in the 
 * 
 * This class represents the default behaviour of the PlayGame module.
 * @author Ben Armstrong
 */
public class PlayGame extends Module {
    
    @Override
    public void run(Population state, Player p) {
        
        HashMap<String, Object> args = getArguments();
        
        Player opponent = ((ArrayList<Player>)args.get("opponent")).get(0);
        
        
    }
}