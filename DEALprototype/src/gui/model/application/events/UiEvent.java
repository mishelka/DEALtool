package gui.model.application.events;

import gui.model.application.scenes.Scene;
import gui.model.domain.Term;

import java.util.Arrays;

public class UiEvent {
	private Scene<?> sourceScene;
	// TODO: mozno spravit dalsie triedy pre reprezentaciu roznych typov eventov
	// zmena obrazovky, zmena obsahu
	// graficka zmena, textova zmena, atd.
	// zatial staci len zaznamenavat postupnost udalosti, to ze nastali, potom
	// dalsie udaje
	private Scene<?> targetScene; // ako toto zistit?
	private Term cause;
	private Object component;
	// TODO: in the future this will be replaced by a list of default actions,
	// for now it is a list of strings
	private String[] commands;

	public UiEvent(Object component) {
		this.component = component;
	}

	public Scene<?> getSourceScene() {
		return sourceScene;
	}

	public void setSourceScene(Scene<?> sourceScene) {
		this.sourceScene = sourceScene;
	}

	public Scene<?> getTargetScene() {
		return targetScene;
	}

	public void setTargetScene(Scene<?> targetScene) {
		this.targetScene = targetScene;
	}

	public Term getCause() {
		return cause;
	}

	public void setCause(Term sourceTerm) {
		this.cause = sourceTerm;
	}

	public Object getComponent() {
		return component;
	}

	public String[] getCommands() {
		return commands;
	}

	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{" + cause + "}");

		sb.append(" ");

		if (commands != null && commands.length != 0) {
			sb.append("[");
			sb.append(Arrays.toString(commands));
			sb.append("]");
		}

		return sb.toString();
	}
}
