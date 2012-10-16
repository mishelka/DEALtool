package gui.model.domain.relation;

import gui.model.domain.Term;

public class BinaryRelation extends AbstractRelation {
	private Term target;
	private boolean bidirectional;
	
	public Term getTarget() {
		return target;
	}
	
	public void setTarget(Term term) {
		this.target = term;
	}
	
	public boolean isBidirectional() {
		return bidirectional;
	}
	
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}
}
