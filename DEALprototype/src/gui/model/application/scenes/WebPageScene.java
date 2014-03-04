package gui.model.application.scenes;

import org.w3c.dom.Element;

/**
 * Not used yet.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class WebPageScene extends DomScene {

	private final static String DOCUMENT_TITLE_XPATH 
	= "html/head/title/text()";
	
	public WebPageScene(Element element) {
		super(element, DOCUMENT_TITLE_XPATH);
	}
	
	@Override
	public String toString() {
		return "Web page " + super.toString();
	}
}
