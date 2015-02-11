package sim.app.evolutiongame;

import sim.app.evolutiongame.Util.Pair;

/**
 * A static class to put all the payoff matrices in and associate them with a
 * game number. We're allowed to have two types of player so each type of game
 * needs to be given two payoff matrices even if they're the same matrix.
 * 
 * To add a game type here you also must add a static int field in Population 
 * and an entry in the domGameNumber() method also in Population class.
 * @author Ben Armstrong
 */
public class PayoffMatrices {
    
    private static Pair<int[][], int[][]> payoffs;
    
    public static void setGame(int gameNumber)
    {
        int[][] p1 = new int[][] {{}};
        int[][] p2 = new int[][] {{}};
        
        switch(gameNumber)
        {
            case 0:     //Rock Paper Scissors!
                p1 = new int[][] {
                    {1, 2, 0},
                    {0, 1, 2},
                    {2, 0, 1}
                };
                p2 = new int[][] {
                    {1, 2, 0},
                    {0, 1, 2},
                    {2, 0, 1}
                };
                break;
            case 1:     //Modified Rock Paper Scissors
                p1 = new int[][]
                {
                    {1, 5, 0},
                    {0, 1, 5},
                    {5, 0, 4}
                };
                p2 = new int[][]
                {
                    {1, 5, 0},
                    {0, 1, 5},
                    {5, 0, 4}
                };
                break;
            case 2:     //Entry Deterrence Game
                p1 = new int[][] {
                    {2, 0},
                    {1, 1}
                };
                p2 = new int[][] {
                    {2, 0},
                    {4, 4}
                };
                break;
            
            default:        //just give everyone a bit for trying..
                p1 = new int[][] {
                    {1}
                };
                p2 = new int[][] {
                    {1}
                };
                break;
        }
        
        payoffs = new Pair<>(p1, p2);
    }
    
    public static int[][] getPayoffMatrix(boolean firstStrategy)
    {
        if(firstStrategy)
            return payoffs.getFirst();
        else
            return payoffs.getSecond();
    }
    
    public static int getMaxNumStrategies()
    {
        int p1Max = Math.max(payoffs.getFirst().length, payoffs.getFirst()[0].length);
        int p2Max = Math.max(payoffs.getSecond().length, payoffs.getSecond()[0].length);
        
        return Math.max(p1Max, p2Max);
    }

}
