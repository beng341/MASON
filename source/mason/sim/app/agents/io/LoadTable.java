package agents.io;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileReader;
import java.io.StreamTokenizer;
import sim.util.*;

/**
 * This class loads a file that contains a table of data.  It assumes a column header for each
 * column of data.  It also includes a test to determine if there is a value that corresponds to
 * each column header for each row.  No assumptions are made about the data in the table and
 * so a bag containing each row of the table as a bag is returned. The first bag is the headers.
 * The test method test each subsequent bag to see if it is the same length as the header bag.
 * If the table is empty it also returns false.  It returns false if the bag only has a header
 * row.
 * @author jcschank
 *
 */
public final class LoadTable implements Serializable{
	
	public final static  Bag load(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		Bag table = new Bag();
		Bag abag = new Bag();
		
		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);

		try {
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
					while(token == StreamTokenizer.TT_EOL) {
						if(abag.numObjs > 0)
							table.add(abag);
						abag = new Bag();					
						token = st.nextToken();
					}
					if(token == StreamTokenizer.TT_WORD){
						abag.add(st.sval);
					}
					while(token == StreamTokenizer.TT_EOL) {
						if(abag.numObjs > 0)
							table.add(abag);
						abag = new Bag();
						token = st.nextToken();
					}
					if(token == StreamTokenizer.TT_NUMBER){
						abag.add(st.nval);
					}
					
					if(token == StreamTokenizer.TT_EOF) { // test to see if we are at the EOF and need to save the bag
						if(abag.numObjs > 0)
							table.add(abag);
					}
					
					token = st.nextToken();
					
					if(token == StreamTokenizer.TT_EOF) {// test to see if we are at the EOF and need to save the bag
						if(abag.numObjs > 0)
							table.add(abag);
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
		return table;	
	}
	
	/**
	 * Test a Bag table to make sure the number of values matches the headers (first bag).
	 * If not, it returns false, else true. A table has to have a row of headers and at
	 * least one row of values.
	 * @param t
	 * @return
	 */
	public final static boolean testTable (Bag t){
		
		if(t == null || t.numObjs < 2) // must be at least one row of values
			return false;
		else {
			Bag headers = (Bag)t.objs[0];
			for(int i = 1; i<t.numObjs;i++){
				Bag b = (Bag)t.objs[i];
				if(b.numObjs != headers.numObjs){
					System.out.println(headers.numObjs + "  "+ b.numObjs+ "  "+ i);
					return false; // return false and the function returns false
				}
			}
			return true; // Must be a table
		}	
		
	}
	
	public static void printTable(Bag t){
		for(int i=0; i< t.numObjs;i++){
			Bag temp = (Bag)t.objs[i];
			for(int j=0;j<temp.numObjs;j++)
				System.out.print(temp.objs[j]+ " ");
			System.out.println();
		}
	}

}
