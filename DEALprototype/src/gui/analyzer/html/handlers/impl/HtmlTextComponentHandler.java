package gui.analyzer.html.handlers.impl;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;

public class HtmlTextComponentHandler extends AbstractHtmlHandler {

	@Override
	public boolean matches(Element element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDomainIdentifier(Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomainDescriptor(Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon(Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	/*************** Singleton pattern *************/
	public static HtmlTextComponentHandler getInstance() {
		if(instance == null) {
			instance  = new HtmlTextComponentHandler();
		}
		return (HtmlTextComponentHandler) instance;
	}
	
	private HtmlTextComponentHandler() {}
}
