package gui.tools;

import gui.analyzer.Recorder;
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
		tryRegisterSuperclass(component);
		
		tryRegisterChildren(component);
	}
	
	private <T> void tryRegisterSuperclass(T component) {
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		
		while (componentClass != null) {
			RecordSupport<? super T> recordSupport = RecordSupports
					.getInstance().getRecordSupport(componentClass);
			if (recordSupport != null) {

				recordSupport.register(component, recorder);
				
				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}
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
