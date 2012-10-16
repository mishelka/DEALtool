package gui.analyzer.aspects;

import gui.analyzer.Inspector;
import gui.model.application.WindowScene;
import gui.ui.DomainModelEditor;

import java.awt.Window;
import java.awt.event.WindowEvent;

public aspect InspectorAspect {
	// for windows and dialogs
	pointcut windowPointcut(WindowEvent windowEvent): execution(*
		 *.eventDispatched(*)) && args(windowEvent);

	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Object o = windowEvent.getSource();

			// this is for displaying in InfoFrame - display a component tree for a window
			if (o instanceof Window
					&& (!(o instanceof DomainModelEditor))
					&& (!DomainModelEditor.getApplication().containsSceneComponent(o))) {
				
				//setup the component tree
				DomainModelEditor.getApplication().addScene(new WindowScene((Window) o));
				DomainModelEditor.getInstance().setupComponentTreeModel();

				// here Inspector is added - lighting a clicked component up with yellow color and displaying it in the component tree
				Inspector.register();
			}
		}
	}
}
