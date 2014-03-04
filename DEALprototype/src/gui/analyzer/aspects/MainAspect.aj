package gui.analyzer.aspects;

import gui.analyzer.util.Logger;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;

/**
 * The main aspect of DEAL.
 * The AWTEventListener is registered to the toolkit for the new windows and dialogs
 * to be registered.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public aspect MainAspect {
	/** The list of all toolkits - if we already have the toolkit in the list, we do not register it. */
	private ArrayList<Toolkit> toolkits = new ArrayList<Toolkit>();

	// For the main method to be able to use windowpointcut
	// THIS IS THE OLD WAY. HOW TO DETECT WINDOW OPENING IN OTHER WAYS? Is it
	// possible?
	pointcut mainPointcut(): execution(* *.main(*));

	/**
	 * Advice for the main method.
	 * Gets the name of the application and installs the a window event listener.
	 * The events will be handler in {@see gui.analyzer.aspects.ModelGeneratorAspect}.
	 */
	before(): mainPointcut() {
		String className = thisJoinPoint.getSignature().getDeclaringType()
				.getName();
		install(className);
	}

	/**
	 * Installs the AWTEventListener on the application
	 * and if successful, logs the name of the application into the console.
	 * @param className the name of the application
	 */
	private void install(String className) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (!toolkits.contains(toolkit)) {
			toolkits.add(toolkit);
			toolkit.addAWTEventListener(new AWTEventListener() {
				public void eventDispatched(AWTEvent event) {
					// ModelGeneratorAspect/windowPointcut will be executed here
				}
			}, AWTEvent.WINDOW_EVENT_MASK);
			
			Logger.log(">>> Window listener was added to \"" + className + "\"");
		}
	}
}
