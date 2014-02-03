package gui.model.application.scenes;

import gui.model.application.windows.Window;

public class WindowsScene extends Scene<Window> {

	public WindowsScene(Window sceneContainer) {
		super(sceneContainer);
	}

	@Override
	protected String getSceneName(Window sceneContainer) {
		return sceneContainer.getTitle();
	}
	
	
	@Override
	public String toString() {
		return "Windows window " + super.toString();
	}
}
