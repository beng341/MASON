package hawkdove_smaldino.io;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileReader;
import java.io.StreamTokenizer;
import sim.util.*;

public final class LoadScript implements Serializable{
	public final static String KEYWORD = "RUNS_FILE_LIST";
	
	public final static  Bag load(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		Bag varibleNames = new Bag();
		Bag values = new Bag();
		
		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		try {
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD )
					varibleNames.add(st.sval);
				token = st.nextToken();
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD)
					values.add(st.sval);
				else if(token == StreamTokenizer.TT_NUMBER)
					values.add(st.nval);
				token = st.nextToken();
					}					
			
			} catch (IOException e) {
				System.err.println("st.nextToken() error in load Script: " + e);
			}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		Bag b = new Bag();
		b.add(varibleNames);
		b.add(values);
		return b;	
	}
	
	public final static  Bag loadAutoScript(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		Bag varibleNames = new Bag();
		Bag values = new Bag();
		Bag fileNames = new Bag();
		boolean getFileNames = false;
		
		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		try {
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD && KEYWORD.equalsIgnoreCase(st.sval)){
					getFileNames = true;
					token = st.nextToken();
					while(token == StreamTokenizer.TT_EOL) //move through end of lines
						token = st.nextToken();
				}
				if(!getFileNames && token == StreamTokenizer.TT_WORD)
					varibleNames.add(st.sval);
				else if(token == StreamTokenizer.TT_WORD)
					fileNames.add(st.sval);
				token = st.nextToken();
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(!getFileNames && token == StreamTokenizer.TT_WORD)
					values.add(st.sval);
				else if(token == StreamTokenizer.TT_WORD)
					fileNames.add(st.sval);
				token = st.nextToken();
					}					
			
			} catch (IOException e) {
				System.err.println("st.nextToken() error in load Script: " + e);
			}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		Bag b = new Bag();
		b.add(varibleNames);
		b.add(values);
		b.add(fileNames);
		return b;	
	}
	public final static  void printBag(Bag b) {
		for(int i=0;i<b.numObjs;i++){
			Bag bi = (Bag)b.objs[i];
			for(int j=0;j<bi.numObjs;j++)
				System.out.println(bi.objs[j]);
			System.out.println();
		}
	}
	
	public final static  void print2Bags(Bag b) {
		Bag b1 = (Bag)b.objs[0];
		Bag b2 = (Bag)b.objs[1];
		for(int j=0;j<b1.numObjs;j++)
			System.out.println(b1.objs[j]+"  "+b2.objs[j]);
		System.out.println();
	}
	
	public final static File[] checkforfiles(String folderName){
		File [] f = null;
		System.out.println(folderName);
		f = CopyFolder.getFileNames(folderName);
		System.out.println("f: " + f);
		if(f != null)
			for(int i=0;i<f.length;i++)
				System.out.println(f[i]);
		return f;
	}
}
