package gui.analyzer.windows.handlers.impl;

import gui.analyzer.util.Util;
import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * komponenty: PropertyGrid, DomainUpDown(spinner), 
 */

public class WindowsContainerHandler extends AbstractWindowsHandler {

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
		return ComponentInfoType.CONTAINERS;
	}

	/**
	 * vrati true ak je to container, false ak nie je
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		return isContainer(element);
	}

	private boolean isContainer(Element element) {
		String elemName = element.getNodeName();
		if(elemName.equals("element")) {
			String roleAttr = element.getAttribute("role");
			if (roleAttr != null) {
				if (roleAttr.equalsIgnoreCase("container")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*************** Singleton pattern *************/
	private static WindowsContainerHandler instance;
	
	public static WindowsContainerHandler getInstance() {
		if(instance == null) {
			instance  = new WindowsContainerHandler();
		}
		return (WindowsContainerHandler) instance;
	}
	
	private WindowsContainerHandler() {}
	/*********** End singleton pattern ************/
}
