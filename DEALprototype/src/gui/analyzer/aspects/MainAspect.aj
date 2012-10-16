package gui.analyzer.aspects;

import gui.analyzer.filter.GeneralTerm;
import gui.analyzer.filter.GeneralTermsFilter;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;

public aspect MainAspect {
	private ArrayList<Toolkit> toolkits = new ArrayList<Toolkit>();

	// for the main method to be able to use windowpointcut
	// THIS IS THE OLD WAY, HOW TO DETECT WINDOW OPENING IN OTHER WAYS? Is it
	// possible?
	pointcut mainPointcut(): execution(* *.main(*));

	before(): mainPointcut() {
		install();
		loadGeneralTerms();
	}

	private void install() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (!toolkits.contains(toolkit)) {
			toolkits.add(toolkit);
			toolkit.addAWTEventListener(new AWTEventListener() {
				public void eventDispatched(AWTEvent event) {
					// ModelGeneratorAspect/windowPointcut will be executed here
				}
			}, AWTEvent.WINDOW_EVENT_MASK);
			System.out.println("window listener was added");
		}
	}
	
	private void loadGeneralTerms() {
		GeneralTermsFilter filter = new GeneralTermsFilter();
		filter.serialize();
		
		//-----
		System.out.println(">>>> <<<<");
		filter.setGeneralTerms(new ArrayList<GeneralTerm>());
		filter.deserialize();
		for(GeneralTerm gt : filter.getGeneralTerms()) {
			System.out.println(gt);
		}
	}
}
