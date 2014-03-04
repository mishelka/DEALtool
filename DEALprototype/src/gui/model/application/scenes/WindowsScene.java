package gui.model.application.scenes;

import org.w3c.dom.Element;

public class WindowsScene extends DomScene {

	private final static String DOCUMENT_TITLE_XPATH = 
			"//attribute[@name=\"title\"]/text()";
	
	public WindowsScene(Element element) {
		super(element, DOCUMENT_TITLE_XPATH);
	}
	
	@Override
	public String toString() {
		return "Web page " + super.toString();
	}
}
