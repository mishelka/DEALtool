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
 * The Command class implementation. Represents the command, which can be executed on a component.
 */
public class DelegateCommand extends Command {

   /** The command name. */
   private final Name name;
   /** The list of parameters. */
   private final Parameter[] parameters;

   /**
    * Constructor creating the instance of the DelegateCommand class.
    * @param name the command name
    * @param parameters the list of parameters of the command
    */
   public DelegateCommand(Name name, @Before("[") @After("]") @Separator(",") Parameter[] parameters) {
      this.name = name;
      this.parameters = parameters;
      System.out.println(name + " " + Arrays.toString(parameters));
   }

   /**
    * @return The command name
    */
   public Name getName() {
      return name;
   }

   /**
    * @return The list of command's parameters
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
