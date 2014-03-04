package gui.analyzer.html.handlers;

import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;

import org.w3c.dom.Element;

public class DefaultHtmlHandler extends AbstractHtmlHandler {
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
	private static DefaultHtmlHandler instance;
	public static DefaultHtmlHandler getInstance() {
		if(instance == null) {
			instance  = new DefaultHtmlHandler();
		}
		return (DefaultHtmlHandler) instance;
	}
	
	/* default constructor */
	private DefaultHtmlHandler() {}
	/*********** End singleton pattern *************/
}
