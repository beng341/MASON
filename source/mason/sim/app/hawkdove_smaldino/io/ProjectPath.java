package hawkdove_smaldino.io;

import java.io.File;
import java.io.FilenameFilter;


public class ProjectPath {
	
	public static String getAbsolutePath(){
		 File mfile = null;
	        String path = "";
	         try{
	             mfile =  new File("");
	             path = mfile.getAbsolutePath();
	         }
	         catch (NullPointerException e) {
	              System.out.println( e);
	                             }  
	         return path;
		
	}
	
	/**
	 * This method takes a project name string and convert it to a path
	 * name.
	 * @param projectName
	 * @return
	 */
	
	public static String projectToPath(String projectName){		
		return projectName.replace(".", File.separator);
	}
	
	public static String relativePath(String projectName, String filename){
		String s = projectToPath(projectName);
	    return new String(s + File.separator + filename);
	}
	
	public static String absolutePath(String projectName, String filename){
		String a = getAbsolutePath();
		String s = projectToPath(projectName);
	     return new String(a + File.separator + s + File.separator + filename);
	}
	
}
