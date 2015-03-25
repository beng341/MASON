package hawkdove_smaldino;
import sim.field.grid.IntGrid2D;


/**
 * This will hold all the information for constructing various wall types.
 */


public class Wall {
	public int gridWidth;
	public int gridHeight;
	
	public Wall(int gridWidth, int gridHeight){
		this.gridHeight = gridHeight;
		this.gridWidth = gridWidth;
	}
	
	//make a plus maze in the center of the grid
	public IntGrid2D makeWall(int length, int breadth){
		IntGrid2D wallGrid = new IntGrid2D(gridWidth, gridHeight, 0);
		if(length > gridWidth)
			length = gridWidth - 2;
		if (breadth > length)
			breadth = (int)(length/2);
		
		final int startx = (int)(gridWidth/2) - (int)(length/2);
		final int starty = (int)(gridHeight/2) - (int)(length/2);
		final int left = (int)(gridWidth/2) - (int)(breadth/2);
		final int right = (int)(gridWidth/2) + (int)(breadth/2);
		final int up = (int)(gridHeight/2) + (int)(breadth/2);
		final int down = (int)(gridHeight/2) - (int)(breadth/2);
		final int startGap = (int)(length/2) - (int)(breadth/2);
		final int endGap = (int)(length/2) + (int)(breadth/2);
		
		for(int i = 0; i < length; i++){
			if(i <= startGap || i >= endGap){	
				wallGrid.set(left, i + starty, 1);//left column			
				wallGrid.set(right, i + starty, 1);//right column			
				wallGrid.set(i + startx, up, 1);//up row			
				wallGrid.set(i + startx, down, 1);//down row
			}
		}
		return wallGrid;	
	}
	
	//make a diagonal of small walls to go across the screen
	public IntGrid2D makeBarrier(){
		IntGrid2D wallGrid = new IntGrid2D(gridWidth, gridHeight, 0);
		
		int length = 7;//(int)(gridWidth/(numPlus + 1));
		int numPlus = (int)(gridWidth/(length-1));
		int breadth = 3;
		
		int center = (int)(length/2) + 1;
		int startx, starty, up, down, left, right;
		final int startGap = (int)(length/2) - (int)(breadth/2);
		final int endGap = (int)(length/2) + (int)(breadth/2);
		for(int j = 0; j < numPlus; j++){
			
			if(center > gridWidth - (int)(length/2))
				break;
			startx = center - (int)(length/2);
			starty = center - (int)(length/2);
			left = center - (int)(breadth/2);
			right = center + (int)(breadth/2);
			up = center + (int)(breadth/2);
			down = center - (int)(breadth/2);
			
			for(int i = 0; i < length; i++){
				if(i <= startGap || i >= endGap){	
					wallGrid.set(left, i + starty, 1);//left column			
					wallGrid.set(right, i + starty, 1);//right column			
					wallGrid.set(i + startx, up, 1);//up row			
					wallGrid.set(i + startx, down, 1);//down row
				}
			}
			center += length - 1;
		}
		return wallGrid;
	}
	
	
}//end class
