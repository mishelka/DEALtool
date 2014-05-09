package gui.analyzer.windows;

import gui.analyzer.windows.handlers.AbstractWindowsHandler;
import gui.analyzer.windows.handlers.WindowsHandlers;
import gui.analyzer.windows.parser.WindowsGUIParser;
import gui.editor.DomainModelEditor;
import gui.model.application.Application;
import gui.model.application.scenes.WindowsScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.tools.DomainModelGenerator;
import gui.tools.WindowsExtractor;
import gui.tools.exception.ExtractionException;
import gui.tools.exception.ParsingException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WindowsAnalyzer {
	private String filePath;
	private Application application;
	private DomainModelGenerator generator;
	
	public WindowsAnalyzer(String filePath) {
		this.filePath = filePath;
		
		DomainModelEditor editor = DomainModelEditor.getInstance();
		
		application = editor.getApplication();
		application.addObserver(editor);
	}
	
	public void analyze() throws ExtractionException, ParsingException {
		Document document = parseGUI();
		WindowsScene wps = generateScene(document.getDocumentElement());
		application.addScene(wps);
	}
	
	private WindowsScene generateScene(Element document) throws ExtractionException {
		//create new windows scene with the document title and a new domain model
		WindowsScene wps = new WindowsScene(document);
		String documentTitle = wps.getName();
		DomainModel dm = new DomainModel(documentTitle);
		wps.setDomainModel(dm);
		
		//get a handler for the root element to create the root term with the app name
		AbstractWindowsHandler handler = WindowsHandlers.getInstance().getWindowsHandler(document);
		Term t = handler.createTerm(document, dm);

		dm.replaceRoot(t);
		
		//extract information from the document into the domain model
		WindowsExtractor windowsExtractor = new WindowsExtractor();
		generator = new DomainModelGenerator(null, windowsExtractor);
		dm = generator.createDomainModel(wps);
		
		//set app name to the root element
		Term root = dm.getRoot();
		if(root != null) {
			root.setName(dm.getName());
		}
		
		return wps;
	}
	
	private Document parseGUI() throws ParsingException {
		WindowsGUIParser winGuiParser = new WindowsGUIParser();
	    Document document = winGuiParser.parse(filePath);
	    return document;
	}

	public String getFilePath() {
		return filePath;
	}
}