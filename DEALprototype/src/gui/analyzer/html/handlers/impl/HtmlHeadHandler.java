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
public class HtmlHeadHandler extends AbstractHtmlHandler {
	private static final String HEAD = "head";
	
	public String getDomainIdentifier(Element element) {
		return null;
	}
	
	@Override
	public String getDomainDescriptor(Element element) {
		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.DESCRIPTIVE;
	}
	
	@Override
	public Icon getIcon(Element component) {
		return null;
	}

	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(HEAD);
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlHeadHandler instance;
	
	public static HtmlHeadHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlHeadHandler();
		}
		return (HtmlHeadHandler) instance;
	}
	
	private HtmlHeadHandler() {}
	/*********** End singleton pattern *************/
}
