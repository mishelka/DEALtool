package gui.analyzer.windows.handlers.impl;

import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;


public class WindowsTextComponentHandler extends AbstractWindowsHandler {

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
	
	@Override
	public boolean extractChildren() {
		return false;
	}

	/*************** Singleton pattern *************/
	public static WindowsTextComponentHandler getInstance() {
		if(instance == null) {
			instance  = new WindowsTextComponentHandler();
		}
		return (WindowsTextComponentHandler) instance;
	}
	
	private WindowsTextComponentHandler() {}
}
