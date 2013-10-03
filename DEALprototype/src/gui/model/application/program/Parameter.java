package gui.model.application.program;

import yajco.annotation.Before;

/**
 * Predstavuje parameter príkazu programu. Každý parameter má svoj názov
 * a svoju hodnotu v tvare: (nazov = "hodnota").
 */
public class Parameter {

   /** Názov parametra. */
   private final Name name;
   /** Hodnota parametra. */
   private final String value;

   /**
    * Konštruktor.
    * @param name Názov parametra.
    * @param value Hodnota parametra.
    */
   public Parameter(Name name, @Before("EQ") String value) {
      this.name = name;
      this.value = value.substring(1, value.length() - 1);
   }

   /**
    * @return Názov parametra.
    */
   public Name getName() {
      return name;
   }

   /**
    * @return Hodnota parametra.
    */
   public String getValue() {
      return value;
   }

   @Override
   public String toString() {
      return name + " = \"" + value + "\"";
   }
}
