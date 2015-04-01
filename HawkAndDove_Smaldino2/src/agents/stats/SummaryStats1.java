package agents.stats;

import agents.AgentsSimulation;
import agents.Observer;
import agents.WriteData;
import sim.engine.SimState;
import sim.util.Bag;

/**
 * 
 * @author jcschank
 *
 *This Class will take the data output from the data collection object.
 */
public class SummaryStats1 {
	public double[][] dataTable = null;
	public double[][] varTable = null;
	public double[][] minTable = null;
	public double[][] maxTable = null;
	public String[] headers;
	WriteData write;
	public  String precision = "%.4f";
	public SimState state;
	Observer ob;

	public SummaryStats1(Observer ob,SimState state,int rows, int coloumns, String[] headers, 
			String folderName, String fileName, String precision){
		this.state = state;
		this.precision = precision;
		this.ob = ob;
		dataTable = new double[rows][coloumns];
		varTable = new double[rows][coloumns];
		minTable = new double[rows][coloumns];
		maxTable = new double[rows][coloumns];
		for(int i=0; i<rows;i++){
			for(int j=0;j<coloumns;j++){
				dataTable[i][j]=0;
				varTable[i][j]=0;
				if(j == (coloumns - 1))
					minTable[i][j]=0;
				else
					minTable[i][j]=1;
				maxTable[i][j]=0;
			}
			;
		}
		this.headers = headers;
		write = new WriteData(folderName, fileName);
	}
	
	public void setNewObserver(Observer ob){
		this.ob = ob;
	}

	public void addToRow(Bag data, int rowNum){
		for(int i=0;i<data.numObjs;i++){
			double x = (Double)data.objs[i];
			dataTable[rowNum][i] += x;
			varTable[rowNum][i] += x*x;
			if(i == data.numObjs - 1){
				maxTable[rowNum][i] += x;
				minTable[rowNum][i] += x;
			}
			else if(x > maxTable[rowNum][i])
				maxTable[rowNum][i] = x;
			else if(x < minTable[rowNum][i])
				minTable[rowNum][i] = x;
		}
	}

	public void doStats(int numOfSimulations){
		for(int i=0;i<dataTable.length;i++ )
			for(int j=0;j<dataTable[0].length-1;j++){ // the last colomun is the number of simulations
				final double x = dataTable[i][dataTable[0].length-1]; //number of simulations
				if(x > 0){
					dataTable[i][j]= dataTable[i][j]/x;
					varTable[i][j] = varTable[i][j]/x - dataTable[i][j]*dataTable[i][j];
					if(Double.isNaN(varTable[i][j]) || Double.isInfinite(varTable[i][j]) || varTable[i][j] < 0)
						varTable[i][j] = 0;
				}
			}
	}

/*	public void printTablesToConsol(String header, boolean printSummary){
		AgentsSimulation as = (AgentsSimulation)state;
		System.out.println();
		if(printSummary){
			Bag b = ob.lifeSpanStats();
			ob.resetLifeSpan();
			String s = "Life Span:\t N =\t"+ b.objs[0]+ "\t mean =\t"+ b.objs[1]+ "\t Var =\t" + b.objs[2];
			System.out.println(s);
			s = "Frequency Played = " + ob.playedFrequency();
			System.out.println(s);
			s = "Frequency Successfully reproduced = " + ob.reproduceFrequency();
			System.out.println(s);
			System.out.println();
		}
		System.out.println(header);
		System.out.println("Means of Simulations");
		String h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		System.out.println(h);

		for(int i=0;i<dataTable.length;i++){
			String row = "";
			for(int j=0;j<dataTable[0].length;j++){
				double x = dataTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			System.out.println(row);
		}
		System.out.println();
		System.out.println("Variance");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		System.out.println(h);

		for(int i=0;i<varTable.length;i++){
			String row = "";
			for(int j=0;j<varTable[0].length;j++){
				double x = varTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			System.out.println(row);
		}

		System.out.println();
		System.out.println("Minimum Values");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		System.out.println(h);

		for(int i=0;i<minTable.length;i++){
			String row = "";
			for(int j=0;j<minTable[0].length;j++){
				double x = minTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			System.out.println(row);
		}

		System.out.println();
		System.out.println("Maximum Values");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		System.out.println(h);

		for(int i=0;i<maxTable.length;i++){
			String row = "";
			for(int j=0;j<maxTable[0].length;j++){
				double x = maxTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			System.out.println(row);
		}

	} */

/*	public void printTablesToFile(String header,boolean printSummary){
		AgentsSimulation as = (AgentsSimulation)state;
		write.writeStringln("");
		if(printSummary){
			Bag b = ob.lifeSpanStats();
			ob.resetLifeSpan();
			String s = "Life Span:\t N =\t"+ b.objs[0]+ "\t mean =\t"+ b.objs[1]+ "\t Var =\t" + b.objs[2];
			write.writeStringln(s);
			s = "Frequency Played = " + ob.playedFrequency();
			write.writeStringln(s);
			s = "Frequency Successfully reproduced = " + ob.reproduceFrequency();
			write.writeStringln(s);
			s = "Correlation offspring x p =" + ob.getCorrelation();
			write.writeStringln(s);
			s = "Mean Offspring = " + ob.meanOffspring() + " Var = " + ob.varOffspring();
			write.writeStringln(s);
			s = "Mean p = " + ob.meansumOf_p() + " Var = " + ob.varp();
			write.writeStringln(s);
			s = "Died = " + ob.died() + " Born = " + ob.born() + " Reproduced = " + ob.reproduced();
			write.writeStringln(s);
			write.writeStringln("");
		}
		write.writeStringln(header);
		write.writeStringln("Means of Simulations");
		String h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		write.writeStringln(h);

		for(int i=0;i<dataTable.length;i++){
			String row = "";
			for(int j=0;j<dataTable[0].length;j++){
				double x = dataTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			write.writeStringln(row);
		}
		write.writeStringln("");
		write.writeStringln("Variance");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		write.writeStringln(h);

		for(int i=0;i<varTable.length;i++){
			String row = "";
			for(int j=0;j<varTable[0].length;j++){
				double x = varTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			write.writeStringln(row);
		}

		write.writeStringln("");
		write.writeStringln("Minimum Values");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		write.writeStringln(h);

		for(int i=0;i<minTable.length;i++){
			String row = "";
			for(int j=0;j<minTable[0].length;j++){
				double x = minTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			write.writeStringln(row);
		}

		write.writeStringln("");
		write.writeStringln("Maximum Values");
		h = "";
		for(int i=0;i<headers.length;i++)
			h += headers[i] + "\t";
		write.writeStringln(h);

		for(int i=0;i<maxTable.length;i++){
			String row = "";
			for(int j=0;j<maxTable[0].length;j++){
				double x = maxTable[i][j];
				if(x == Math.rint(x))
					row += String.format("%.0f", (float)x) + "\t";
				else if(precision != "")
					row += String.format(precision, (float)x) + "\t";
				else
					row += ((Object)x).toString() + "\t";
			}
			write.writeStringln(row);
		}

	}*/

}
