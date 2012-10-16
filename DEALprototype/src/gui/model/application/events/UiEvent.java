package gui.model.application.events;

import gui.model.application.Scene;
import gui.model.domain.Term;

public abstract class UiEvent {
	private Scene sourceScene;
	//TODO: mozno spravit dalsie triedy pre reprezentaciu roznych typov eventov
	// zmena obrazovky, zmena obsahu
	// graficka zmena, textova zmena, atd.
	// zatial staci len zaznamenavat postupnost udalosti, to ze nastali, potom dalsie udaje
	private Scene targetScene;
	private Term cause;
	
	public Scene getSourceScene() {
		return sourceScene;
	}
	
	public void setSourceScene(Scene sourceScene) {
		this.sourceScene = sourceScene;
	}
	
	public Scene getTargetScene() {
		return targetScene;
	}
	
	public void setTargetScene(Scene targetScene) {
		this.targetScene = targetScene;
	}
	
	public Term getCause() {
		return cause;
	}
	
	public void setCause(Term sourceTerm) {
		this.cause = sourceTerm;
	}
}
