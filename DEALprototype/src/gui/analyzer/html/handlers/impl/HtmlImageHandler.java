package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlImageHandler extends AbstractHtmlHandler{
	
	private final String IMAGE = "img";
	
	

	/**
	 *  Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isImage(element);
			
		return matches;
	}
	
	/**
	 * Metoda specificka pre element Obrazok <img>.
	 * @param element
	 * @return
	 */
	public boolean isImage(Element element){
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(IMAGE);
	}

	/**
	 * Metoda vracia identifikator elementu.
	 */
	@Override
	public String getDomainIdentifier(Element element) {
		String value = null;
		
		if(element == null) return null;
		
		try {
			value = XPathHelper.getString("src()", element);
		} catch (XPathExpressionException e1) {
			// do nothing - there is no value
		}

		if (Util.isEmpty(value)) {
			try {
				value = XPathHelper.getString("@alt", element);
			} catch (XPathExpressionException e1) {
				// do nothing - there is no value
			}
		}
		
		return value;
	}
	
	/**
	 * Metoda vracia popis elementu, ak ho element obsahuje.
	 */
	@Override
	public String getDomainDescriptor(Element element) {
		return super.getDomainDescriptor(element);
	
	}
	
	@Override
	public Icon getIcon(Element element) {
		
		return null;
	}
	
	

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.FUNCTIONAL;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlImageHandler instance;
	
	public static HtmlImageHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlImageHandler();
		}
		return (HtmlImageHandler) instance;
	}
	
	private HtmlImageHandler() {}
	/*********** End singleton pattern *************/

}
