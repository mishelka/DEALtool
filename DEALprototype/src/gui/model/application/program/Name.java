package gui.model.application.program;

/**
 * Predstavuje názov príkazu alebo parametra.
 */
public class Name {
   /** Reťazec názvu príkazu alebo parametra. */
   private final String name;

   /**
    * Konštruktor
    * @param name reťazec názvu príkazu alebo parametra.
    */
   public Name(String name) {
      if (name.startsWith("[") && name.endsWith("]")) {
         this.name = name.substring(1, name.length() - 1);
      } else {
         this.name = name;
      }
   }

   @Override
   public String toString() {
      return name;
   }
}
