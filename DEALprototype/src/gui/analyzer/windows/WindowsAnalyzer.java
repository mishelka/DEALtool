package gui.analyzer.windows;

import gui.analyzer.util.Logger;
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
	
	public void analyze() {
		Document document = parseGUI();
		
		try {
			WindowsScene wps = generateScene(document.getDocumentElement());
			
			application.addScene(wps);
			
		} catch (ExtractionException e) {
			Logger.logError("Extraction unsuccessful");
			e.printStackTrace();
		}		
	}
	
	private WindowsScene generateScene(Element document) throws ExtractionException {		
		WindowsScene wps = new WindowsScene(document);
		String documentTitle = wps.getName();
		DomainModel dm = new DomainModel(documentTitle);
		wps.setDomainModel(dm);
		
		AbstractWindowsHandler handler = WindowsHandlers.getInstance().getWindowsHandler(document);
		Term t = handler.createTerm(document, dm);
		
		dm.replaceRoot(t);
		
		WindowsExtractor windowsExtractor = new WindowsExtractor();
		generator = new DomainModelGenerator(null, windowsExtractor);
		dm = generator.createDomainModel(wps);
		
		return wps;
	}
	
	//TODO: Peter implementovat parser pre XML, ktory vrati dom Document v baliku parser a tu ho pouzit
	//parser pouzije filePath cestu k suboru, ktora je ulozena tu v tejto triede
	private Document parseGUI() {
		WindowsGUIParser winGuiParser = new WindowsGUIParser();
	    Document document = winGuiParser.parse(filePath);
	    return document;
	}

	public String getFilePath() {
		return filePath;
	}
}