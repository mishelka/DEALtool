package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;


public class HtmlLinkHandler extends AbstractHtmlHandler {
	//nastavi sa v matches
	private static final String A_NAME = "a";

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
		return ComponentInfoType.FUNCTIONAL;
	}

	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isA(element);

		return matches;
	}
	
	
	/**
	 * Metoda specificka pre element Odkaz <a>.
	 * @param element
	 * @return
	 */
	//<a target="_top" href="dom_obj_anchor.asp">&lt;a&gt; </a>
	private boolean isA(Element element) {
		String tagName = element.getTagName();
		return tagName.equals(A_NAME);
	}
	
	/*************** Singleton pattern *************/
	private static HtmlLinkHandler instance;
	
	public static HtmlLinkHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlLinkHandler();
		}
		return (HtmlLinkHandler) instance;
	}
	
	private HtmlLinkHandler() {}
	/*********** End singleton pattern *************/
}
