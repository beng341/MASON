package hawkdove_smaldino.io;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileReader;
import java.io.StreamTokenizer;
import sim.util.*;

public final class LoadSimulationParameters implements Serializable{
	public final static String KEYWORD = "SIMULATION_PARAMETERS";

	public final static  Bag load(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		int n;
		Bag names = new Bag();
		Bag values = new Bag();;

		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		try {
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD)
					names.add(st.sval);
				token = st.nextToken();
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD){						
					if(st.sval.equalsIgnoreCase("true"))
						values.add(true);
					else if(st.sval.equalsIgnoreCase("false"))
						values.add(false);
					else
						values.add(st.sval);
				}
				else if(token == StreamTokenizer.TT_NUMBER){
					values.add(st.nval);				
				}
				token = st.nextToken();
			}					

		} catch (IOException e) {
			System.err.println("st.nextToken() error in load Chromosome: " + e);
		}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		Bag b = new Bag();
		b.add(names);
		b.add(values);
		return b;	
	}

	public final static  Bag loadParameters(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		Bag varibleNames = new Bag();
		Bag values = new Bag();
		Bag simulationVariableNames = new Bag();
		Bag simulations = new Bag();
		boolean getsimulationVariableNames = false;

		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		try {
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD && KEYWORD.equalsIgnoreCase(st.sval)){
					getsimulationVariableNames = true;
					token = st.nextToken();
					while(token == StreamTokenizer.TT_EOL) //move through end of lines
						token = st.nextToken();
					// next get the variable names for the simulation
					if(token == StreamTokenizer.TT_WORD){
						simulationVariableNames.add(st.sval);
						token = st.nextToken();
						while(token != StreamTokenizer.TT_EOL){
							if(token == StreamTokenizer.TT_WORD){
								simulationVariableNames.add(st.sval);
							}
							token = st.nextToken();
						}
					}
					while(token == StreamTokenizer.TT_EOL) //move through end of lines
						token = st.nextToken();
				}
				if(!getsimulationVariableNames){
					if(token == StreamTokenizer.TT_WORD)
						varibleNames.add(st.sval);
					token = st.nextToken();
					while(token == StreamTokenizer.TT_EOL) //move through end of lines
						token = st.nextToken();
					if(token == StreamTokenizer.TT_WORD || token == StreamTokenizer.TT_NUMBER){
						if(token == StreamTokenizer.TT_WORD)
							values.add(st.sval);
						if(token == StreamTokenizer.TT_NUMBER)
							values.add(st.nval);
					}
					token = st.nextToken();
				}
				else {
					if(token == StreamTokenizer.TT_WORD || token == StreamTokenizer.TT_NUMBER ){
						Bag simulationValues = new Bag();
						if(token == StreamTokenizer.TT_WORD)
							simulationValues.add(st.sval);
						if(token == StreamTokenizer.TT_NUMBER)
							simulationValues.add(st.nval);
						token = st.nextToken();
						while(token != StreamTokenizer.TT_EOL){
							if(token == StreamTokenizer.TT_WORD|| token == StreamTokenizer.TT_NUMBER ){
								if(token == StreamTokenizer.TT_WORD)
									simulationValues.add(st.sval);
								if(token == StreamTokenizer.TT_NUMBER)
									simulationValues.add(st.nval);
							}
							token = st.nextToken();
						}
						/*for(int i=0;i<simulationValues.numObjs;i++)
							System.out.println(simulationValues.objs[i]);*/
						simulations.add(simulationValues);
					}
				}
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
		b.add(simulationVariableNames);
		b.add(simulations);
		return b;	
	}

	public final static void printPramaters(Bag b){
		Bag b1 = (Bag)b.objs[0];
		Bag b2 = (Bag)b.objs[1];
		for(int i=0;i<b1.numObjs;i++)
			System.out.println(b1.objs[i] + "  " + b2.objs[i]);
		Bag b3 = (Bag)b.objs[2];
		for(int i=0;i<b3.numObjs;i++)
			System.out.print(b3.objs[i]+ "  ");
		System.out.println();
		System.out.println("b3.numObjs " + b3.numObjs);
		
		Bag b4 = (Bag)b.objs[3];
		for(int i=0;i<b4.numObjs;i++){
			Bag v =(Bag) b4.objs[i];
			for(int j=0;j<v.numObjs;j++)
				System.out.print(v.objs[j]+ "  ");
			System.out.println();
		}
		
		System.out.println("b4.numObjs finish " + b4.numObjs);
	}

	public final static  Bag loadInt(String pathName){
		StreamTokenizer st = null;
		FileReader rd = null;
		int n;
		String[] names = null;
		int values[] = null;

		rd = TokenizeFile.readTextFile(pathName);
		st = TokenizeFile.tokenizeFileStream(rd);
		String s = TokenizeFile.getNextString(st);
		n = TokenizeFile.getNextInt(st);
		names = new String[n];
		values = new int[n];
		try {
			int i = 0;
			//we need to get the column headers
			int token = st.nextToken();
			while(token != StreamTokenizer.TT_EOF){
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_WORD)
					names[i] = st.sval;
				token = st.nextToken();
				while(token == StreamTokenizer.TT_EOL) //move through end of lines
					token = st.nextToken();
				if(token == StreamTokenizer.TT_NUMBER)
					values[i] = (int)st.nval;
				token = st.nextToken();
				//System.out.println(names[i] + " " + values[i]);
				i++;
				if(i >= n)
					break;
			}					

		} catch (IOException e) {
			System.err.println("st.nextToken() error in load Chromosome: " + e);
		}
		try{
			rd.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		Bag b = new Bag();
		b.add(names);
		b.add(values);
		return b;	
	}
	
	public final static  void print2Bags(Bag b1, Bag b2) {
		for(int j=0;j<b1.numObjs;j++)
			System.out.println(b1.objs[j]+"  "+b2.objs[j]);
		System.out.println();
	}

}
