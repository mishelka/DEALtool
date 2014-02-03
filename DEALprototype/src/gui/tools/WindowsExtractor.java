package gui.tools;

import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.analyzer.windows.handlers.WindowsHandlers;
import gui.model.domain.Term;

import org.w3c.dom.Element;

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

		//TODO: Peter doplnit
		/*
		 * for(each child of thisElement) {
		 *    extractSubelement(child, thisTerm);
		 *}
		*/
	}
}
