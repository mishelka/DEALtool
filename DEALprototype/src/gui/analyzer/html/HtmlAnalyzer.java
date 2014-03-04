package gui.analyzer.html;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.html.handlers.HtmlHandlers;
import gui.analyzer.util.Logger;
import gui.editor.DomainModelEditor;
import gui.model.application.Application;
import gui.model.application.scenes.WebPageScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.tools.DomainModelGenerator;
import gui.tools.HtmlExtractor;
import gui.tools.exception.ExtractionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HtmlAnalyzer {
	private String url;
	private Application application;
	private DomainModelGenerator generator; 
	
	public HtmlAnalyzer(String url) {
		this.url = url;
		
DomainModelEditor editor = DomainModelEditor.getInstance();
		
		application = editor.getApplication();
		application.addObserver(editor);
	}
	
	public void analyze() {
		Document document = parseWebPage();
		
		try {
			WebPageScene wps = generateScene(document.getDocumentElement());
			
			application.addScene(wps);
			
		} catch (ExtractionException e) {
			Logger.logError("Extraction unsuccessful");
			e.printStackTrace();
		}		
	}
	
	private WebPageScene generateScene(Element document) throws ExtractionException {		
		WebPageScene wps = new WebPageScene(document);
		String documentTitle = wps.getName();
		DomainModel dm = new DomainModel(documentTitle);
		wps.setDomainModel(dm);
		
		AbstractHtmlHandler handler = HtmlHandlers.getInstance().getHtmlHandler(document);
		Term t = handler.createTerm(document, dm);
		
		dm.replaceRoot(t);
		
		HtmlExtractor htmlExtractor = new HtmlExtractor();
		generator = new DomainModelGenerator(null, htmlExtractor);	
		dm = generator.createDomainModel(wps);
		
		return wps;
	}
	
	//TODO: Valika implementovat parser pre HTML, ktory vrati dom Document v baliku parser a tu ho pouzit
	//parser pouzije url, ktora je ulozena tu v tejto triede
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