package gui.model.application.scenes;

import java.awt.Frame;
import java.awt.Window;

public class WindowScene extends Scene<Window> {

	public WindowScene(Window sceneContainer) {
		super(sceneContainer);
		if(sceneContainer instanceof Frame) {
			super.setName(((Frame)sceneContainer).getTitle());
		}
	}
}
