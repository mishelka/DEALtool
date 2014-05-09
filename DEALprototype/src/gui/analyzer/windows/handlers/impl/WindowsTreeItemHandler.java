package gui.analyzer.windows.handlers.impl;

import gui.analyzer.util.Util;
import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class WindowsTreeItemHandler extends AbstractWindowsHandler {

	@Override
	/**
	 * Vrati primarnu informaciu o komponente - napr. nazov tlacidla "Submit"
	 */
	public String getDomainIdentifier(Element element) {
		String value = null;
		
		/*tu bol problem, nemoze sa v tomto pripade vyberat cez
		 * getElementsByTagName(...) pretoze tento nod obsahuje
		 * dalsie podelementy s tym istym tag name. Preto potom v cykle
		 * to prechadzalo aj podelementy a pridavalo to aj to, co nemalo
		 * (teda nazvy z dcerskych elementov). Posledny nazov ostal vzdy vo value
		 * teda bol to nazov nejakeho dcerskeho elementu. Preto to nesedelo.
		 * Ak robite nejaky item ktory moze obsahovat elementy s tym
		 * istym nazvom, tak je to lepsie robit cez getChildNodes
		 * alebo pomocou XPath.
		 * Na to je tu trieda XPathHelper - Valika s nim robila, vysvetli
		 * Vam ako na to, je to jednoduche.
		 * Cez XPath viete nastavit presnu cestu k elementu/atributu
		 * co je vyhodnejsie pretoze to nehlada potom v podelementoch.
		 * Ak je to nieco take ako Button, tak nemame problem lebo v nom
		 * uz nic nebude, ale ak je to vnoreny element napr. v tabe, tak
		 * to je problem.
		 * Prejdite si este raz vsetky handlery a zistite ci niekde nie je rovnaky
		 * problem (alebo moznost rovnakeho problemu) a ak ano, tak tam nastavte
		 * staticky cestu cez XPath alebo pouzite getChildNodes().  
		*/
		NodeList nl = element.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeName().equals("attribute") && n instanceof Element) {
				Element e = (Element) n;
				
				String attr = e.getAttribute("name");
				if(!Util.isEmpty(attr) && attr.equals("text")) {
					value = e.getTextContent();
					if(!Util.isEmpty(value)) value = value.trim();
					System.out.println(">> this should be: " + value);
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
		
		NodeList nl = element.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeName().equals("attribute") && n instanceof Element) {
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
		return ComponentInfoType.LOGICALLY_GROUPING;
	}

	/**
	 * vrati true ak je to treeitem, false ak nie je
	 */
	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		return isTreeItem(element);
	}

	private boolean isTreeItem(Element element) {
		String elemName = element.getNodeName();
		if(elemName.equals("element")) {
			String roleAttr = element.getAttribute("role");
			if (roleAttr != null) {
				if (roleAttr.equalsIgnoreCase("treeitem")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*************** Singleton pattern *************/
	private static WindowsTreeItemHandler instance;
	
	public static WindowsTreeItemHandler getInstance() {
		if(instance == null) {
			instance  = new WindowsTreeItemHandler();
		}
		return (WindowsTreeItemHandler) instance;
	}
	
	private WindowsTreeItemHandler() {}
	/*********** End singleton pattern ************/
}
