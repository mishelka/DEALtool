package gui.model.application.events;

import java.util.List;

public class UiEventSequence {
	//TODO: doplnit metody pre lepsie pridavanie a odoberanie eventov a taktiez za ucelom filtracie sekvencii na vyssej urovni
	// napr. vyber vsetky sekvencie ktore sa tykaju toho a toho okna/pojmu
	
	private List<UiEvent> events;
	
	public List<UiEvent> getEvents() {
		return events;
	}
	
	public void setEvents(List<UiEvent> events) {
		this.events = events;
	}
}
