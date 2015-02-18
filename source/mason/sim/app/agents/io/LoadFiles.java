package agents.io;

import java.io.*;



public class LoadFiles implements java.io.Serializable {
	public static final int STRINGLENGTH = 255;
	public static final int ERROR = -1;
	public int numFiles;
	public int columns;
	public int rows;
	public double[][][] data = null; // we will create it when we read the first file;
	
	
	public LoadFiles(File[] names){
		numFiles = names.length;
		for(int i = 0; i< names.length;i++){
			readTextFile(names[i].getAbsolutePath(), i);
		}
	}
	
	public  void getTextDoubles(String name, BufferedReader in, int numFiles) throws
	IOException {
		int numRows = 0;
		int numColumns = 0;
		String line;
		int rows = readIndices(in.readLine());
		int columns = readIndices(in.readLine());
		if(data == null && rows != ERROR && columns != ERROR){
			this.columns = columns;
			this.rows = rows;
			data = new double[this.numFiles][rows][columns];			
		}
		if(rows == ERROR || columns == ERROR){
			System.out.println("rows " + rows + "columns " + columns);
			System.exit(0); //fatal error just quit
		}
			
		do {
			line = in.readLine();
			if (line != null)
			{				
				numColumns = getDoubles(line,numRows, numFiles);
				numRows++;
			}
		}
		while (line != null);
		System.out.println(name + "\t" + numRows + "\t" + numColumns);
	}

	public  void readTextFile(String fileName, int fileNum) {
		BufferedReader in = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			in = new BufferedReader(fileReader);
			getTextDoubles(fileName, in, fileNum);
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
	
	public int readIndices(String line){
		int num = -1;
		forLoop:
		for(int i=0;i<line.length();i++){
			char c = line.charAt(i);
			if(Character.isDigit(c)){
				String s = line.substring(i);
			
				try {	
					num = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					System.out.println(e);
					num = -1;					
					}
				break forLoop;
				}
		}			
		return num;
	}

	public  int getDoubles(String line, long numLines, int numFiles) {
		int numColumns = 0;
		double num = 0;
		int index = 0,i = 0;
		char[] word= new char[STRINGLENGTH];
		boolean prevWhitespace = true;
		String numString, subString;
		whileLoop:
		while (index < line.length()) {
			char c = line.charAt(index);
			index++;
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
					break whileLoop;
				}
				if(numColumns > this.columns || numLines > this.rows){
					System.out.println("outobounds: "+ "numFiles " + numFiles + " rows " + rows + " columns "+ columns);
					break whileLoop;
				}	
				data[numFiles][(int)numLines][(int)numColumns]= num;
				numColumns++;
			}
			prevWhitespace = currWhitespace;
		}
		return numColumns;
	}		
}