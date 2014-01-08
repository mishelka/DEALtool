package gui.analyzer.html;

import gui.analyzer.util.Logger;
import gui.model.application.scenes.Scene;
import gui.model.application.scenes.WebPageScene;
import gui.model.application.webpage.WebPage;
import gui.model.domain.DomainModel;
import gui.tools.HtmlExtractor;
import gui.tools.exception.ExtractionException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class HtmlAnalyzer {
	private String url;
	private List<Scene<WebPage>> scenes = new ArrayList<Scene<WebPage>>(); 
	
	public HtmlAnalyzer(String url) {
		this.url = url;
	}
	
	public void analyze() {
		Document document = parseWebPage();
		
		try {
			WebPageScene wps = generateScene(document);
			scenes.add(wps);
		} catch (ExtractionException e) {
			Logger.logError("Extraction unsuccessful");
		}		
	}
	
	private WebPageScene generateScene(Document document) throws ExtractionException {		
		String documentTitle = getDocumentTitle(document);
		
		DomainModel dm = new DomainModel(documentTitle);
		WebPage webPage = new WebPage(documentTitle);
		
		WebPageScene wps = new WebPageScene(webPage);
		
		HtmlExtractor htmlExtractor = new HtmlExtractor();
		dm = htmlExtractor.eXTRACT(wps);	
		
		wps.setDomainModel(dm);
		
		return wps;
	}
	
	private String getDocumentTitle(Document document) {
		String title = null;
		
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xPath.evaluate("*/head/title/text()",
			        document.getDocumentElement(), XPathConstants.NODESET);
			if(nodes.getLength() > 0) {
				title = nodes.item(0).getNodeValue();
			}
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		}
				
		return title;
	}
	
	//TODO: implementovat parser pre HTML, ktory vrati dom Document
	private Document parseWebPage() {
		//use the parser package to implement parser classes
		//connect to the url, and use parser to parse it to org.w3c.dom.Document
		//if the HTML has errors, first correct
		//format the HTML, the tags should use only lower case letters for simplicity
		//it is possible to exclude comments during parsing, etc. (look for Pitonak's code)
		return null;
	}

	public String getUrl() {
		return url;
	}
}