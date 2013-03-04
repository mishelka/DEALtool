package gui.analyzer.handlers;

import gui.analyzer.ProcessingException;
import gui.analyzer.Recorder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Serves for adding an apropriate record support handler for a component class.
 * The RecordSupports class is implemented according to the singleton pattern.
 * Trieda RecordSupports je impelentovanĂˇ podÄľa vzoru singleton.
 * @author Michaela Bačíková <michaela.bacikova@tuke.sk>
 */
public class RecordSupports {

   /** Property file name. */
   public static final String PROPERTIES_FILE = "recordSupports.properties";
   /** Path to the properties file directory. */
   public static final String PROPERTIES_FILE_PROPERTY = "gui.analyzer.handlers";
   /** A hashmap containing couples: a component class and a record support handler */
   private Map<Class<?>, RecordSupport<?>> recordSupports = new HashMap<Class<?>, RecordSupport<?>>();
   /** Singleton instance of the RecordSupports class. */
   private static RecordSupports instance;

   /**
    * The constructor loads the property file to memory and according to its content the recordSupports hashmap is filled.
    */
   private RecordSupports() {
      Properties properties = new Properties();
      try {
         String propertiesFile = System.getProperty(PROPERTIES_FILE_PROPERTY, PROPERTIES_FILE);
         properties.load(getClass().getResourceAsStream(propertiesFile));

         for (String className : properties.stringPropertyNames()) {
            Class<?> componentClass = Class.forName(className);
            @SuppressWarnings("unchecked")
            Class<? extends RecordSupport<?>> recordSupportClass = (Class<? extends RecordSupport<?>>) Class.forName(properties.getProperty(className));
            recordSupports.put(componentClass, recordSupportClass.newInstance());
         }
      } catch (Exception e) {
         e.printStackTrace();
         new ProcessingException("Cannot load " + PROPERTIES_FILE, e);
      }
   }

   /**
    * @return Singleton instance of the RecordSupports class.
    */
   public static RecordSupports getInstance() {
      if (instance == null) {
         instance = new RecordSupports();
      }
      return instance;
   }

   /**
    * Based on the given component class returns a record support handler for this class.
    * @param <T> Component class type
    * @param componentClass component class
    * @return A record support handler for the given component class.
    */
   @SuppressWarnings("unchecked")
   public <T> RecordSupport<T> getRecordSupport(Class<T> componentClass) {
      return (RecordSupport<T>) recordSupports.get(componentClass);
   }
}
