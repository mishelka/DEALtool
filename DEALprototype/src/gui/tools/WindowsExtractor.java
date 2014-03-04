package gui.tools;

import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.analyzer.windows.handlers.WindowsHandlers;
import gui.model.domain.Term;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WindowsExtractor extends AbstractDealExtractor {

	@Override
	protected <T> void extractSubtree(T element, Term parentTerm) {
		Element e = (Element) element;
		extractSubelement(e, parentTerm);
	}
	
	private void extractSubelement(Element thisElement, Term parentTerm) {
		AbstractWindowsHandler handler = WindowsHandlers.getInstance().getWindowsHandler(thisElement);
		
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
