package gui.tools;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.handlers.RecordSupports;
import gui.model.application.scenes.Scene;

public class RecorderRegistator {
	private Recorder recorder;
	
	public RecorderRegistator(Recorder recorder) {
		this.recorder = recorder;
	}
	
	public void rEGISTER_RECORDER(Scene<?> scene) {
		registerComponentTree(scene.getSceneContainer());
	}
	
	private <T> void registerComponentTree(T component) {
		boolean continueToChildren = tryRegisterSuperclass(component);
		
		if(continueToChildren) tryRegisterChildren(component);
	}
	
	private <T> boolean tryRegisterSuperclass(T component) {
		boolean continueToChildren = true;
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		
		while (componentClass != null) {
			RecordSupport<? super T> recordSupport = RecordSupports
					.getInstance().getRecordSupport(componentClass);
			if (recordSupport != null) {
				continueToChildren = recordSupport.register(component, recorder);
				
				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}
		return continueToChildren;
	}
	
	private <T> void tryRegisterChildren(T component) {
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Composite<? super T> composite = Composites.getInstance().getComposite(
				componentClass);
		if (composite != null) {
			for (Object subcomponent : composite.getComponents(component)) {
				registerComponentTree(subcomponent);
			}
		}
	}
}
