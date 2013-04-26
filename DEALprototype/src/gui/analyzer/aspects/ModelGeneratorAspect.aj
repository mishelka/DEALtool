package gui.analyzer.aspects;

import gui.analyzer.Recorder;
import gui.editor.DomainModelEditor;
import gui.model.DomainModelGenerator;
import gui.model.application.Application;
import gui.model.application.DialogScene;
import gui.model.application.FrameScene;
import gui.model.application.Scene;
import gui.model.application.WindowScene;
import gui.model.domain.DomainModel;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Aspect for generating domain models for application scenes.
 */
privileged aspect ModelGeneratorAspect {
	private DomainModelGenerator generator;
	private DomainModelEditor editor;
	private Recorder recorder;

	public ModelGeneratorAspect() {
		editor = DomainModelEditor.getInstance();
		editor.setVisible(true);

		generator = new DomainModelGenerator();
		
		//Create a recorder and set it to the editor and generator
		if((recorder = editor.getRecorder()) == null) {
			recorder = new Recorder();
			editor.setRecorder(recorder);
			generator.setRecorder(recorder);
		}
	}

	/**
	 * Pointcut for newly created scenes (windows, frames, dialogs).
	 * @param windowEvent a window event which contains the target scene as source.
	 */
	public pointcut windowPointcut(WindowEvent windowEvent):
		execution(* *.eventDispatched(*)) && args(windowEvent);
	
	/**
	 * Calls the DomainModelGenerator for each new opened scene (a window, frame or dialog).
	 * If the newly opened scene is the first one of the opened application, then a new DomainModel is created as a primary domain model.
	 * If the newly opened scene is a subscene of the first application scene, then a new DomainModel is created and it is added into the primary domain model as a subnode.
	 * @param windowEvent a window event which contains the target scene as source.
	 */
	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		DomainModel newModel = null;
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Object o = windowEvent.getSource();
			if (!(o instanceof DomainModelEditor)) {

				String title = null;
				@SuppressWarnings("rawtypes")
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
					
					@SuppressWarnings("static-access")
					Application app = editor.getApplication();

					if (! app.contains(scene)) {
						scene.setName(title);
						if (app.getScenes().size() == 0)
							app.setName(title);
					}

					try {
						newModel = generator.createDomainModel(scene, title);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(newModel != null) {
						editor.addDomainModel(scene, newModel);
					}
				}
			}
		}
	}
}
