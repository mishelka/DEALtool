package gui.model.application.program;

/**
 * An abstract program command. Defines the voluntary abstract method execute, which
 * executes this command on a given component.
 */
public abstract class Command {
   /**
    * Executes the command on a given component.
    * @param <T> class of the given component
    * @param component the component, on which the command should be executed
    * @return true if the command was executed properly, false otherwise
    */
   public abstract <T> boolean execute(T component);
}
