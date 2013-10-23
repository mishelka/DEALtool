package gui.model.application.scenes;

import java.awt.Frame;

/**
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class FrameScene extends Scene<Frame>{

	public FrameScene(Frame sceneContainer) {
		super(sceneContainer);
		// TODO Auto-generated constructor stub
	}
	
	protected String getSceneName(Frame scene) {
		return sceneContainer.getTitle();
	}

	@Override
	public String toString() {
		return "Frame " + super.toString();
	}
}
