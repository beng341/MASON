package sim.app.evolutiongame;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * This will run one round of the simulation. That means matching players against
 * each other, having them play, and having the relevant effect on the population.
 * 
 * This class exists to give maximal control over how many games are played per
 * time unit, how players are matched, how population is affected, etc..
 * The alternative was to have each Player be Steppable but they would need to be
 * matched against other Steppable Players so only some of them would actually do
 * their real step() and others would just get dragged along and play in some other
 * Player's step() function.
 * @author Ben Armstrong
 */
public class GameRound implements Steppable
{

    @Override
    public void step(SimState state)
    {
        Population pop = (Population)state;
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
