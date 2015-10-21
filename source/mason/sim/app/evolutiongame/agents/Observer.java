package sim.app.evolutiongame.agents;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.Config;
import sim.app.evolutiongame.PayoffMatrices;
import sim.app.evolutiongame.Population;
import sim.app.evolutiongame.Util;
import sim.app.evolutiongame.modules.Module;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 *
 * @author Ben Armstrong
 */
public class Observer implements Steppable {
    
    private boolean initializedColumns = false;
    private ArrayList<String> columnNames = new ArrayList<>();
    private HashMap<String, ArrayList<String>> columnData = new HashMap<>();
    
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd_HH:mm:ss";
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    private FileWriter writer;
    
    private boolean save;
    
    public Observer(Population pop){
        if(pop.getSaveData()){
            save = true;
            try
            {
                File folder = new File("output");
                boolean mkdir = folder.mkdir();
                writer = new FileWriter(new File("output/output_" + sdf.format(cal.getTime()) + ".csv"));
            } catch (IOException ex)
            {
                Logger.getLogger(Observer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else 
            save = false;
    }

    @Override
    public void step(SimState state) {
        
        boolean print = ((Population)state).getPrintData();
        
        Population pop = (Population)state;
        
        int[] strategyCounts = new int[PayoffMatrices.getMaxNumStrategies()];
        for(Player p: pop.getPlayers()){
            if(!p.hasVariable("strategy")){
                p.runModuleOutOfTurn("FindStrategy");
            }
            strategyCounts[(int)p.getVariable("strategy")]++;
        }
        
        //Make sure everything is nice and initialized
        if(!initializedColumns){
            columnNames = new ArrayList<>();
            columnData = new HashMap<>();
            columnNames.add("Time");
            columnData.put("Time", new ArrayList<>());
            
            columnNames.add("# Players");
            columnData.put("# Players", new ArrayList<>());
            
            //add arraylists for keeping track of number of players of each strategy
            for(int strat = 0; strat < strategyCounts.length; ++strat){
                columnNames.add("Strategy " + strat);
                columnData.put("Strategy " + strat, new ArrayList<>());
            }
        }
        
        for(int strat = 0; strat < strategyCounts.length; ++strat){
            columnData.get("Strategy " + strat).add(strategyCounts[strat]+"");
        }
        
        //Update time and population columns in stored data
        columnData.get("Time").add(new DecimalFormat("#.#").format(state.schedule.getTime()));
        columnData.get("# Players").add(pop.getPlayers().size() + "");
        
        
        
        //Run trackStatistics methods and record all the results
        for(Map.Entry<String, Util.Pair<Module, Method>> entry: Config.statisticsMethods.entrySet()){
            Object o = this.invokeMethod(entry.getValue().getSecond(), entry.getValue().getFirst(), pop);
            if( null == o)
                continue;
            if(o instanceof ArrayList) {
                //data should contain two arraylists, one with column names
                //and the other with column values
                ArrayList<ArrayList<String>> data = (ArrayList<ArrayList<String>>) o;
                if(data.size() != 2)
                    continue;

                //data is semi-validated, start saving it.
                //Assume no duplicate column names
                for(int i = 0; i < data.get(0).size(); ++i){
                    String column = data.get(0).get(i);
                    if(!columnNames.contains(column))
                        columnNames.add(column);
                    //associate the column name with the data
                    if(!columnData.containsKey(column))
                        columnData.put(column, new ArrayList<>());
                    columnData.get(column).add(data.get(1).get(i));
                }
            }
        }
        
        //create row of the new data for printing or appending to output file
        ArrayList<String> row = new ArrayList<>();
        for(String name: columnNames){
            ArrayList<String> column = columnData.get(name);
            row.add(column.get(column.size()-1));
        }
        
        if(print){
            if(!initializedColumns){
                String nameRow = "";
                for(String name: columnNames){
                    nameRow += name + "\t";
                }
                System.out.println(nameRow);
            }
            for(String value: row)
                System.out.print(value + "\t");
            System.out.println("");
        }
        
        if(save){
            if(!initializedColumns){
                //write header row
                String nameRow = "";
                for(String name: columnNames){
                    nameRow += "\"" + name + "\",";
                }
                nameRow = nameRow.substring(0, nameRow.length()-1);
                nameRow += "\n";
                try {
                    writer.append(nameRow);
                } catch (IOException ex) {
                    Logger.getLogger(Observer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //write next row of data
            String rowString = "";
            for(String value: row){
                rowString += "\"" + value + "\",";
                
            }
            rowString = rowString.substring(0, rowString.length()-1);
            rowString += "\n";
            try {
                writer.append(rowString);
            } catch (IOException ex) {
                System.err.println("Tried writing to stream when it was already closed.");
            }
        }
        
        if(pop.getPlayers().isEmpty()){
            System.out.println("No players remain. Ending Observations.");
            pop.finish();
        }
        
        initializedColumns = true;
    }
    
    public void closeWriter(){
        try
        {
            if(null != writer)
                writer.close();
        } catch (IOException ex)
        {
            Logger.getLogger(Observer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object invokeMethod(Method m, Module module, Population pop) {
        try {
            return m.invoke(module);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
