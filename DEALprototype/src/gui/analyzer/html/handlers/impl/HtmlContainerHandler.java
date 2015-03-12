package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;

/**
 * Deliberately returns null in all methods.
 * The page title is automatically set during WebPageScene creation, therefore the head title is no longer needed here.
 * Other meta information represent implementation details, that are not important for the domain model.
 */
public class HtmlContainerHandler extends AbstractHtmlHandler {
	private final String DIV = "div";
	private final String SPAN = "span";
	private final String PARAGRAPH = "p";
	
	public String getDomainIdentifier(Element element) {
		return null;
	}
	
	@Override
	public String getDomainDescriptor(Element element) {
		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.CONTAINERS;
	}
	
	@Override
	public Icon getIcon(Element component) {
		return null;
	}

	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		String tagName = element.getTagName();
		return tagName.equalsIgnoreCase(PARAGRAPH) || tagName.equalsIgnoreCase(DIV) || tagName.equalsIgnoreCase(SPAN);
	}
	
	/*************** Singleton pattern *************/
	private static HtmlContainerHandler instance;
	
	public static HtmlContainerHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlContainerHandler();
		}
		return (HtmlContainerHandler) instance;
	}
	
	private HtmlContainerHandler() {}
	/*********** End singleton pattern *************/
}
