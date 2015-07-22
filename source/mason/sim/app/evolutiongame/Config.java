package sim.app.evolutiongame;

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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.evolutiongame.Util.Pair;
import sim.app.evolutiongame.modules.Module;

/**
 *
 * @author Ben Armstrong
 */
public class Config {
    
    private static final String FILE_NAME = "modules.json";
    private static final String MODULE_PACKAGE = "sim.app.evolutiongame.modules";
    private static final String MODULE_PATH = "source/mason/sim/app/evolutiongame/modules";
    
    /**
     * From the list of modules generated by findModuleImplementations(), this 
     * method will create/overwrite the json configuration file in the root
     * project directory.
     * Currently the config file consists of a list of implementations of each
     * module and a list of which implementation to use for each module. The
     * default config file will use all modules that have at least one
     * implementation.
     * @param modules 
     */
    public static void generateConfigFile() {
        
        LinkedHashMap<String, Object> modules = findModuleImplementations();
        LinkedHashMap<String, String> defaults = getDefaultImplementations(modules);
        HashSet<String> notInUse = getModulesNotInUse(modules, defaults.keySet());
        
        //use a LinkedHashMap to preserve order (which keeps the output file in
        //a more readable format).
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        output.put("All Module Implementations", modules);
        output.put("Modules In Use (Ordered)", defaults);
        output.put("Modules Not In Use", notInUse);
        
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
    public static LinkedHashMap<String, Object> findModuleImplementations() {
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
        
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        ArrayList<String> implementations = null;
        String s = folder.getAbsolutePath();
        
        for(File module: folder.listFiles(folderFilter)) {
            for(File implementation: module.listFiles(fileFilter)) {
                if(implementations == null)
                    implementations = new ArrayList<>();
                implementations.add(implementation.getName().replace(".java", ""));
            }
            if(implementations != null) {
                map.put(module.getName(), implementations);
                implementations = null;
            }
            
        }
        
        return map;
    }
    
    /**
     * Gets the name of the default implementation of each module given to it.
     * The default name is the implementation that has the same name as the 
     * module, or if that doesn't exist, the first implementation listed.
     * @param modules
     * @return A map of module names to the default implementation of that module.
     */
    private static LinkedHashMap<String, String> getDefaultImplementations(HashMap<String, Object> modules){
        
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
    
    private static HashSet<String> getModulesNotInUse(HashMap<String, Object> modules, Set<String> inUse) {
        HashSet<String> notInUse = new HashSet<>();
        
        for(String module: modules.keySet()) {
            if(!inUse.contains(module)) {
                notInUse.add(module);
            }
        }
        
        return notInUse;
    }
    
    /**
     * Reads in the config file and puts it into a hashmap.
     * @param p
     * @return 
     */
    public static HashMap<String, Object> readConfigFile(Population p) {
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

    /**
     * Generates a list of all methods that should be run for each player in
     * each time step. Methods should all have the signature:
     *  public static void run(Population state, Player p)
     * The classes that the methods are in should be specified in the
     * configuration file.
     * @param modules
     * @param order List of Modules, in the order that they should be run.
     * @return 
     */
    public static LinkedHashMap<String, Pair<Module, Method>> getMethods(LinkedTreeMap<String, String> modules) {
        
        LinkedHashMap<String, Pair<Module, Method>> methods = new LinkedHashMap<>();
        
        for(String module: modules.keySet()) {
            Class c = null;
            try {
                c = Class.forName(MODULE_PACKAGE+"."+module+"."+modules.get(module));

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Method m;
            try {
                m = c.getMethod("run", Population.class, Player.class, Object.class);
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
}
