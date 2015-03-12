package gui.analyzer.html.handlers;


import gui.analyzer.html.handlers.impl.*;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class HtmlHandlers {
	private List<AbstractHtmlHandler> htmlHandlers = new ArrayList<AbstractHtmlHandler>();
	private AbstractHtmlHandler defaultHandler = DefaultHtmlHandler.getInstance();
	
	private static HtmlHandlers instance;
	
	
	public HtmlHandlers() {
		htmlHandlers.add(HtmlButtonHandler.getInstance());
		htmlHandlers.add(HtmlContainerHandler.getInstance());
		htmlHandlers.add(HtmlHeadHandler.getInstance());
		htmlHandlers.add(HtmlHeadlineHandler.getInstance());
		htmlHandlers.add(HtmlImageHandler.getInstance());
		htmlHandlers.add(HtmlInputHandler.getInstance());
		htmlHandlers.add(HtmlLabelHandler.getInstance());
		htmlHandlers.add(HtmlLinkHandler.getInstance());
		htmlHandlers.add(HtmlListHandler.getInstance());
		htmlHandlers.add(HtmlSelectHandler.getInstance());
		htmlHandlers.add(HtmlTableCompHandler.getInstance());
		htmlHandlers.add(HtmlTableHandler.getInstance());
		htmlHandlers.add(HtmlTextComponentHandler.getInstance());
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
