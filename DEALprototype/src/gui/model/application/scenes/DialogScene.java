package gui.model.application.scenes;


import java.awt.Dialog;

public class DialogScene extends Scene<Dialog> {
	public DialogScene(Dialog sceneContainer) {
		super(sceneContainer);
		this.setName(sceneContainer.getTitle());
	}
}
