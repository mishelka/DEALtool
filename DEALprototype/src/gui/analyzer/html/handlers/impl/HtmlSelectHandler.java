package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlSelectHandler extends AbstractHtmlHandler {
	
	private static final String SELECT = "select";
	private static final String OPTION = "option";
	
	
	
	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isSelect(element) | isOption(element);
		
		return matches;
	}
	/**
	 * Metoda specificka pre element Select <select>.
	 * @param element
	 * @return
	 */
	//<select id="mySelect" size="4">
	 private boolean isSelect(Element element){
		String tagName = element.getTagName();
	
		return tagName.equalsIgnoreCase(SELECT);
		
	}
	
	 /**
	  * Metoda specificka pre element Option <option>.
	  * @param element
	  * @return
	  */
	private boolean isOption(Element element){
		String tagName = element.getTagName();

		return tagName.equalsIgnoreCase(OPTION);

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

		if ((Util.isEmpty(value))) {
			try {
				value = XPathHelper.getString("@value", element);
				
			} catch (XPathExpressionException e1) {
				// do nothing - there is no value
			}
		}
		
		if(Util.isEmpty(value)) {
			try {
				//try first option to be the description
				value = XPathHelper.getString("option[1]/text()", element);
			} catch (XPathExpressionException e) {
				// do nothing - there is no value
			}
		}
		
		return value;
	}

	/**
	 * Metoda vracia popis elementu, ak ho element obsahuje.
	 */
	public String getDomainDescriptor(Element element) {
		return super.getDomainDescriptor(element);
		
	}
	
	@Override
	public Icon getIcon(Element component) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Metoda urcuje vztah medzi prvkami.
	 */
	@Override
	public RelationType getRelation(Element element) {
		if (isSelect(element)) {
			return RelationType.MUTUALLY_EXCLUSIVE;
		}
		return null;
    }

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.LOGICALLY_GROUPING;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlSelectHandler instance;
	
	public static HtmlSelectHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlSelectHandler();
		}
		return (HtmlSelectHandler) instance;
	}
	
	private HtmlSelectHandler() {}
	/*********** End singleton pattern *************/

}
