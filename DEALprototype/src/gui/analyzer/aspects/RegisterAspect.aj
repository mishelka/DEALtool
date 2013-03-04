package gui.analyzer.aspects;

import gui.analyzer.Inspector;
import gui.editor.DomainModelEditor;
import gui.model.application.WindowScene;

import java.awt.Window;
import java.awt.event.WindowEvent;

/**
 * Registers Inspector (for coloring in yellow) and Recorder and prepares a DomainModelEditor instance.
 */
public aspect RegisterAspect {

	// for windows and dialogs
	pointcut windowPointcut(WindowEvent windowEvent): execution(*
		 *.eventDispatched(*)) && args(windowEvent);

	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Object o = windowEvent.getSource();

			// this is for displaying in editor - display a component tree for a window
			if (o instanceof Window
					&& (!(o instanceof DomainModelEditor))
					&& (!DomainModelEditor.getApplication().containsSceneComponent(o))) {
				
				//setup the component tree
				DomainModelEditor.getApplication().addScene(new WindowScene((Window) o));
				DomainModelEditor editorInstance = DomainModelEditor.getInstance();
				editorInstance.setupComponentTreeModel();
				
				// here Inspector is added - lighting a clicked component up with yellow color and displaying it in the component tree
				Inspector.register();
			}
		}
	}
}
