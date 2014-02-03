package gui.tools;

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
		
		Term thisTerm = handler.createTerm(thisElement, domainModel);
		parentTerm.addChild(thisTerm);

		//TODO: Valika doplnit
		/*
		 * for(each child of thisElement) {
		 *    extractSubelement(child, thisTerm);
		 *}
		*/
	}
}
