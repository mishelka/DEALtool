package gui.model.application.scenes;

import java.awt.Dialog;

/**
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class DialogScene extends Scene<Dialog> {
	public DialogScene(Dialog sceneContainer) {
		super(sceneContainer);
		this.name = getSceneName(sceneContainer);
	}
	
	public String getSceneName(Dialog sceneContainer) {
		return sceneContainer.getTitle();
	}
	
	@Override
	public String toString() {
		return "Dialog " + super.toString();
	}
}
