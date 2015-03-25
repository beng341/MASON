package hawkdove_smaldino.io;

import java.io.File;
import java.io.FilenameFilter;


class TextFileFilter implements FilenameFilter{
	
	public boolean accept(File dir, String name) {
	     String fileType = "txt";
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