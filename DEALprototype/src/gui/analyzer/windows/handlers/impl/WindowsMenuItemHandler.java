package gui.analyzer.windows.handlers.impl;

import gui.analyzer.util.Util;
import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * <element capabilities="accessible" enabled="True" hasfocus="True"
 * index="4" role="menuitem" visible="False">
 * 
 * <attribute name="text" type="System.String"> Save copy as... </attribute>
 * 
 * 
 * 
 * 
 * <children/> </element>
 */
public class WindowsMenuItemHandler extends AbstractWindowsHandler {

	@Override
	/**
	 * Vrati primarnu informaciu o komponente - napr. nazov tlacidla "Submit"
	 */
	public String getDomainIdentifier(Element element) {
		String value = null;
		
		NodeList nl = element.getElementsByTagName("attribute");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n instanceof Element) {
				Element e = (Element) n;
				
				String attr = e.getAttribute("name");
				if(!Util.isEmpty(attr) && attr.equals("text")) {
					value = e.getTextContent();
					if(!Util.isEmpty(value)) value = value.trim();
					System.out.println(value);
				}
			}
		}
		
		return value;
	}

	/**
	 * Vrati sekundarnu informaciu o komponente, napr. tooltip
	 */
	@Override
	public String getDomainDescriptor(Element element) {
		String value = null;
		
		NodeList nl = element.getElementsByTagName("attribute");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n instanceof Element) {
				Element e = (Element) n;
				
				String attr = e.getAttribute("name");
				if(!Util.isEmpty(attr) && attr.equals("accessibledescription")) {
					value = e.getTextContent();
					if(!Util.isEmpty(value)) value = value.trim();
				}
			}
		}
		
		return value;
	}

	@Override
	public Icon getIcon(Element element) {
		//button moze mat aj ikonu, ziskat a vratit ikonu
		return null;
	}

	/**
	 * Vrati typ komponentu, vid triedu ComponentInfoType a existujuce handlery v gui.analyzer.handlers.swing
	 */
	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.FUNCTIONAL;
	}

	/**
	 * vrati true ak je to menuitem, false ak nie je
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		return isMenuItem(element);
	}

	private boolean isMenuItem(Element element) {
		String elemName = element.getNodeName();
		if(elemName.equals("element")) {
			String roleAttr = element.getAttribute("role");
			if (roleAttr != null) {
				if (roleAttr.equalsIgnoreCase("menuitem") && !isSeparator(element)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isSeparator(Element element) {
		String value = null;
		
		NodeList nl = element.getElementsByTagName("attribute");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n instanceof Element) {
				Element e = (Element) n;
				
				String attr = e.getAttribute("name");
				if(!Util.isEmpty(attr) && attr.equals("text")) {
					value = e.getTextContent().trim();
					if (value.equals("-"))
						return true;
			}
		}
		}
		return false;
	}
	
	/*************** Singleton pattern *************/
	private static WindowsMenuItemHandler instance;
	
	public static WindowsMenuItemHandler getInstance() {
		if(instance == null) {
			instance  = new WindowsMenuItemHandler();
		}
		return (WindowsMenuItemHandler) instance;
	}
	
	private WindowsMenuItemHandler() {}
	/*********** End singleton pattern ************/
}
