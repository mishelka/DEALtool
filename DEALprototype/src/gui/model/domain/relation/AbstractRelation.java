package gui.model.domain.relation;

import gui.model.domain.Term;

public abstract class AbstractRelation {
	private RelationType type = RelationType.AND;
	private String name;
	private Term source;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public RelationType getType() {
		return type;
	}
	
	public void setType(RelationType type) {
		this.type = type;
	}
	
	public Term getSource() {
		return this.source;
	}
	
	public void setSource(Term term) {
		this.source = term;
	}
}
