/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.evolutiongame;

/**
 *
 * @author armstrob
 */
public class Util {
    
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
            if(p.getFirst().equals(this.getFirst()) && p.getSecond().equals(this.getSecond()))
                return true;
            return false;
        }
    }
}
