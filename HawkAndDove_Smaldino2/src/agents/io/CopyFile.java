package agents.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class CopyFile {
   /**
    * Copies the content of one foler to a new one and used a file filter.
    * @param fileIn
    * @param fileOut
    * @throws IOException
    */
	public final static void docopy(String fileIn, String fileOut) 
		throws IOException{
		File inputFile = new File(fileIn);
		File outputFile = new File(fileOut);

		FileReader in = new FileReader(inputFile);
		FileWriter out = new FileWriter(outputFile);
		int c;

		while ((c = in.read()) != -1)
			out.write(c);

		in.close();
		out.close();
	} 
	
	public final static void copy(String fileIn, String fileOut) {
	try{
	    docopy(fileIn, fileOut);
	    }catch (IOException e) {
			System.err.println("file copy error");
		}
	} 
}