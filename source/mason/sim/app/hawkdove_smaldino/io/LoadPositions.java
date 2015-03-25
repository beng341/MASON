package hawkdove_smaldino.io;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileReader;
import java.io.StreamTokenizer;
import sim.util.*;

public final class LoadPositions implements Serializable{
	public final static int POSITIONSCOLUMNS = 0;
	public final static int X_COLUMN = 1;
	public final static int Y_COLUMN = 2;
	
	public final static  Bag[] load(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		int sets;
		Bag[] positions = null;
		
		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		String s = TokenizeFile.getNextString(st);
		if(!s.equalsIgnoreCase("sets"))
			System.err.println("sets not found, exit loading! " + st.sval);
		sets =  TokenizeFile.getNextInt(st);
		Int2D rc = TokenizeFile.getRowsColumns(st);
		if(rc.x > 0 && rc.y > 0 ){
			positions = new Bag[sets];
			for(int i=0;i<sets;i++)
				positions[i]= new Bag();
		}

		if(positions != null){
			
			try {
				int r = 0;
				int cl = 0;
				int m = 0;
				int x=0, y=0;
				//we need to get the column headers
				int token = st.nextToken();
				while(token != StreamTokenizer.TT_EOF){
					while(token == StreamTokenizer.TT_EOL) //move through end of lines
						token = st.nextToken();
					
					while(token != StreamTokenizer.TT_EOL && token != StreamTokenizer.TT_EOF){
						if(cl > Y_COLUMN){
							System.err.println("Too many colums  at line: " + r);
							System.exit(1);
						}						
						else { // for all other parameters, which are doubles
							if(token == StreamTokenizer.TT_NUMBER){
								if(cl == X_COLUMN)
									x = (int)st.nval;
								else if(cl == Y_COLUMN){
									y = (int)st.nval;
									((Bag)positions[m]).add(new Int2D(x,y));
								}									
							}
						}				
						
						cl++;
						if(token != StreamTokenizer.TT_EOF)
							token = st.nextToken();
					}
					r++;
					if(r > rc.x){
						r = 0; // if there is more than one set
						m++;
					}
					cl = 0;
				}
			
			} catch (IOException e) {
				System.err.println("st.nextToken() error in load Chromosome: " + e);
			}
		}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		
		return positions;
	}

}
