package gui.analyzer.windows.handlers;

import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;

public class DefaultWindowsHandler extends AbstractWindowsHandler {
	@Override
	public boolean matches(Element element) {
		return false;
	}

	@Override
	public String getDomainIdentifier(Element element) {
		return null;
	}

	@Override
	public String getDomainDescriptor(Element element) {
		return null;
	}

	@Override
	public Icon getIcon(Element element) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Element element) {
		return null;
	}

	/*************** Singleton pattern *************/
	public static DefaultWindowsHandler getInstance() {
		if(instance == null) {
			instance  = new DefaultWindowsHandler();
		}
		return (DefaultWindowsHandler) instance;
	}
	
	/* default constructor */
	private DefaultWindowsHandler() {}
}
