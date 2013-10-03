package gui.model.application.program;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.CommandHandlers;
import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;

import java.util.Arrays;

import yajco.annotation.After;
import yajco.annotation.Before;
import yajco.annotation.Separator;

/**
 * Predstavuje implement�ciu pr�kazu Command. Reprezentuje samotn� pr�kaz, ktor�
 * je mo�n� vykona� na programe.
 */
public class DelegateCommand extends Command {

   /** N�zov pr�kazu. */
   private final Name name;
   /** Zoznam parametrov. */
   private final Parameter[] parameters;

   /**
    * Kon�truktor
    * @param name n�zov pr�kazu.
    * @param parameters zoznam parametrov pr�kazu.
    */
   public DelegateCommand(Name name, @Before("[") @After("]") @Separator(",") Parameter[] parameters) {
      this.name = name;
      this.parameters = parameters;
      System.out.println(name + " " + Arrays.toString(parameters));
   }

   /**
    * @return N�zov pr�kazu.
    */
   public Name getName() {
      return name;
   }

   /**
    * @return Zoznam parametrov pr�kazu.
    */
   public Parameter[] getParameters() {
      return parameters;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> boolean execute(T component) {
      Class<? super T> componentClass = (Class<T>) component.getClass();
      //Handlers
      while (componentClass != null) {
         CommandHandler<? super T> commandHandler = CommandHandlers.getInstance().getHandler(componentClass);
         if (commandHandler != null) {
            if (commandHandler.supportsCommand(this, component)) {
               commandHandler.execute(this, component);
               return true;
            }
         }
         componentClass = componentClass.getSuperclass();
      }

      //Composites
      componentClass = (Class<T>) component.getClass();
      Composite<? super T> composite = Composites.getInstance().getComposite(componentClass);
      if (composite != null) {
         for (Object subcomponent : composite.getComponents(component)) {
            if (execute(subcomponent)) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return name.toString();
   }
}
