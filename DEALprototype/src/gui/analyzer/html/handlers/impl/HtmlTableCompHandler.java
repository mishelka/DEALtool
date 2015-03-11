package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlTableCompHandler extends AbstractHtmlHandler{
	
	private final String TH = "th";
	private final String TR = "tr";
	private final String TD = "td";

	/**
	 *  Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isTableComp(element);
			
		return matches;
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
	public Icon getIcon(Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element component) {
		return ComponentInfoType.TEXTUAL;
	}
	
	/**
	 * Metoda specificka pre elementy, ktore obsahuje tabulka.
	 * @param element
	 * @return
	 */
	private boolean isTableComp(Element element) {
		String tagName = element.getTagName();
				
		return  tagName.equalsIgnoreCase(TH)
				| tagName.equalsIgnoreCase(TR) | tagName.equalsIgnoreCase(TD);
	}
	
	/*************** Singleton pattern *************/
	private static HtmlTableCompHandler instance;
	
	public static HtmlTableCompHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlTableCompHandler();
		}
		return (HtmlTableCompHandler) instance;
	}
	
	private HtmlTableCompHandler() {}
	/*********** End singleton pattern *************/

}
