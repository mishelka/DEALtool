package gui.analyzer.aspects.components;

import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

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
//				Logger.log(" it's a dialog");
//			else if (o instanceof JFrame)
//				Logger.log(" it's a frame");
//			else if (o instanceof Window)
//				Logger.log(" it's a window");
//			else
//				Logger.log("it's something else: "
//						+ o.getClass().getSimpleName());
		}
	}
}
