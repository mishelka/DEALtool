package gui.model.application.scenes;

import gui.analyzer.util.XPathHelper;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public abstract class DomScene extends Scene<Element> {

	private String titleElementXPath;

	public DomScene(Element element, String titleElementXPath) {
		super(element);
		this.titleElementXPath = titleElementXPath;
		updateName(element);
	}

	protected String getSceneName(Element element) {
		return getDocumentTitle();
	}

	private String getDocumentTitle() {
		String title = null;

		try {
			title = XPathHelper.getString(titleElementXPath, sceneContainer);
			if (title != null)
				title = title.trim();
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}

		return title;
	}
}
