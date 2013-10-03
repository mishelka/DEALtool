package gui.model.application.program;

/**
 * Predstavuje abstraktnı príkaz programu. Definuje povinnú abstraktnú metódu
 * execute, ktorá má za úlohu vykona príkaz na zadanom komponente.
 */
public abstract class Command {
   /**
    * Vykoná príkaz na zadanom komponente.
    * @param <T> trieda zadaného komponentu.
    * @param component komponent, na ktorom sa má vykona príkaz.
    * @return Hodnota true ak bol príkaz vykonanı v poriadku, false inak.
    */
   public abstract <T> boolean execute(T component);
}
