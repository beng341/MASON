package hawkdove_smaldino; 

import java.io.File;
import java.lang.reflect.Field;
import hawkdove_smaldino.control.SimController;
import hawkdove_smaldino.states.SimStateWithSimController;
import sim.display.Display2D;
import sim.engine.SimState;


public class HandleData implements java.io.Serializable {

	WriteData write;     
	SimController sc;
	public Display2D display = null;
	public String precision= "%.4f";
        private static final long serialVersionUID = 1L;

	/** This code should be reused for each simulation **/

	public HandleData(SimState state, String folderName, String fileName, boolean saveToFile,boolean savePNGToFile,SimController sc, String precision){

		if(saveToFile || savePNGToFile)
			write = new WriteData(folderName, fileName);
		this.sc = sc;
		this.precision = precision;
		SimStateWithSimController ssc = (SimStateWithSimController)state;
		if(ssc.display != null)
			this.display = ssc.display;
		else
			System.out.println("sc is Null!");
	}
	
	public void takeSnapshot(String folderName, String filename) {
		if(display != null){
			String s = write.folderName+File.separator+folderName;
			s = WriteData.makeFolder(s);
			String fn = s + File.separator + filename;
			//System.out.println(fn);
			//display.takeSnapshot(fn);
		}
	}

	public void printPramsToConsole(SimState state){
		Field f = null;
		Class a =  state.getClass(); // gets the runtime class
		Field[] x = a.getDeclaredFields(); // gets the declared fields, which we will use 
		// to print out the parameter values
		String b = " ";

		for(int i=0;i<x.length;i++)
			try{                        //there can be errors that we must catch

				b = x[i].toString();
				int j = b.lastIndexOf(".");
				b = b.substring(j+1);
				b = b + "  " + x[i].get(state).toString();
				f = x[i];
				Class cp = f.getType();
				if(cp.equals(int.class) || cp.equals(double.class)|| cp.equals(float.class)
						|| cp.equals(boolean.class) || cp.equals(short.class) || cp.equals(String.class)
						|| cp.equals(long.class) || cp.equals(char.class) || cp.equals(byte.class))
					System.out.println(b);
			}


		catch(NullPointerException e){
			//System.out.println("NullPointerException; " + e + " " + f);

		}
		catch(IllegalArgumentException e){
			System.out.println("IllegalArgumentException; " + e);
		}

		catch (IllegalAccessException e){
			System.out.println("IllegalAccessException; " + e);
		}
	}


	public void printPramsToFile(SimState state){
		String b;
		Field f = null;
		Class a =  state.getClass(); // gets the runtime class
		Field[] x = a.getDeclaredFields(); // gets the declared fields, which we will use                                                    // to print out the parameter values
		for(int i=0;i<x.length;i++)
			try{                        //there can be errors that we must catch
				b = x[i].toString();
				int j = b.lastIndexOf(".");
				b = b.substring(j+1);
				b = b + "  " + x[i].get(state).toString();
				f = x[i];
				Class cp = f.getType();
				if(cp.equals(int.class) || cp.equals(double.class)|| cp.equals(float.class)
						|| cp.equals(boolean.class) || cp.equals(short.class) || cp.equals(String.class)
						|| cp.equals(long.class) || cp.equals(char.class) || cp.equals(byte.class)){
				if(b != null)
					write.writeStringln(b);
				}
			}
		catch(NullPointerException e){
			//System.err.println("NullPointerException, printPramsToFile; " + e + " " + f);
		}

		catch(IllegalArgumentException e){
			System.out.println("IllegalArgumentException; " + e);
		}

		catch (IllegalAccessException e){
			System.out.println("IllegalAccessException; " + e);
		}

	}

	/** end use this code each time **/

	public void getData(SimState state){                                                         

	}
	
	public void printStringToFile(String s){
		write.writeStringln(s);
	}

	public void printStringtoConsol(String s){
		System.out.println(s);
	}

	public void printHeadersToConsole(Data data){
		String header = "";
		if(sc != null)
			System.out.println("Sim#: "+sc.simulationCount);
		for(int i=0; i<data.fileheaders.length;i++)
			header += (data.fileheaders[i] + "\t");
		System.out.println(header);
	}

	public void printDataToConsole(Data data){
		String thedata = "";
		
		for(int i=0; i<data.data.numObjs;i++){
			String s;
			double x = (Double)data.data.objs[i];
			if(x == Math.rint(x)) 
				s = String.format("%.0f", (float)x);
			else if(precision != "")
				s = String.format(precision, (float)x);
			else
				s = ((Object)x).toString();
			thedata += (s + "\t");                 
		}
		System.out.println(thedata);
	}

	public void printHeadersToFile(Data data){
		String header = "";
		if(sc != null)
			write.writeStringln("Sim#: "+sc.simulationCount);
		for(int i=0; i<data.fileheaders.length;i++)
			header += (data.fileheaders[i] + "\t");
		write.writeStringln(header);
	}

	public void printHeadersToFile2(Data data){
		String header = "";
		for(int i=0; i<data.fileheaders.length;i++)
			header += (data.fileheaders[i] + "\t");
		write.writeStringln(header);
	}

	public void printDataToFile(Data data){
		String thedata = "";
		for(int i=0; i<data.data.numObjs;i++){
			String s;
			double x = (Double)data.data.objs[i];
			if(x == Math.rint(x)) 
				s = String.format("%.0f", (float)x);
			else if(precision != "")
				s = String.format(precision, (float)x);
			else
				s = ((Object)x).toString();
			thedata += (s + "\t");                     

		}
		write.writeStringln(thedata);
	}

}