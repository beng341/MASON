package sim.app.evolutiongame;

import java.util.ArrayList;

/**
 * This class will hold a payoff matrix/payoff matrices for a two player game.
 * As well, it will have some helper methods to find useful facts such as the
 * set of Nash Equilibrium in a game.
 * @author Ben Armstrong
 */
public class Game
{
    private ArrayList<int[][]> payoffs;
    
    /**
     * 
     * @param payoffs List of payoff matrices, one for each player.
     */
    public Game(ArrayList<int[][]> payoffs)
    {
        this.payoffs = payoffs;
    }
    
    
}
