package gui.analyzer.html.handlers;

import gui.analyzer.html.handlers.impl.HtmlButtonHandler;
import gui.analyzer.html.handlers.impl.HtmlTextComponentHandler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class HtmlHandlers {
	private List<AbstractHtmlHandler> htmlHandlers = new ArrayList<AbstractHtmlHandler>();
	private AbstractHtmlHandler defaultHandler = DefaultHtmlHandler.getInstance();
	
	private static HtmlHandlers instance;
	
	//TODO: Valika ak bude novy handler, tu doplnit
	public HtmlHandlers() {
		htmlHandlers.add(HtmlButtonHandler.getInstance());
		htmlHandlers.add(HtmlTextComponentHandler.getInstance());
		//TODO: add new handlers into the htmlHandlers list, otherwise they won't work
		//htmlHandlers.add(...);
		//htmlHandlers.add(...);
		//htmlHandlers.add(...);
		//...
	}
	
	public AbstractHtmlHandler getHtmlHandler(Element element) {
		for (AbstractHtmlHandler htmlHandler : htmlHandlers) {
			if (htmlHandler.matches(element))
				return htmlHandler;
		}
		
		return defaultHandler;
	}
	
	/**
	 * Singleton pattern.
	 * @return HtmlHandlers class instance.
	 */
	public static HtmlHandlers getInstance() {
		if (instance == null) {
			instance = new HtmlHandlers();
		}
		return instance;
	}
}
