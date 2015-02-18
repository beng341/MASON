package agents.io;


import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.Serializable;
import sim.util.Int2D;;

public final class TokenizeFile implements Serializable {
	
	StreamTokenizer st = null;
	FileReader rd = null;
	
	public final static  FileReader readTextFile(String fileName) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(fileName);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		return fileReader;
	}
	
	public final static void close(FileReader rd){
		try{
			rd.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	/*
	 * The creator method creates a stream of tokens with the desired properties
	 */
	
	public final static StreamTokenizer tokenizeFileStream(FileReader rd){
		StreamTokenizer st = null;
		if(rd != null){
			st = new StreamTokenizer(rd);
			 // Prepare the tokenizer for Java-style tokenizing rules
			if(st != null) {
				st.parseNumbers();
				st.wordChars('_', '_');
				st.eolIsSignificant(true); // can detect end of lines
				st.slashSlashComments(true); // makes "//" line ignored
		        st.slashStarComments(true);  // does /* ... */ comments ignored
			}
			else //st == null
			{
				System.err.println("StreamTokenizer st is null");
				System.exit(1);
			}
		}
		else {
			System.err.println("readTextFile rd is null");
			System.exit(1);
		}
		return st;
	}
	
	public final static String getNextString(StreamTokenizer st){
		String s = null;
		try {
			
			int token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				if(token == StreamTokenizer.TT_WORD){
						s = st.sval;
						break;
					}
				token = st.nextToken();
			}
										
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public final static double getNextDouble(StreamTokenizer st){
		double d = 0;
		try {
			
			int token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				if(token == StreamTokenizer.TT_NUMBER){
						d = st.nval;
						break;
					}
				token = st.nextToken();
			}
										
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public final static int getNextInt(StreamTokenizer st){
		return (int)getNextDouble( st);
	}
	
	public TokenizeFile(String filename){
		rd = readTextFile(filename);
		if(rd != null){
			st = new StreamTokenizer(rd);
			 // Prepare the tokenizer for Java-style tokenizing rules
			if(st != null) {
				st.parseNumbers();
				st.wordChars('_', '_');
				st.eolIsSignificant(true); // can detect end of lines
				st.slashSlashComments(true); // makes "//" line ignored
		        st.slashStarComments(true);  // does /* ... */ comments ignored
			}
			else //st == null
			{
				System.err.println("StreamTokenizer st is null");
				System.exit(1);
			}
		}
		else {
			System.err.println("readTextFile rd is null");
			System.exit(1);
		}
	}
	
	/**
	 * If we assume that the first to readable rows are rows and columns, then
	 * we should get the numbers and verify it or otherwise quit with an error message.
	 */
	
	public final static Int2D getRowsColumns (StreamTokenizer st){
		String word = "";
		double rows = -1;
		double columns = -1;
		try {
			int token = st.nextToken();
			while (token == StreamTokenizer.TT_EOL) // skip over the end of line token
				token = st.nextToken();
			
			if(token == StreamTokenizer.TT_WORD) {
				word = st.sval;
				if(!word.equalsIgnoreCase("rows")){
					System.err.println("rows not found, exit loading! " + st.sval);
					System.exit(1);
				}
			}
			token = st.nextToken();
			if(token == StreamTokenizer.TT_NUMBER) 
			 	rows = st.nval;
			 else {
			 	System.err.println("The number of rows was not found!" );
			 	System.exit(1);
			 	}
			
			token = st.nextToken();
			while (token == StreamTokenizer.TT_EOL) // skip over the end of line token
				token = st.nextToken();
			
			if(token == StreamTokenizer.TT_WORD) {
				word = st.sval;
				if(!word.equalsIgnoreCase("columns")){
					System.err.println("columns not found, exit loading!");
					System.exit(1);
					}
			}
			token = st.nextToken();
			if(token == StreamTokenizer.TT_NUMBER) {
				columns = st.nval;
				 }
			else {
				 System.err.println("The number of columns was not found!");
				 System.exit(1);
				}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Int2D((int)rows,(int)columns);
	}
	
	public  Int2D getRowsColumns (){
		String word = "";
		double rows = -1;
		double columns = -1;
		try {
			int token = st.nextToken();
			while (token == StreamTokenizer.TT_EOL) // skip over the end of line token
				token = st.nextToken();
			
			if(token == StreamTokenizer.TT_WORD) {
				word = st.sval;
				if(!word.equalsIgnoreCase("rows")){
					System.err.println("rows not found, exit loading! " + st.sval);
					System.exit(1);
				}
			}
			token = st.nextToken();
			if(token == StreamTokenizer.TT_NUMBER) 
			 	rows = st.nval;
			 else {
			 	System.err.println("The number of rows was not found!" );
			 	System.exit(1);
			 	}
			
			token = st.nextToken();
			while (token == StreamTokenizer.TT_EOL) // skip over the end of line token
				token = st.nextToken();
			
			if(token == StreamTokenizer.TT_WORD) {
				word = st.sval;
				if(!word.equalsIgnoreCase("columns")){
					System.err.println("columns not found, exit loading!");
					System.exit(1);
					}
			}
			token = st.nextToken();
			if(token == StreamTokenizer.TT_NUMBER) {
				columns = st.nval;
				 }
			else {
				 System.err.println("The number of columns was not found!");
				 System.exit(1);
				}
		
		} catch (IOException e) {
				e.printStackTrace();
		
		}
		
		return new Int2D((int)rows,(int)columns);
	}
	
}

