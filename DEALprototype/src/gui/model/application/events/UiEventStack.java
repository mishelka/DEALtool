package gui.model.application.events;

import gui.model.domain.Term;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class UiEventStack extends ArrayList<UiEvent> {
	
	private Term term;
	
	public UiEventStack(Term t) {
		super();
		this.term = t;
	}

	public UiEventStack(Collection<? extends UiEvent> c, Term t) {
		super(c);
		this.term = t;
	}

	public UiEventStack(int initialCapacity, Term t) {
		super(initialCapacity);
		this.term = t;
	}
	
	public Term getTerm() {
		return term;
	}
}
