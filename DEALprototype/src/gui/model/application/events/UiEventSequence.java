package gui.model.application.events;

import gui.model.domain.Term;

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
	
	private List<UiEventStack> stacks = new ArrayList<UiEventStack>();

	/**
	 * @return the list of UI events representing the sequence
	 */
	public List<UiEvent> getEvents() {
		return events;
	}
	
	public UiEvent getPenultimate() {
		if(getEventCount() > 1) {
			System.out.println(">>>>>"+ events.get(getEventCount() - 2));
			return events.get(getEventCount() - 2);
		} else return null;
	}
	
	/**
	 * @param events the list of UI events reprsenting the sequence
	 */
	public void setEvents(List<UiEvent> events) {
		this.events = events;
	}

	public int getEventCount() {
		return events.size();
	}
	
	/**
	 * Adds a new event to the sequence, but only if the given event is not
	 * null.
	 * @param event the event to be added to the sequence.
	 */
	public void add(UiEvent event) {
		events.add(event);
		
		//add event to stack
		Term term = event.getCause();
		UiEventStack lastStack = getLastStack();
		if(term != null && lastStack != null 
				&& term.equals(lastStack.getTerm())) {
				lastStack.add(event);
		} else {
			UiEventStack stack = new UiEventStack(term);
			stacks.add(stack);
			stack.add(event);
		}
	}
	
	public UiEventStack getLastStack() {
		if(stacks.isEmpty()) return null;
		return stacks.get(stacks.size()-1);
	}
	
	public UiEventStack getStack(int i) {
		return stacks.get(i);
	}
	
	public int getLastStackSize() {
		if(getLastStack() == null) return -1;
		return getLastStack().size();
	}
	
	public int getStackCount() {
		return stacks.size();
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
