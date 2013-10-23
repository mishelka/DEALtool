package gui.model.application.scenes;

import java.awt.Frame;
import java.awt.Window;

/**
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class WindowScene extends Scene<Window> {

	public WindowScene(Window sceneContainer) {
		super(sceneContainer);
		this.name = getSceneName(sceneContainer);
	}
	
	protected String getSceneName(Window sceneContainer) {
		if(sceneContainer instanceof Frame) {
			return ((Frame)sceneContainer).getTitle();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Window " + super.toString();
	}
}
