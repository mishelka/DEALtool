package gui.tools;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.html.handlers.HtmlHandlers;
import gui.model.domain.Term;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

		NodeList nl = thisElement.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if(node instanceof Element) {
				Element e = (Element) node;
				extractSubelement(e, thisTerm);
			}
		}
		
	}
}
