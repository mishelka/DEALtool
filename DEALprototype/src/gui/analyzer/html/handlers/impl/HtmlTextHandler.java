package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlTextHandler extends AbstractHtmlHandler {
	
	private final String PARAGRAF = "p";
	private final String TITLE = "title";
	private final String CAPTION = "caption";
	private final String DIV = "div";
	private final String SPAN = "span";
		
	

	/**
	 *  Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isText(element);
		
		
		return matches;
	}

	/**
	 * Metoda specificka pre elementy, ktore obsahuju textovu informaciu.
	 * @param element
	 * @return
	 */
	private boolean isText(Element element) {
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(PARAGRAF)  | tagName.equalsIgnoreCase(TITLE)
				| tagName.equalsIgnoreCase(CAPTION) | tagName.equalsIgnoreCase(DIV)
				| tagName.equalsIgnoreCase(SPAN);
	}
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.TEXTUAL;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlTextHandler instance;
	
	public static HtmlTextHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlTextHandler();
		}
		return (HtmlTextHandler) instance;
	}
	
	private HtmlTextHandler() {}
	/*********** End singleton pattern *************/

}