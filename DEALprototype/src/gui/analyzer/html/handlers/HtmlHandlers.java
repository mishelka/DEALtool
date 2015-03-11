package gui.analyzer.html.handlers;


import gui.analyzer.html.handlers.impl.HtmlButtonHandler;
import gui.analyzer.html.handlers.impl.HtmlHeadlineHandler;
import gui.analyzer.html.handlers.impl.HtmlImageHandler;
import gui.analyzer.html.handlers.impl.HtmlInputHandler;
import gui.analyzer.html.handlers.impl.HtmlLabelHandler;
import gui.analyzer.html.handlers.impl.HtmlLinkHandler;
import gui.analyzer.html.handlers.impl.HtmlListHandler;
import gui.analyzer.html.handlers.impl.HtmlSelectHandler;
import gui.analyzer.html.handlers.impl.HtmlTableCompHandler;
import gui.analyzer.html.handlers.impl.HtmlTableHandler;
import gui.analyzer.html.handlers.impl.HtmlTextComponentHandler;
import gui.analyzer.html.handlers.impl.HtmlTextHandler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class HtmlHandlers {
	private List<AbstractHtmlHandler> htmlHandlers = new ArrayList<AbstractHtmlHandler>();
	private AbstractHtmlHandler defaultHandler = DefaultHtmlHandler.getInstance();
	
	private static HtmlHandlers instance;
	
	
	public HtmlHandlers() {
		htmlHandlers.add(HtmlButtonHandler.getInstance());
		htmlHandlers.add(HtmlLinkHandler.getInstance());
		htmlHandlers.add(HtmlTableHandler.getInstance());
		htmlHandlers.add(HtmlTextComponentHandler.getInstance());
		htmlHandlers.add(HtmlSelectHandler.getInstance());
		htmlHandlers.add(HtmlLabelHandler.getInstance());
		htmlHandlers.add(HtmlHeadlineHandler.getInstance());
		htmlHandlers.add(HtmlTextHandler.getInstance());
		htmlHandlers.add(HtmlImageHandler.getInstance());
		htmlHandlers.add(HtmlInputHandler.getInstance());
		htmlHandlers.add(HtmlTableCompHandler.getInstance());
		htmlHandlers.add(HtmlListHandler.getInstance());
		
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
