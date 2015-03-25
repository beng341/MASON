package hawkdove_smaldino.graphics;

import java.awt.Color;

public final class ColorPicker {
	
	public static Color getColor(String s){
		Color c = Color.gray; //default
		String color = s.toLowerCase(); //make sure it is lower case for testing
		if(color.equals("green"))
			c = Color.green;
		else if(color.equals("yellow"))
			c = Color.yellow;
		else if(color.equals("magenta"))
			c = Color.magenta;
		else if (color.equals("lightgrey"))
			c = Color.lightGray;
		else if(color.equals("black"))
			c = Color.black;
		else if (color.equals("white"))
			c = Color.white;
		else if (color.equals("red"))
			c = Color.red;
		else if (color.equals("grey"))
			c = Color.gray;
		else if (color.equals("blue"))
			c = Color.blue;
		else if(color.equals("cyan"))
			c = Color.cyan;
		else if (color.equals("darkgray"))
			c = Color.darkGray;
		else if (color.equals("pink"))
			c = Color.pink;
		return c;
	}

}
