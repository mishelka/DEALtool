package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlButtonHandler extends AbstractHtmlHandler {
	//nastavi sa v matches
	private ButtonType buttonType;
	
	
	private static final String BUTTON = "button";
	
	private static final String INPUT = "input";
	private static final String SUBMIT = "submit";
	
	/**
	 * Metoda vracia identifikator elementu.
	 */
	@Override
	//<input type="submit" value="Submit">
	//<button type="button">Click Me!</button>
	//will perform only if matches!
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
		if(buttonType == ButtonType.BUTTON_TYPE) {
			
			
			//...
			//button moze mat aj ikonu, ziskat a vratit ikonu
			//input nemoze mat ikonu
		}
		
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.FUNCTIONAL;
	}

	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isButton(element) || isSubmit(element);
		
		return matches;
	}
	
	/**
	 * Metoda specificka pre element Button.
	 * @param element
	 * @return
	 */
	//<button type="button">Click Me!</button>
	private boolean isButton(Element element) {
		String tagName = element.getTagName();
		
		if(tagName.equalsIgnoreCase(BUTTON)) {
			buttonType = ButtonType.BUTTON_TYPE;
			return true;
		}
	
		return false;
	}
	
	/**
	 * Metoda specificka pre element Submit.
	 * @param element
	 * @return
	 */
	//<input type="submit" value="Submit">
	private boolean isSubmit(Element element) {
		String tagName = element.getTagName();

		if(tagName.toLowerCase().trim().equals(INPUT)) {

		String typeAttrValue = null;
		try {
			typeAttrValue = XPathHelper.getString("@type", element);
				} catch (XPathExpressionException e) {
					// do nothing - there is no such value
		}

		if(SUBMIT.equalsIgnoreCase(typeAttrValue) | BUTTON.equalsIgnoreCase(typeAttrValue)) {
			buttonType = ButtonType.SUBMIT_TYPE; 
				return true;
					}
				}
			return false;
		}

	private enum ButtonType {
		BUTTON_TYPE, SUBMIT_TYPE;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlButtonHandler instance;
	
	public static HtmlButtonHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlButtonHandler();
		}
		return (HtmlButtonHandler) instance;
	}
	
	private HtmlButtonHandler() {}
	/*********** End singleton pattern *************/
}
