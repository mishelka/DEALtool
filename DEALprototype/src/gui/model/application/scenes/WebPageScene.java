package gui.model.application.scenes;

import gui.model.application.webpage.WebPage;

/**
 * Not used yet.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class WebPageScene extends Scene<WebPage> {

	public WebPageScene(WebPage sceneContainer) {
		super(sceneContainer);
		// TODO Auto-generated constructor stub
	}
	
	protected String getSceneName(WebPage scene) {
		return sceneContainer.getTitle();
	}
	
	@Override
	public String toString() {
		return "Web page " + super.toString();
	}
}
