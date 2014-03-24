package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;

public class HtmlButtonHandler extends AbstractHtmlHandler {
	//nastavi sa v matches
	private ButtonType buttonType;
	private static final String BUTTON_TAG_NAME = "button";
	private static final String BUTTON_ATTR_VAL = "button";
	private static final String INPUT_TAG_NAME = "input";
	private static final String TYPE_ATTR_NAME = "type";
	private static final String INPUT_ATTR_VAL = "submit";
	
	private static final String VALUE_ATTR_NAME = "value";
	
	@Override
	//<input type="submit" value="Submit">
	//<button type="button">Click Me!</button>
	//will perform only if matches!
	public String getDomainIdentifier(Element element) {
		String value = null;
		if(element == null) return null;
		
		if (element.hasAttribute(VALUE_ATTR_NAME)) {
			value = element.getAttribute(VALUE_ATTR_NAME);
		}
		System.out.println(element);
		
		//get text content of the node
		if(Util.isEmpty(value)) {
			value = element.getFirstChild().getNodeValue();
		}
		
		return value;
	}

	@Override
	public String getDomainDescriptor(Element element) {
		//vratit tooltip buttonu alebo submitu -> atribut title, alebo alt?
		return null;
	}

	@Override
	public Icon getIcon(Element element) {
		if(buttonType == ButtonType.BUTTON) {
			//...
			//button moze mat aj ikonu, ziskat a vratit ikonu
			//input nemoze mat ikonu
		}
		
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return ComponentInfoType.FUNCTIONAL;
	}

	@Override
	public boolean matches(Element element) {
		if(element == null) return false;
		boolean matches = isButton(element) || isSubmit(element);
		System.out.println(matches);
		return matches;
	}
	
	//<button type="button">Click Me!</button>
	private boolean isButton(Element element) {
		String tagName = element.getTagName();
		if(tagName.equals(BUTTON_TAG_NAME)) {
			if(element.hasAttribute(TYPE_ATTR_NAME)) {
				String value = element.getAttribute(TYPE_ATTR_NAME);
				if(value.equals(BUTTON_ATTR_VAL)) { return true; }
			}
		}
		
		buttonType = ButtonType.BUTTON;
		
		return false;
	}
	
	//<input type="submit" value="Submit">
	private boolean isSubmit(Element element) {
		String tagName = element.getTagName();
		if(tagName.equals(INPUT_TAG_NAME)) {
			if(element.hasAttribute(TYPE_ATTR_NAME)) {
				String value = element.getAttribute(TYPE_ATTR_NAME);
				if(value.equals(INPUT_ATTR_VAL)) { return true; }
			}
		}
		
		buttonType = ButtonType.SUBMIT;
		
		return false;
	}

	private enum ButtonType {
		BUTTON, SUBMIT;
	}
	
	/*************** Singleton pattern *************/
	private static HtmlButtonHandler instance;
	
	public static HtmlButtonHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlButtonHandler();
		}
		return (HtmlButtonHandler) instance;
	}
	
	private HtmlButtonHandler() {}
	/*********** End singleton pattern *************/
}
