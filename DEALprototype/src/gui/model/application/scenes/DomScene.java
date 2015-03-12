package gui.model.application.scenes;

import gui.analyzer.util.XPathHelper;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

public abstract class DomScene extends Scene<Element> {

	private String titleElementXPath;
	private String descriptionElementXPath;

	public DomScene(Element element, String titleElementXPath, String descriptionElementXPath) {
		super(element);
		this.titleElementXPath = titleElementXPath;
		this.descriptionElementXPath = descriptionElementXPath;
		updateName(element);
		updateDescription(element);
	}
	
	public DomScene(Element element, String titleElementXPath) {
		super(element);
		this.titleElementXPath = titleElementXPath;
		updateName(element);
	}

	@Override
	protected String getSceneName(Element element) {
		return getDocumentTitle();
	}
	
	@Override
	protected String getSceneDescription(Element element) {
		return getDocumentDescription();
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
	
	private String getDocumentDescription() {
		String description = null;
		
		try {
			description = XPathHelper.getString(descriptionElementXPath, sceneContainer);
			if (description != null)
				description = description.trim();
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}
		
		System.out.println(">>>> " + description);
		
		return description;
	}
}
