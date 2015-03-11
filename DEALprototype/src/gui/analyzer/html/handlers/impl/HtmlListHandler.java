package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public class HtmlListHandler extends AbstractHtmlHandler{
	
	private final String UL = "ul";
	private final String OL = "ol";
	private final String IL = "il";

	
	/**
	 * Overovacia metoda, ktora zistuje o aky element sa jedna.
	 */
	@Override
	public boolean matches(Element element) {
			if(element == null) return false;
			boolean matches = isList(element) | isIl(element);
			
			return matches;
		
	}
	
	/**
	 * Metoda specificka pre elementy typu Zoznam cislovany(ol), necislovany(ul).
	 * @param element
	 * @return
	 */
	private boolean isList(Element element){
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(UL) | tagName.equalsIgnoreCase(OL) ;
	}
	
	/**
	 * Metoda specificka pre element IL, ktory urcuje polozky zoznamu.
	 * @param element
	 * @return
	 */
	private boolean isIl(Element element){
		String tagName = element.getTagName();
		
		return tagName.equalsIgnoreCase(IL);
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
		
		if(Util.isEmpty(value)){
			value = "<LIST>";
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
	public Icon getIcon(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.TEXTUAL;
	}
	
	/**
	 * Metoda urcuje vztah medzi prvkami.
	 */
	@Override
	public RelationType getRelation(Element element) {
		if (isList(element)){
        return RelationType.MUTUALLY_EXCLUSIVE;
			}
		return null;
    }
	
	/*************** Singleton pattern *************/
	private static HtmlListHandler instance;
	
	public static HtmlListHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlListHandler();
		}
		return (HtmlListHandler) instance;
	}
	
	private HtmlListHandler() {}
	/*********** End singleton pattern *************/

}
