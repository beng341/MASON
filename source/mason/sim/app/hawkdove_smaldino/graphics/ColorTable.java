package hawkdove_smaldino.graphics;

import java.awt.Color;
import sim.util.*;
/**
 * Makes a new color table for coloring pixels in a text image
 * @author jcschank
 *
 */
public class ColorTable {
	public final static int VALUE = 0;
	public final static int RED = 1;
	public final static int GREEN = 2;
	public final static int BLUE = 3;
	public final static int ALPHA = 4;

	public final static Color[] make(Bag table, int size, Color other){
		Color[] c = new Color[size];
		
		for(int i = 1; i< table.numObjs; i++){
			Bag row = (Bag)table.objs[i];
			int x = (int)((Double)(row.objs[VALUE])).doubleValue();
			float red = (float)((Double)(row.objs[RED])).doubleValue();
			float blue = (float)((Double)(row.objs[BLUE])).doubleValue();
			float green = (float)((Double)(row.objs[GREEN])).doubleValue();
			float alpha = (float)((Double)(row.objs[ALPHA])).doubleValue();
			c[x] = new Color(red,green,blue,alpha);
		}
		
		for(int i=0; i< size; i++) // for all other value use the default other
			if(c[i] == null)
				c[i] = other;
		
		return c;
	}
	
	public final static Color[] make(int value,int size, float red, float green, float blue,float alpha, Color other){
		Color[] c = new Color[size];
		
		c[value] = new Color(red,green,blue,alpha);
		
		for(int i=0; i< size; i++) // for all other value use the default other
			if(c[i] == null)
				c[i] = other;
		
		return c;
	}
	
	
}
