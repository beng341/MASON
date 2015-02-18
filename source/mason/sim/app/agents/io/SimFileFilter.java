package agents.io;

import java.io.File;
import java.io.FilenameFilter;


class SimFileFilter implements FilenameFilter{
	String fileType = "txt";
	public SimFileFilter(String filter){
		fileType =filter;
	}
	
	public boolean accept(File dir, String name) {
		
	     String subString;
		 int j = name.lastIndexOf(".");
		 subString = name.substring(j+1);
		 subString = subString.toLowerCase(); //for testing against file type
		 if(fileType.equals(subString))
			 return true;
		 else
			 return false;
	}
	
}