package hawkdove_smaldino.io;

import java.io.*;

import hawkdove_smaldino.states.SimStateWithImage;


import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;


public class LoadTextImages implements java.io.Serializable {
	
	public final static int STRINGLENGTH = 255;
	public final static int MAX_VALUE = 255;
	public final static int INT_GRID = 0;
	public final static int DOUBLE_GRID = 1;
	public int type;
	public  int gridWidth = 464;
	public  int gridHeight = 672;
	public  IntGrid2D landscape;
	public 	DoubleGrid2D landscapeD;
	public String  fileName = "textImage.txt";
	
	public LoadTextImages(SimState state, String fileName, int type, int gridWidth,int gridHeight){
		this.fileName = fileName;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		if(type == INT_GRID){
			landscape = new IntGrid2D (gridWidth, gridHeight, 0);
			((SimStateWithImage)state).landscape = landscape;
		}
		else {
			landscapeD = new DoubleGrid2D (gridWidth, gridHeight, 0);
			((SimStateWithImage)state).landscapeD = landscapeD;
		}
		this.type = type;
		readImageTextFile(state, fileName);
	}
	
	public  void getTextIntegers(SimState state, String name, BufferedReader in) throws
	IOException {
		int numRows = 0;
		int numColumns = 0;
		String line;
		do {
			line = in.readLine();
			if (line != null)
			{				
				if(type == INT_GRID)
					numColumns = getIntegers(state,line,numRows);
				else
					numColumns = getDoubles(state,line,numRows);
				numRows++;
			}
		}
		while (line != null);
		//System.out.println(name + "\t" + numRows + "\t" + numColumns);
	}

	public  void readImageTextFile(SimState state, String fileName) {
		BufferedReader in = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			in = new BufferedReader(fileReader);
			getTextIntegers(state,fileName, in);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	public  int getIntegers(SimState state, String line, int numLines) {
		int numColumns = 0;
		int num = 0,i = 0;
		char[] word= new char[STRINGLENGTH];
		boolean prevWhitespace = true;
		String numString, subString;
		forLoop:
		for (int index = 0; index< line.length();index++) {
			char c = line.charAt(index);			
			boolean currWhitespace = Character.isWhitespace(c);
			if(i < STRINGLENGTH && !currWhitespace){
				word[i]=c;
				i++;
			}
			if(!prevWhitespace && (currWhitespace || line.length()-1 == index)){
				numString =  new String(word);
				subString = numString.substring(0,i);
				i = 0;
				try {					
					num = Integer.parseInt(subString);
				} catch (NumberFormatException e) {
					System.out.println(e);
					break forLoop;
				}
				if(numColumns > gridWidth || numLines > gridHeight){
					System.out.println("image outobounds: "+ "gridWidth " + gridWidth + "gridHeight "+ gridHeight);
					break forLoop;
				}		
				//System.out.println("y " +gridWidth + "  "+ gridHeight);
				//System.out.println("x " +numColumns + "  "+ numLines);
				landscape.set(numColumns,numLines, num);
				//System.out.println(numColumns + "  "+ numLines);
				numColumns++;
			}				
			prevWhitespace = currWhitespace;
		}
		return numColumns;
	}
	
	public  int getDoubles(SimState state, String line, int numLines) {
		int numColumns = 0;
		int i = 0;
		char[] word= new char[STRINGLENGTH];
		boolean prevWhitespace = true;
		String numString, subString;
		double num = 0;
		forLoop:
		for (int index = 0; index< line.length();index++) {
			char c = line.charAt(index);
			boolean currWhitespace = Character.isWhitespace(c);
			if(i < STRINGLENGTH && !currWhitespace){
				word[i]=c;
				i++;
			}
			if(!prevWhitespace && (currWhitespace || line.length()-1 == index)){
				numString =  new String(word);
				subString = numString.substring(0,i);
				i = 0;
				try {					
					num = Double.parseDouble(subString);
				} catch (NumberFormatException e) {
					System.out.println(e);
					break forLoop;
				}
				if(numColumns > gridWidth || numLines > gridHeight){
					System.out.println("image outobounds: "+ "gridWidth " + gridWidth + "gridHeight "+ gridHeight);
					break forLoop;
				}
				landscapeD.set(numColumns,numLines, num);
				numColumns++;
			}
			prevWhitespace = currWhitespace;
		}
		return numColumns;
	}
	
}