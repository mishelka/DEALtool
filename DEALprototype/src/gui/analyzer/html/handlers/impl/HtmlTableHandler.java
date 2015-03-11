package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.analyzer.util.XPathHelper;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;


public class HtmlTableHandler extends AbstractHtmlHandler {
	
	private final String TABLE = "table"; 
	
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
			
			if(Util.isEmpty(value)) {
				value = "TABLE";
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
			return ComponentInfoType.LOGICALLY_GROUPING;
		}

		/**
		 * Overovacia metoda, ktora zistuje o aky element sa jedna.
		 */
		@Override
		public boolean matches(Element element) {
			if(element == null) return false;
			boolean matches = isTable(element);
				
			return matches;
		}
		
		/**
		 * Metoda specificka pre element Table .
		 * @param element
		 * @return
		 */
		private boolean isTable(Element element){
			String tagName = element.getTagName();
			return tagName.equalsIgnoreCase(TABLE);
		}
		
		
		
		@Override
		public boolean extractChildren() {
			return false;
		}
		
		/*************** Singleton pattern *************/
		private static HtmlTableHandler instance;
		
		public static HtmlTableHandler getInstance() {
			if(instance == null) {
				instance  = new HtmlTableHandler();
			}
			return (HtmlTableHandler) instance;
		}
		
		private HtmlTableHandler() {}
		/*********** End singleton pattern *************/
	}


