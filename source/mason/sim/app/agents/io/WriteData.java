package agents.io; 
import java.io.*;

public final class WriteData implements Serializable {
    public  RandomAccessFile file = null;
    public  String fileName = "";
    public  String folderName ="data";
    
    public void setFolderName(String folder){
        folderName = folder;
    }
    
    public WriteData (String fileName){
        this.fileName = fileName;
        File mfile = null;
         try{
             mfile =  new File("");
             String s = new String(mfile.getAbsolutePath()+File.separator +this.folderName);
             this.folderName = s;
             boolean mdir = new File(s).mkdir();
             this.fileName = folderName + File.separator + this.fileName;
         }
         catch (NullPointerException e) {
              System.out.println( e);
                             }        
     }

    public WriteData (String folderName, String fileName){
        this.fileName = fileName;
        this.folderName = folderName;
        File mfile = null;
         try{
                mfile =  new File("");
                String s = new String(mfile.getAbsolutePath()+File.separator +this.folderName);
                this.folderName = s;
                boolean mdir = new File(s).mkdir();
                this.fileName = this.folderName + File.separator + this.fileName;
         }
         catch (NullPointerException e) {
                System.out.println( e);
                             }  
     }
    
    public  void makePaths (String file){
        fileName = file;
        File mfile = null;
         try{
             mfile =  new File("");
             folderName = new String(mfile.getAbsolutePath() + File.separator + folderName);
             fileName = folderName + File.separator + fileName;
         }
         catch (NullPointerException e) {
              System.out.println( e);
                             }        
     }

    public  void makePaths (String folder, String file){
        fileName = file;
        folderName = folder;
        File mfile = null;
         try{
                mfile =  new File("");
                folderName = new String(mfile.getAbsolutePath()+File.separator + folderName);
                fileName = folderName + File.separator + fileName;
         }
         catch (NullPointerException e) {
                System.out.println( e);
                             }  
     }

    public boolean openFile (String f){
        boolean outcome = true;
        fileName = f;
        try{
             file =  new RandomAccessFile(fileName, "rw");
           } 
        catch (FileNotFoundException e) {
              System.out.println("IO error: " + e);
              outcome = false;
           }
       return outcome;  
    }

    public void writeInt (int x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            Integer i = new Integer(x);
            file.writeChars(i.toString());                 
            file.close();
              }catch (IOException e) {
                 System.out.println("IO error: "+ e);
              }
        }
        else
            System.out.println("File does not exit");                             
    }
    
     public void writeIntln (int x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            Integer i = new Integer(x);
            file.writeChars(i.toString()+"\n");                     
            file.close();
              }catch (IOException e) {
                System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }
     
    public void writeLong (long x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            Long i = new Long(x);
            file.writeChars(i.toString());                 
            file.close();
              }catch (IOException e) {
                  System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");
     }

    
     public void writeLongln (Long x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            Long i = new Long(x);
            file.writeChars(i.toString()+"\n");                     
            file.close();
              }catch (IOException e) {
                   System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }
     
    public void writeLineOfDouble(int presion, double[] nums){
    	String s ="";
    	String formatString = new String("%1." + presion + "f");
    	for(int i=0; i < nums.length;i++)
    		if(i<nums.length-1)
    			s = s + String.format(formatString,nums[i]) + "\t";
    		else
    			s = s + String.format(formatString,nums[i]) + "\n";
    	if(fileName.length() != 0){
    		openFile (fileName);
    	 try{
             file.skipBytes((int)file.length());
             file.writeChars(s);                          
             file.close();
               }catch (IOException e) {
                  System.out.println("IO error: "+ e);
                              }
         }
         else
             System.out.println("File does not exit");   
    	
    }

   
    public void writeDouble (int presion, double x){        
        if(fileName.length() != 0){
            openFile (fileName);
            String formatString = new String("%1." + presion + "f");
         try{
            file.skipBytes((int)file.length());
            file.writeChars(String.format(formatString,x));                          
            file.close();
              }catch (IOException e) {
                 System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }

    
    public void writeDoubleln (int presion, double x){        
        if(fileName.length() != 0){
            openFile (fileName);
            String formatString = new String("%1." + presion + "f");
         try{            
            file.skipBytes((int)file.length());
            file.writeChars(String.format(formatString,x)+"\n");                
            file.close();
              }catch (IOException e) {
                 System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");
    }

    
     public void writeString (String x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            file.writeChars(x);                        
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");
    }

    
    public void writeStringln (String x){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{            
            file.skipBytes((int)file.length());
            file.writeChars(x +"\n");                           
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }

    
    public void writeBytes (byte[] b){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{
            file.skipBytes((int)file.length());
            file.write(b);                    
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }

    public void writeBytesln (byte[] b){        
        if(fileName.length() != 0){
            openFile (fileName);
         try{            
            file.skipBytes((int)file.length());
            file.writeBytes(b +"\n");                           
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");                             
    }

    public void tab (){
        if(fileName.length() != 0){
            openFile (fileName);
         try{            
            file.skipBytes((int)file.length());
            file.writeChars("\t");                  
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");
    }

    public void newLine (){
        if(fileName.length() != 0){
            openFile (fileName);
         try{            
            file.skipBytes((int)file.length());
            file.writeChars("\n");                  
            file.close();
              }catch (IOException e) {
                             System.out.println("IO error: "+ e);
                             }
        }
        else
            System.out.println("File does not exit");
    }
}
