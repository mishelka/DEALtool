package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlInputHandler extends AbstractHtmlHandler {

	private final String INPUT = "input";
	private final String CHECKBOX = "checkbox";
	private final String PASS = "password";
	private final String DATE = "date";
	private final String DATETIME = "datetime";
	private final String EMAIL = "email";
	private final String FILE = "file";
	private final String RESET = "reset";
	private final String TEXT = "text";
	private final String RADIO = "radio";
	private final String FORM = "form";
	private final String IMAGE = "image";
	
	
	
	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isInput(element) | isR(element) ;
		
		
		return matches;
	}
	
	/**
	 * Metoda specificka pre element Input <input>.
	 * @param element
	 * @return
	 */
	private boolean isInput(Element element){
		String tagName = element.getTagName();

		if(tagName.toLowerCase().trim().equals(INPUT)) {

		String typeAttrValue = null;
		try {
			typeAttrValue = XPathHelper.getString("@type", element);
				} catch (XPathExpressionException e) {
					// do nothing - there is no such value
		}

		if(CHECKBOX.equalsIgnoreCase(typeAttrValue)  | RADIO.equalsIgnoreCase(typeAttrValue)
				|PASS.equalsIgnoreCase(typeAttrValue) 
				| DATE.equalsIgnoreCase(typeAttrValue) | DATETIME.equalsIgnoreCase(typeAttrValue)
				| EMAIL.equalsIgnoreCase(typeAttrValue) | FILE.equalsIgnoreCase(typeAttrValue)
				| RESET.equalsIgnoreCase(typeAttrValue) | IMAGE.equalsIgnoreCase(typeAttrValue)
				| TEXT.equalsIgnoreCase(typeAttrValue) ) 
		{
			
				return true;
					}
				}
			return false;
		}
	
	private boolean isR(Element element){
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(FORM);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.FUNCTIONAL;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlInputHandler instance;
	
	public static HtmlInputHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlInputHandler();
		}
		return (HtmlInputHandler) instance;
	}
	
	private HtmlInputHandler() {}
	/*********** End singleton pattern *************/

}
