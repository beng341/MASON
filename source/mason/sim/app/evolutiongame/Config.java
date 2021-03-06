package sim.app.evolutiongame;

import sim.app.evolutiongame.agents.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.agents.Environment;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class Config {
    
    public static void main(String[] args){
        generateConfigFile();
    }
    
    private static final String FILE_NAME = "config.json";
    private static final String MODULE_PACKAGE = "sim.app.evolutiongame.modules";
    private static final String MODULE_PATH = "source/mason/sim/app/evolutiongame/modules";
    
    /***** The list of sections in the outputted configuration file. *******/
    
    /**
     * This section contains a list of all module names pointing at:
  1) A list of the names of all implementations of the module
  2) A list of the playerArguments that the default implementation of the module
  says are required.
     */
    public static final String ALL_MODULES = "All Modules";
    public static LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> moduleImplementations = findModuleImplementations();
    
    public static LinkedHashMap<String, String> playerDefaults = 
            getDefaultImplementations(moduleImplementations.get("player"));
    public static LinkedHashMap<String, String> environmentDefaults = 
            getDefaultImplementations(moduleImplementations.get("environment"));
    
    public static LinkedHashMap<String, String[]> playerArguments = 
            getArguments(moduleImplementations.get("player").keySet(), "player");
    public static LinkedHashMap<String, String[]> environmentArguments = 
            getArguments(moduleImplementations.get("environment").keySet(), "environment");
    
    /**
     * The list of modules pointing at implementations that should be run when
     * a specific module is run, not at the time specified in MODULES_TO_RUN.
     */
    public static final String PREFERRED_IMPLEMENTATIONS = "Preferred Implementations";
    
    private static final HashMap<String, Object> configElements = readConfigFile();
    
    /**
     * The names of modules and module implementations that will be run on each
     * player, in the order that they will be run.
     */
    public static final String MODULES_TO_RUN = "Modules To Run (Ordered)";
    
    //This currently is a map: player type -> (module name -> (module class, module run method))
    public static final LinkedHashMap<String, LinkedHashMap<String, Pair<Module, Method>>> 
            playerModulesToRun = configElements.isEmpty() ? null: Config.getPlayerRunMethods(
                    (LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.MODULES_TO_RUN));
    
    public static final LinkedHashMap<String, Pair<Module, Method>> 
            environmentModulesToRun = configElements.isEmpty() ? null: 
            Config.getRunMethods(
                    ((LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.MODULES_TO_RUN)).get("environment"), "environment");
    
    public static final LinkedHashMap<String, Util.Pair<Module, Method>> 
            preferredPlayerModules = configElements.isEmpty() ? null: 
            getPreferredModuleMethods(
                    ((LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.PREFERRED_IMPLEMENTATIONS)).get("player"), "player");
    public static final LinkedHashMap<String, Util.Pair<Module, Method>> 
            preferredEnvironmentModules = configElements.isEmpty() ? null: 
            getPreferredModuleMethods(
                    ((LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.PREFERRED_IMPLEMENTATIONS)).get("environment"), "environment");
    
    
    public static final String SETUP_MODULES = "Setup Modules";
    
    public static final LinkedHashMap<String, Util.Pair<Module, Method>> 
            playerSetupModules = configElements.isEmpty() ? null: 
            getSetupMethods(((LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.SETUP_MODULES)).get("player"), "player");
    public static final LinkedHashMap<String, Util.Pair<Module, Method>> 
            environmentSetupModules = configElements.isEmpty() ? null: 
            getSetupMethods(((LinkedTreeMap<String, LinkedTreeMap<String, String>>)configElements.get(Config.SETUP_MODULES)).get("environment"), "environment");
    
    public static final LinkedHashMap<String, Pair<Module, Method>> 
            statisticsMethods = configElements.isEmpty() ? null: getStatisticsMethods(playerModulesToRun);
    public static final LinkedHashMap<String, Pair<Module, Method>> 
            cleanupMethods = configElements.isEmpty() ? null: getCleanUpMethods(playerModulesToRun);
    
    public static final LinkedTreeMap<String, String> 
//            simParamaters = configElements.isEmpty() ? null : getSimulationParameters((LinkedTreeMap<String, String>)configElements.get("Simulation Parameters"));
            simParamaters = configElements.isEmpty() ? null : (LinkedTreeMap<String, String>)configElements.get("Simulation Parameters");
    
    public static void loadModules(){
        
    }
    
    
    /**
     * From the list of modules generated by findModuleImplementations(), this 
     * method will create/overwrite the json configuration file in the root
     * project directory.
     * Currently the config file consists of a list of implementations of each
     * module and a list of which implementation to use for each module. The
     * default config file will use all modules that have at least one
     * implementation. 
     */
    public static void generateConfigFile() {
        //generate modules to run section with subsections for player and environment
        LinkedHashMap<String, LinkedHashMap<String, String>> toRun = new LinkedHashMap<>();
        toRun.put("player", playerDefaults);
        toRun.put("environment", environmentDefaults);
        
        //generate All Modules section with subsections for player and environment
        LinkedHashMap<String, LinkedHashMap<String, Object>> playerModules = new LinkedHashMap<>();
        for(String moduleName: moduleImplementations.get("player").keySet()){
            LinkedHashMap<String, Object> tmp = new LinkedHashMap<>();
            tmp.put("Implementations", moduleImplementations.get("player").get(moduleName));
            tmp.put("Arguments", playerArguments.get(moduleName));
            playerModules.put(moduleName, tmp);
        }
        LinkedHashMap<String, LinkedHashMap<String, Object>> environmentModules = new LinkedHashMap<>();
        for(String moduleName: moduleImplementations.get("environment").keySet()){
            LinkedHashMap<String, Object> tmp = new LinkedHashMap<>();
            tmp.put("Implementations", moduleImplementations.get("environment").get(moduleName));
            tmp.put("Arguments", environmentArguments.get(moduleName));
            environmentModules.put(moduleName, tmp);
        }
        LinkedHashMap<String, Object> allModules = new LinkedHashMap<>();
        allModules.put("player", playerModules);
        allModules.put("environment", environmentModules);
        
        //PREFERRED_IMPLEMENTATIONS should contain every module
        LinkedHashMap<String, Object> preferred = new LinkedHashMap<>();
        preferred.put("player", playerDefaults);
        preferred.put("environment", environmentDefaults);
        
        //use a LinkedHashMap to preserve order (which keeps the output file in
        //a more readable format).
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        output.put(ALL_MODULES, allModules);
        output.put(MODULES_TO_RUN, toRun);
        output.put(PREFERRED_IMPLEMENTATIONS, preferred);
        
        //Initial Modules should contain an example of how to write modules into
        //the section.
        output.put(SETUP_MODULES, preferred);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
          writer.write(gson.toJson(output));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Finds a list of all java classes that are in each module subdirectory.
     * Associates with each module the classes under it.
     * Ex: If /modules contains folders "play" and "move", with classes "play1",
     * "play2", "move1", "move2" then this will return a map of "play" -> {play1, play2},
     * "move" -> {move1, move2}.
     * This will likely assume that /modules contains only folders and that each
     * folder within /modules contains only files providing an implementation
     * of that module.
     * @return 
     */
    public static LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> findModuleImplementations() {
        FileFilter folderFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname){
                return pathname.isDirectory();
            }};
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname){
                return pathname.isFile();
            }};
        
        File folder = new File(MODULE_PATH);
        
        LinkedHashMap<String, ArrayList<String>> playerImplementations = new LinkedHashMap<>();
        LinkedHashMap<String, ArrayList<String>> environmentImplementations = new LinkedHashMap<>();
        ArrayList<String> implementations = null;
        
        //Search all the folders within the modules folder, then in each folder
        //found, search for more folders, then in each of those folders, look
        //for each java file.
        //We should find modules/player and modules/environment to begin with
        
        for(File typeOfModule: folder.listFiles(folderFilter)) {
            String s = typeOfModule.getName();
            for(File module: typeOfModule.listFiles(folderFilter)) {
                for(File implementation: module.listFiles(fileFilter)) {
                    if(implementations == null)
                        implementations = new ArrayList<>();
                    if(implementation.getName().contains(".java")){
                        implementations.add(implementation.getName().replace(".java", ""));
                    }
                }
                if(implementations != null) {
                    s = typeOfModule.getPath();
                    if(typeOfModule.getName().contains("player")){
                        playerImplementations.put(module.getName(), implementations);
                    } else if(typeOfModule.getName().contains("environment")){
                        environmentImplementations.put(module.getName(), implementations);
                    }
                    implementations = null;
                }

            }
        }
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>
                results = new LinkedHashMap<>();
        results.put("player", playerImplementations);
        results.put("environment", environmentImplementations);
        
        return results;
    }
    
    /**
     * Gets the name of the default implementation of each module given to it.
     * The default name is the implementation that has the same name as the 
     * module, or if that doesn't exist, the first implementation listed.
     * @param modules
     * @return A map of module names to the default implementation of that module.
     */
    private static LinkedHashMap<String, String> getDefaultImplementations(HashMap<String, ArrayList<String>> modules){
        
        LinkedHashMap<String, String> defaults = new LinkedHashMap<>();
        
        for(String module_name: modules.keySet()) {
            ArrayList<String> implementations = (ArrayList<String>)modules.get(module_name);
            
            for(String implementation: implementations) {
                if(implementation.equalsIgnoreCase(module_name)) {
                    defaults.put(module_name, implementation);
                    break;
                }
            }
            if(!defaults.containsKey(module_name) && implementations.size() > 0)
                defaults.put(module_name, implementations.get(0));
        }
        
        
        return defaults;
    }
    
    /**
     * Reads in the config file and puts it into a hashmap.
     * @return 
     */
    public static HashMap<String, Object> readConfigFile() {
        Gson gson = new Gson();
        
        HashMap<String, Object> configElements = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
              configElements = gson.fromJson(reader, HashMap.class);
        } catch (FileNotFoundException e) {
            System.out.println("Finised with JSON examples now.");
        } catch (IOException e) {
            System.out.println("Finised with JSON examples now.");
        }
        
        return configElements;
    }
    
    public static LinkedHashMap<String, LinkedHashMap<String, Pair<Module, Method>>> getPlayerRunMethods(LinkedTreeMap<String, LinkedTreeMap<String, String>> modules){
        
        LinkedHashMap<String, LinkedHashMap<String, Pair<Module, Method>>> playerModules = new LinkedHashMap<>();
        
        for(String s: modules.keySet()){
            if(s.equals("environment")){
                continue;
            }
            playerModules.put(s, Config.getRunMethods(modules.get(s), "player"));
        }
        
        return playerModules;
    }
    

    /**
     * Generates a list of all methods that should be run for each player in
     * each time step. Methods should all have the signature:
     *  public static void run(Population state, Player p)
     * The classes that the methods are in should be specified in the
     * configuration file.
     * @param modules
     * @param type
     * @return 
     */
    public static LinkedHashMap<String, Pair<Module, Method>> getRunMethods(LinkedTreeMap<String, String> modules, String type) {
        
        LinkedHashMap<String, Pair<Module, Method>> methods = new LinkedHashMap<>();
        
        for(String module: modules.keySet()) {
            Class c = null;
            try {
                c = Class.forName(MODULE_PACKAGE+"."+type+"."+module+"."+modules.get(module));
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Method m;
            try {
                switch(type){
                    case "player":
                        m = c.getMethod("run", Population.class, Player.class);
                        break;
                    case "environment":
                        m = c.getMethod("run", Population.class, Environment.class);
                        break;
                    default:
                        m = c.getMethod("run", Population.class, Player.class);
                        break;
                }
                methods.put(module, new Pair<>((Module)c.newInstance(), m));
            } catch (NoSuchMethodException ex){
                System.err.println("No run() method found in " + c.toString());
            } catch(NullPointerException ex){
                if(null == c){
                    System.err.println("No class found for " + modules.get(module));
                }
            } catch(SecurityException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return methods;
    }
    
    public static LinkedHashMap<String, Pair<Module, Method>> getStatisticsMethods(LinkedHashMap<String, LinkedHashMap<String, Pair<Module, Method>>> modules) {
        
        LinkedHashMap<String, Pair<Module, Method>> result = new LinkedHashMap<String, Pair<Module, Method>>();
        
        for(String s: modules.keySet()){
            for(String module: modules.get(s).keySet()) {
                Class c = result.get(module).getFirst().getClass();

                Method m;
                try {
                    m = c.getMethod("trackStatistics", Population.class);
                    result.put(module, new Pair<>(result.get(module).getFirst(), m));
                } catch (NoSuchMethodException ex){
                    //System.out.println("No run() method found in " + c.toString());
                } catch(SecurityException ex) {
                    Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        return result;
    }
    
    public static LinkedHashMap<String, Pair<Module, Method>> getCleanUpMethods(LinkedHashMap<String, LinkedHashMap<String, Pair<Module, Method>>> modules) {
        
        LinkedHashMap<String, Pair<Module, Method>> result = new LinkedHashMap<String, Pair<Module, Method>>();
        
        for(String s: modules.keySet()){
            for(String module: modules.get(s).keySet()) {
                Class c = result.get(module).getFirst().getClass();

                Method m;
                try {
                    m = c.getMethod("cleanUp", Population.class);
                    result.put(module, new Pair<>(result.get(module).getFirst(), m));
                } catch (NoSuchMethodException ex){
                    //System.out.println("No run() method found in " + c.toString());
                } catch(SecurityException ex) {
                    Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        return result;
    }
    
    public static LinkedHashMap<String, Pair<Module, Method>> getPreferredModuleMethods(LinkedTreeMap<String, String> modules, String type) {
        
        LinkedHashMap<String, Pair<Module, Method>> methods = new LinkedHashMap<>();
        
        for(String module: modules.keySet()) {
            Class c = null;
            try {
                c = Class.forName(MODULE_PACKAGE+"."+type+"."+module+"."+modules.get(module));

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Method m;
            try {
                switch(type){
                    case "player":
                        m = c.getMethod("run", Population.class, Player.class);
                        break;
                    case "environment":
                        m = c.getMethod("run", Population.class, Environment.class);
                        break;
                    default:
                        m = c.getMethod("run", Population.class, Player.class);
                        break;
                }
                methods.put(module, new Pair<>((Module)c.newInstance(), m));
            } catch (NoSuchMethodException ex){
                System.out.println("No run() method found in " + c.toString());
            } catch(SecurityException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return methods;
    }
    
    public static LinkedHashMap<String, Pair<Module, Method>> getSetupMethods(LinkedTreeMap<String, String> modules, String type) {
        
        LinkedHashMap<String, Pair<Module, Method>> methods = new LinkedHashMap<>();
        
        for(String module: modules.keySet()) {
            Class c = null;
            try {
                c = Class.forName(MODULE_PACKAGE+"."+type+"."+module+"."+modules.get(module));

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Method m;
            try {
                switch(type){
                    case "player":
                        m = c.getMethod("setup", Population.class, Player.class);
                        break;
                    case "environment":
                        m = c.getMethod("setup", Population.class, Environment.class);
                        break;
                    default:
                        m = c.getMethod("setup", Population.class, Player.class);
                        break;
                }
                methods.put(module, new Pair<>((Module)c.newInstance(), m));
            } catch (NoSuchMethodException ex){
                System.err.println("No setup() method found in " + c.toString());
            } catch(SecurityException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return methods;
    }

    /**
     * Use reflection to retrieve the value of the "args" String array in each 
     * module class. This relies on the default class for each module, the 
     * module implementation with the same name as the module itself.
     * @param modules
     * @return 
     */
    public static LinkedHashMap<String, String[]> getArguments(Set<String> modules, String type)
    {
        LinkedHashMap<String, String[]> arguments = new LinkedHashMap<>();
        
        for(String module: modules) {
            Class c = null;
            try {
                c = Class.forName(MODULE_PACKAGE+"."+type+"."+module+"."+module);
                Field field = c.getDeclaredField("args");
                String[] args = (String[])field.get(null); //can be null since args should be static
                arguments.put(module, args);
            } catch (NoSuchFieldException ex)
            {
                //This will occur if someone simply forgets to add the args 
                //array to their module.  This is not a big deal, it just means
                //their are no arguments. Add an empty array to indicate this.
                arguments.put(module, new String[]{});
                
            } catch (ClassNotFoundException ex) {
                
                //This will occur if an implementation exists for a module that
                //has no implementation with the same name as the module itself
                arguments.put(module, new String[]{});
                
            } catch (SecurityException ex)
            {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex)
            {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex)
            {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex)
            {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return arguments;
    }

    private static LinkedHashMap<String, String> getSimulationParameters(LinkedTreeMap<String, String> params)
    {
        if(params == null)
            return null;
        
        
        
        
        
        return null;
    }
}
