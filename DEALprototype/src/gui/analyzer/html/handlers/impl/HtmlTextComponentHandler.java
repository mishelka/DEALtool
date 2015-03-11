package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;




public class HtmlTextComponentHandler extends AbstractHtmlHandler {
	
	private static final String TEXT = "textarea";
	
	

	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isTextComponent(element);
		
			return matches;
	}

	/**
	 * Metoda specificka pre element textoveho pola <textarea>.
	 * @param element
	 * @return
	 */
	private boolean isTextComponent(Element element){
		
		String tagName = element.getTagName();
		
		if (tagName.equalsIgnoreCase(TEXT)){
			
		try {
				XPathHelper.getString("@id", element);
			} catch (XPathExpressionException e) {
				// do nothing - there is no such value
			}
			
			return tagName.equalsIgnoreCase(TEXT);
			}

		return false;
	}
	

	/**
	 * Metoda vracia identifikator elementu.
	 */
	@Override
	public String getDomainIdentifier(Element element) {
		
			String value = null;
			
			try {
				value = XPathHelper.getString("text()", element);
			} catch (XPathExpressionException e1) {
				// do nothing - there is no value
			}

			if (Util.isEmpty(value)) {
				try {
					value = XPathHelper.getString("@value", element);
				} catch (XPathExpressionException e1) {
					// do nothing - there is no value
				
			}
				if(Util.isEmpty(value)) {
					value = "TextArea";
				}
			
			return value;
		}
		return null;
	}

	/**
	 * Metoda vracia popis elementu, ak ho element obsahuje.
	 */
	@Override
	public String getDomainDescriptor(Element element) {
		return super.getDomainDescriptor(element);
		
	}

	@Override
	public Icon getIcon(Element component) {
		
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.TEXTUAL;
	}

	/*************** Singleton pattern *************/
	private static HtmlTextComponentHandler instance;
	public static HtmlTextComponentHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlTextComponentHandler();
		}
		return (HtmlTextComponentHandler) instance;
	}
	
	private HtmlTextComponentHandler() {}
	/*********** End singleton pattern *************/
}
