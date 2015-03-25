package hawkdove_smaldino.states;

import hawkdove_smaldino.io.LoadTextImages;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;

public class SimStateWithImage extends SimState {
	
	 public String projectName;
	 public Continuous2D primatesSpace = null;
	 public double discretization_size;
	 public ContinuousPortrayal2D agentsPortrayal = null;
	 public IntGrid2D landscape = null;
	 public DoubleGrid2D landscapeD = null;
	 public int gridWidth;
	 public int gridHeight;
	 public int textImageWidth;
	 public int textImageHeight;
	 public  LoadTextImages textImage;
	 public boolean loadimagefile;
	 public boolean imageInt;
	 public String textImageName;
	 
	 public String colorLow;
	 public String colorHigh;
	 public boolean gradientOrColorTable; //determines whether the image will be colored by a gradient
	 //or a color table.
	 public final static double MAXGRAYSCALE =255;
	 
	 public float red;                              
	 public float green;
	 public float blue;
	 public float alpha;
	 public double scale;
	 public String shape;

     /* 
      * By getting the size of the image on creation of the simulation state, we can make sure
      * that the image fits the simulation window and we don't have to specify its size in
      * advance.
      */
	 
	public SimStateWithImage(long seed) {
		super(seed);
		
	}

}
