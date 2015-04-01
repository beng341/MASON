package agents.io;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileReader;
import java.io.StreamTokenizer;
import sim.util.*;

/**
 * This class counts the size of a text image.  It assumes the file is a columns x rows
 * array of numbers.
 * @author jcschank
 *
 */
public final class CountRowColumns implements Serializable{
	
	public final static  Int2D load(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		int rows = 0;
		int columns = 0;
		boolean columnsDone = false;
		
		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);

		try {
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) {
					if(columns > 0){
						columnsDone = true;	
						rows++;
					}
					token = st.nextToken();
				}
				if(!columnsDone)
					columns++;					
				token = st.nextToken();
				if(token == StreamTokenizer.TT_EOF){
					if(columns > 0){
						columnsDone = true;	
						rows++;
					}
				}
				} // end while(token != StreamTokenizer.TT_EOF)				
			
			} catch (IOException e) {
				System.err.println("st.nextToken() error in load Chromosome: " + e);
			}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		return new Int2D(columns,rows);	
	}
	


}
