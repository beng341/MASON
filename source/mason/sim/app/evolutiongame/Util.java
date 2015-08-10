/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.evolutiongame;

import sim.app.evolutiongame.agents.Player;
import sim.field.grid.DenseGrid2D;
import sim.util.Bag;

/**
 *
 * @author armstrob
 */
public class Util {
    
    public static Pair<Integer, Integer> findPlayerLocation(DenseGrid2D grid, Player p)
    {
        try{
            final int width = grid.getWidth();
            final int height = grid.getHeight();
            Bag[] fieldx = null;
            for (int x = 0; x < width; x++)
            {
                fieldx = grid.field[x];
                for (int y = 0; y < height; y++)
                {
                    Bag bag = fieldx[y];
                    if(null == bag){
                        continue;
                    }
                    int len = bag.size();

                    for (int i = 0; i < len; i++)
                    {
                        Object obj = bag.get(i);
                        if (obj.equals(p))
                        {
                            return new Util.Pair<>(x, y);
                        }
                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        

        return null;
    }
    
    /**
     * A basic generic class for storing two values in a pair.
     * @param <X>
     * @param <Y> 
     */
    public static class Pair<X, Y>
    {
        private X x;
        private Y y;
        
        public Pair(X x, Y y)
        {
            this.x = x;
            this.y = y;
        }

        public X getFirst() {
            return x;
        }

        public void setFirst(X x) {
            this.x = x;
        }

        public Y getSecond() {
            return y;
        }

        public void setSecond(Y y) {
            this.y = y;
        }
        
        public boolean equals(Object o)
        {
            if(!o.getClass().equals(this.getClass()))
                return false;
            Pair<?,?> p = (Pair<?,?>)o;
            return p.getFirst().equals(this.getFirst()) && p.getSecond().equals(this.getSecond());
        }
        
        public String toString(){
            return "Pair: (" + x + " , " + y +" )";
        }
    }
}
