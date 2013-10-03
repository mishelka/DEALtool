package gui.model.application.program;

/**
 * Predstavuje abstraktn� pr�kaz programu. Definuje povinn� abstraktn� met�du
 * execute, ktor� m� za �lohu vykona� pr�kaz na zadanom komponente.
 */
public abstract class Command {
   /**
    * Vykon� pr�kaz na zadanom komponente.
    * @param <T> trieda zadan�ho komponentu.
    * @param component komponent, na ktorom sa m� vykona� pr�kaz.
    * @return Hodnota true ak bol pr�kaz vykonan� v poriadku, false inak.
    */
   public abstract <T> boolean execute(T component);
}
