package gui.analyzer.windows.handlers;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import org.w3c.dom.Element;

public abstract class AbstractWindowsHandler extends DomainIdentifiable<Element> {
	protected static AbstractWindowsHandler instance;
	
	public abstract boolean matches(Element element);
	
	public Term createTerm(Element element, DomainModel domainModel) {
		Term term = super.createDefaultTerm(element, domainModel);
		if (element != null) {
			if (matches(element)) {
				return super.createTerm(element, domainModel);
			}
		}
		return term;
	}
}
