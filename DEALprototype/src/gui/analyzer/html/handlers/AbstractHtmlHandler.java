package gui.analyzer.html.handlers;

import javax.xml.xpath.XPathExpressionException;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import org.w3c.dom.Element;

public abstract class AbstractHtmlHandler extends DomainIdentifiable<Element> {
	public abstract boolean matches(Element element);
	
	@Override
	public String getDomainDescriptor(Element element) {
		String value = null;
		
		try {
			value = XPathHelper.getString("@title", element);
		} catch (XPathExpressionException e1) {
			//  do nothing - there is no value
		}
		
		if(Util.isEmpty(value)) {
			try {
				value = XPathHelper.getString("@alt", element);
			} catch (XPathExpressionException e1) {
				//  do nothing - there is no value
			}
		}
		
		return value;
	}
	
	public Term createTerm(Element element, DomainModel domainModel) {
		Term term = super.createDefaultTerm(element, domainModel);
		if (element != null) {
			if (matches(element)) {
				term = super.createTerm(element, domainModel);
				term.setComponentClassHtml(Util.nodeToClass(element));
			}
		}
		return term;
	}
}
