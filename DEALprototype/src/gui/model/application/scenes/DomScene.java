package gui.model.application.scenes;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xPath.evaluate(titleElementXPath,
			        sceneContainer, XPathConstants.NODESET);
			if(nodes.getLength() > 0) {
				title = nodes.item(0).getNodeValue();
				if(title != null) title = title.trim();
			}
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}
				
		return title;
	}
}
