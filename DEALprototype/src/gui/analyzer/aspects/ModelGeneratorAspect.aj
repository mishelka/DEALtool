package gui.analyzer.aspects;

import gui.editor.DomainModelEditor;
import gui.editor.FindDialog;
import gui.model.application.Application;
import gui.model.application.scenes.DialogScene;
import gui.model.application.scenes.Scene;
import gui.model.application.scenes.WindowScene;
import gui.tools.DomainModelGenerator;
import gui.tools.DuplicateSceneDetector;
import gui.tools.Recorder;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;

/**
 * Aspect for generating domain models for application scenes.
 */
public privileged aspect ModelGeneratorAspect {
	private DomainModelGenerator generator;
	private DomainModelEditor editor;
	private DuplicateSceneDetector detector;
	private Recorder recorder;
	private Application application;

	/**
	 * Creates new ModelGeneratorAspect instance.
	 * Gets the DomainModelEditor instance.
	 * Creates a Recorder instance and stores it to the editor.
	 * Creates a new DomainModelGenerator instance.
	 * Creates a new DuplicateSceneDetector instance.
	 * Gets an Application instance from the editor.
	 * Adds the editor as an observer to the Application.
	 */
	@SuppressWarnings("static-access")
	public ModelGeneratorAspect() {
		editor = DomainModelEditor.getInstance();
		editor.setVisible(true);

		// Create a recorder and set it to the editor and generator
		if ((recorder = editor.getRecorder()) == null) {
			recorder = new Recorder();
			editor.setRecorder(recorder);
		}

		generator = new DomainModelGenerator(recorder);
		detector = new DuplicateSceneDetector();
		
		application = editor.getApplication();
		
		application.addObserver(editor);
	}

	/**
	 * Pointcut for newly created scenes (windows, frames, dialogs).
	 * 
	 * @param windowEvent
	 *            a window event which contains the target scene as source.
	 */
	public pointcut windowPointcut(WindowEvent windowEvent):
		execution(* *.eventDispatched(*)) && args(windowEvent);

	/**
	 * Calls the DomainModelGenerator for each new opened scene (a window, frame
	 * or dialog). If the newly opened scene is the first one of the opened
	 * application, then a new DomainModel is created as a primary domain model.
	 * If the newly opened scene is a subscene of the first application scene,
	 * then a new DomainModel is created and it is added into the primary domain
	 * model as a subnode.
	 * 
	 * @param windowEvent
	 *            a window event which contains the target scene as source.
	 */
	after(WindowEvent windowEvent): windowPointcut(windowEvent) {
		if (windowEvent.getID() == WindowEvent.WINDOW_ACTIVATED) {
			Window w = windowEvent.getWindow();
			onWindowActivated(w);
		}
	}
	
	/**
	 * For each activated window <code>w</code> creates a new <code>Scene</code> instance,
	 * registers the Inspector (if there already is one registered, then it will not be registered again),
	 * reloads the editor and generates a new domain model for the window.
	 * If there already is such domain model in the list of all existing domain models,
	 * then it replaces the old one with the new.
	 * @param w the window, for which a Scene should be created and a domain model should be generated.
	 */
	private void onWindowActivated(Window w) {
		if (!(w instanceof DomainModelEditor) && !(w instanceof FindDialog)) {
			DomainModelEditor.getInstance().addApplicationWindow(w);
			
			Scene<?> scene = createScene(w);

			// register inspector
			registerInspector();

			if (application.getSceneCount() == 0)
				editor.setupComponentTreeModel();
			
			try {
				generator.createDomainModel(scene);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			detectAndDeletePreviousModel(scene, w);
			
			addNewScene(scene);
		}
	}
	
	/**
	 * Detects and removes previous Scene from the Application.
	 * @param scene the Scene instance, for which the previous instance is to be detected and removed
	 * @param w the window, which is the scene component
	 */
	private void detectAndDeletePreviousModel(Scene<?> scene, Window w) {
		if(application.contains(scene)) {
			application.removeScene(scene);
		} else {
			Scene<?> matchedScene = detector.detect(w, scene.getDomainModel());
			if(matchedScene != null) {
				application.removeScene(matchedScene);
			}
		}
	}

	/**
	 * Adds a new Scene instance into the Application's scene list.
	 * @param scene the Scene to be added.
	 */
	private void addNewScene(Scene<?> scene) {
		application.addScene(scene);
	}

	/**
	 * Creates a new Scene instance with the given Window component.
	 * @param window the component, the Scene should be created for
	 * @return the newly created Scene with the given Window as the scene component
	 */
	@SuppressWarnings("unchecked")
	private Scene<?> createScene(Window window) {
		@SuppressWarnings("rawtypes")
		// search if there is already a scene created for this component
		Scene scene = application.getSceneForSceneComponent(window);

		// if there is no such scene, create a new scene
		if (scene == null)
			if (window instanceof Dialog)
				scene = new DialogScene((Dialog) window);
			else
				scene = new WindowScene(window);
		// else update the scene name to a new one
		else {
			scene.updateName(window);
		}
		
		return scene;
	}

	/**
	 * Registers the Inspector.
	 */
	private void registerInspector() {
		//disabled for now
		//Inspector.register();
	}
}
