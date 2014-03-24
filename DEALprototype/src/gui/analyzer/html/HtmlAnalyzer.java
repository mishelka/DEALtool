package gui.analyzer.html;

import gui.analyzer.html.handlers.AbstractHtmlHandler;
import gui.analyzer.html.handlers.HtmlHandlers;
import gui.analyzer.html.parser.HtmlGUIParser;
import gui.editor.DomainModelEditor;
import gui.model.application.Application;
import gui.model.application.scenes.WebPageScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.tools.DomainModelGenerator;
import gui.tools.HtmlExtractor;
import gui.tools.exception.ExtractionException;
import gui.tools.exception.ParsingException;

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
	
	public void analyze() throws ParsingException, ExtractionException {
		Document document = parseWebPage();
		WebPageScene wps = generateScene(document.getDocumentElement());

		application.addScene(wps);
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
		
		Term root = dm.getRoot();
		if(root != null) {
			root.setName(dm.getName());
		}
		
		return wps;
	}
	
	private Document parseWebPage() throws ParsingException {
		HtmlGUIParser htmlGuiParser = new HtmlGUIParser();
	    Document document = htmlGuiParser.parse(url);
	    return document;
	}

	public String getUrl() {
		return url;
	}
}