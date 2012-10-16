package gui.analyzer.aspects;

import gui.model.DomainModelGenerator;
import gui.model.application.Application;
import gui.model.application.DialogScene;
import gui.model.application.FrameScene;
import gui.model.application.Scene;
import gui.model.application.WindowScene;
import gui.model.domain.DomainModel;
import gui.ui.DomainModelEditor;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JFrame;

privileged aspect ModelGeneratorAspect {
	private DomainModelGenerator generator;
	private DomainModelEditor editor;

	public ModelGeneratorAspect() {
		editor = DomainModelEditor.getInstance();
		editor.setVisible(true);

		generator = new DomainModelGenerator();
	}

	// for windows and dialogs
	public pointcut windowPointcut(WindowEvent windowEvent): execution(* *.eventDispatched(*)) && args(windowEvent);

	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		DomainModel newModel = null;
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Object o = windowEvent.getSource();
			if (!(o instanceof DomainModelEditor)) {

				String title = null;
				Scene scene = null;

				if (o instanceof Window) {
					Window w = (Window) o;

					if (o instanceof JFrame) {
						title = ((JFrame) o).getTitle();
						scene = new FrameScene((JFrame) o);
					} else if (o instanceof Dialog) {
						title = ((Dialog) o).getTitle();
						scene = new DialogScene((Dialog) o);
					} else {
						scene = new WindowScene(w);
					}
					
					Application app = editor.getApplication();

					if (! app.contains(scene)) {
						scene.setName(title);
						if (app.getScenes().size() == 0)
							app.setName(title);
					}

					newModel = generator.createDomainModel(scene, title);
					
					if(newModel != null) {
						editor.addDomainModel(scene, newModel);
					}
				}
			}
		}
	}
}
