package gui.model.application.events;

import java.util.ArrayList;
import java.util.List;

public class UiEventSequence {
	// TODO: doplnit metody pre lepsie pridavanie a odoberanie eventov a taktiez
	// za ucelom filtracie sekvencii na vyssej urovni
	// napr. vyber vsetky sekvencie ktore sa tykaju toho a toho okna/pojmu

	private List<UiEvent> events = new ArrayList<UiEvent>();

	public List<UiEvent> getEvents() {
		return events;
	}

	public void setEvents(List<UiEvent> events) {
		this.events = events;
	}

	/**
	 * Adds a new event to the sequence, but only if the given event is not
	 * null.
	 * 
	 * @param event
	 *            The event to add to the sequence.
	 */
	public void add(UiEvent event) {
		events.add(event);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int i = 1;
		for(UiEvent event : events) {
			sb.append(i + ". ");
			sb.append(event.toString());
			sb.append("\n");
			i++;
		}
		
		return sb.toString();
	}
}
