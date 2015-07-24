package sim.app.evolutiongame.modules.PlayGame;

import sim.app.evolutiongame.Player;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class PlayGameMultiPlayer extends Module {

    @Override
    public void run(Population state, Player p) {
        this.p = p;
        this.arguments = new String[]{};
    }
}