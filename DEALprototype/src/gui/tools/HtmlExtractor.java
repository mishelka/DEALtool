package gui.tools;

import gui.analyzer.html.handlers.DefaultHtmlHandler;
import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.html.handlers.HtmlHandlers;
import gui.model.domain.Term;

import org.w3c.dom.Element;

public class HtmlExtractor extends AbstractDealExtractor {

	@Override
	protected <T> void extractSubtree(T element, Term parentTerm) {
		Element e = (Element) element;
		extractSubelement(e, parentTerm);
	}
	
	private void extractSubelement(Element thisElement, Term parentTerm) {
		AbstractHtmlHandler handler = HtmlHandlers.getInstance().getHtmlHandler(thisElement);
		
		if(handler == null) {
			handler = DefaultHtmlHandler.getInstance();
		}
		
		/*
		 * for(each child of thisElement) {
		 *    extractSubelement(child, thisElement);
		 *}
		*/
	}
}
