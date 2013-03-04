package gui.analyzer.aspects;

import gui.analyzer.util.Logger;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;

public aspect MainAspect {
	private ArrayList<Toolkit> toolkits = new ArrayList<Toolkit>();

	// For the main method to be able to use windowpointcut
	// THIS IS THE OLD WAY. HOW TO DETECT WINDOW OPENING IN OTHER WAYS? Is it
	// possible?
	pointcut mainPointcut(): execution(* *.main(*));

	/**
	 * Pointcut for the main method.
	 * Also Installs a Window event listener.
	 */
	before(): mainPointcut() {
		String className = thisJoinPoint.getSignature().getDeclaringType().getName();
		install(className);
	}

	private void install(String className) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (!toolkits.contains(toolkit)) {
			toolkits.add(toolkit);
			toolkit.addAWTEventListener(new AWTEventListener() {
				public void eventDispatched(AWTEvent event) {
					// ModelGeneratorAspect/windowPointcut will be executed here
				}
			}, AWTEvent.WINDOW_EVENT_MASK);
			
			Logger.logError(">>> Window listener was added to \"" + className + "\"");
		}
	}
}
