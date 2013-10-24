package gui.model.application.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the sequence of UI events.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class UiEventSequence {
	// TODO: doplnit metody pre lepsie pridavanie a odoberanie eventov a taktiez
	// za ucelom filtracie sekvencii na vyssej urovni
	// napr. vyber vsetky sekvencie ktore sa tykaju toho a toho okna/pojmu
	/** The list of UI events representing the sequence */
	private List<UiEvent> events = new ArrayList<UiEvent>();

	/**
	 * @return the list of UI events representing the sequence
	 */
	public List<UiEvent> getEvents() {
		return events;
	}

	/**
	 * @param events the list of UI events reprsenting the sequence
	 */
	public void setEvents(List<UiEvent> events) {
		this.events = events;
	}

	/**
	 * Adds a new event to the sequence, but only if the given event is not
	 * null.
	 * @param event the event to be added to the sequence.
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
