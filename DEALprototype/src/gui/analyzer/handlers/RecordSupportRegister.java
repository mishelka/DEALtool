package gui.analyzer.handlers;

import gui.analyzer.Recorder;
import gui.model.domain.DomainModel;

/**
 * An abstract class defining an interface for a record support handler.
 * @author Michaela Baèíková <michaela.bacikova@tuke.sk>
 * @param <T> Component class type.
 */
public interface RecordSupportRegister<T> {
   /**
    * Registers an event listener for the given component and adds a recorder for this component.
    * @param component the component which the recorder should be registered for.
    * @param recorder The recorder, which should be added for the given component.
    * @return true if the given component is a composite and DEAL can continue to get subcomponents of the given component during traversing, false otherwise.
    */
   public boolean register(T component, Recorder recorder);
}