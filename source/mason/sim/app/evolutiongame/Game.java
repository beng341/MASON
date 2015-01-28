package sim.app.evolutiongame;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.HashSet;
import sim.app.evolutiongame.Util.Pair;

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
    
    
    /**
     * 
     * @param payoffs A list of payoff matrices. One for each player.
     */
    public Game(ArrayList<int[][]> payoffs)
    {
        this.payoffs = payoffs;//should do some validation to ensure matrices are good sizes
    }
    
    public HashSet<Pair<Integer, Integer>> getNashEquilibria()
    {
        if(payoffs.size() != 2)
            return new HashSet<Pair<Integer, Integer>>();
        else if(equilibria != null)
            return equilibria;
        
        int[][] payoff1 = payoffs.get(0);//payoff matrix for player 1
        int[][] payoff2 = payoffs.get(1);//payoff matrix for player 2
        
        //potential Nash Equilibria for players 1 and 2
        HashSet<Pair<Integer, Integer>> potential1 = new HashSet<>();
        HashSet<Pair<Integer, Integer>> potential2 = new HashSet<>();
        
        for(int i = 0; i < payoff1.length; ++i)//for each strategy of player 2
        {
            //get best reply of player 1
            int best = findMax(payoff1[i]);
            //save (i, best) as potential Nash Equilibrium
            potential1.add(new Pair<>(i, best));
        }
        for(int i = 0; i < payoff2.length; ++i)//for each strategy of player 1
        {
            //get best reply of player 2
            int best = findMax(payoff1[i]);
            //save (i, best) as potential Nash Equilibrium
            potential2.add(new Pair<>(i, best));
        }
        potential2.retainAll(potential1);//get intersection of two sets
        
        this.equilibria = potential2;
        return equilibria;
    }
    
    private int findMax(int[] a)
    {
        int currentMax = a[0];
        for(int i = 0; i < a.length; ++i)
            if(a[i] > currentMax)
                currentMax = a[i];
        return currentMax;
    }
}
