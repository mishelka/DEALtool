package gui.analyzer.aspects.components;

import java.awt.event.WindowEvent;

public aspect WindowAspect {
	// for windows and dialogs
	pointcut windowPointcut(WindowEvent windowEvent): execution(*
		 *.eventDispatched(*)) && args(windowEvent);

	// zaregistrovanie aktivovaneho okna
	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			//System.out.print(">> Window activated: ");
//			Object o = windowEvent.getSource();
//
//			if (o instanceof Dialog)
//				System.out.println(" it's a dialog");
//			else if (o instanceof JFrame)
//				System.out.println(" it's a frame");
//			else if (o instanceof Window)
//				System.out.println(" it's a window");
//			else
//				System.out.println("it's something else: "
//						+ o.getClass().getSimpleName());
		}
	}
}
