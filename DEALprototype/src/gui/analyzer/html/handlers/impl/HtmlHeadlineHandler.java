package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;


public class HtmlHeadlineHandler extends AbstractHtmlHandler {
	//nastavi sa v matches
	private static final String H_NAME1 = "h1";
	private static final String H_NAME2 = "h2";
	private static final String H_NAME3 = "h3";
	private static final String H_NAME4 = "h4";
	private static final String H_NAME5 = "h5";
	private static final String H_NAME6 = "h6";
	
	/**
	 * Metoda vracia identifikator elementu.
	 */
	@Override
	public String getDomainIdentifier(Element element) {
		String value = null;
		
		if(element == null) return null;
		
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
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.TEXTUAL;
	}

	/**
	 *  Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isH(element);
		
		return matches;
	}
	
	/**
	 * Metoda specificka pre element Nadpisu <h>.
	 * @param element
	 * @return
	 */
	private boolean isH(Element element) {
		String tagName = element.getTagName();
		
		return tagName.equals(H_NAME1) | tagName.equals(H_NAME2) 
				| tagName.equals(H_NAME3) | tagName.equals(H_NAME4)
				| tagName.equals(H_NAME5) | tagName.equals(H_NAME6);
	}
	
	/*************** Singleton pattern *************/
	private static HtmlHeadlineHandler instance;
	
	public static HtmlHeadlineHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlHeadlineHandler();
		}
		return (HtmlHeadlineHandler) instance;
	}
	
	private HtmlHeadlineHandler() {}
	/*********** End singleton pattern *************/


	
}
