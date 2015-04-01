package agents.io;

import java.io.File;
import java.io.FilenameFilter;


public class CopyFolder {
	public  String firstFolder ="";
	public String secondFolder = "";
	
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
	
	public static String makePath(String path, String file){
	       return new String(path + File.separator + file);
	}
	
	public static String makePath(String path, String name1, String name2){
	       return new String(path + File.separator + name1 + File.separator + name2);
	}
	
	public static  String makePath (String folderName){
        File mfile = null;
        String path = "";
         try{
             mfile =  new File("");
             path = new String(mfile.getAbsolutePath() + File.separator + folderName);
         }
         catch (NullPointerException e) {
              System.out.println( e);
                             }  
         return path;
     }
	
	public static  String makePathFile (String folderName, String fileName){
        File mfile = null;
        String path = "";
         try{
             mfile =  new File("");
             path = new String(mfile.getAbsolutePath() + File.separator + folderName+ File.separator + fileName);
         }
         catch (NullPointerException e) {
              System.out.println( e);
                             }  
         return path;
     }
	
	public static File[] getFileNames(String folder) {
		File myDir = new File(folder);
		File[] files = null;
		if( myDir.exists() && myDir.isDirectory()){
			files = myDir.listFiles(new TextFileFilter());
		}
			return files; //file names
	}
	
	public static File[] getFileNames(String folder, String filter) {
		File myDir = new File(folder);
		File[] files = null;
		if( myDir.exists() && myDir.isDirectory()){
			files = myDir.listFiles(new SimFileFilter(filter));
		}
			return files; //file names
	}
	
	public static void makeFolder (String folderPath){
		boolean mdir = new File(folderPath).mkdir();
	}
	
	public static void copyFilesToFolder(String firstFolderPath,String secondFolderPath, File[] files) {
		for(int i=0;i<files.length;i++)
			CopyFile.copy(firstFolderPath + File.separator + files[i].getName(),secondFolderPath +  File.separator + files[i].getName());
	}
	
	public static void copy (String firstFolder, String secondFolder){
		String  firstFolderPath = makePath (firstFolder);
		String  secondFolderPath = makePath (secondFolder);
		File[] files = getFileNames(firstFolderPath);
		makeFolder (secondFolder);
		copyFilesToFolder(firstFolderPath,secondFolderPath, files);
	}
	
	public static void copy (String firstFolder, String secondFolder,String filter){
		String  firstFolderPath = makePath (firstFolder);
		String  secondFolderPath = makePath (secondFolder);
		File[] files = getFileNames(firstFolderPath,filter);
		makeFolder (secondFolder);
		copyFilesToFolder(firstFolderPath,secondFolderPath, files);
	}
	
}
