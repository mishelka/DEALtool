package gui.tools.listener;

import gui.model.application.events.UiEvent;
import gui.model.application.events.UiEventSequence;

public interface UiEventListener {

	public void uiEventRecorded(UiEvent uiEvent, UiEventSequence uiEvSeq);

}
