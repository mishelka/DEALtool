package gui.analyzer;

import gui.analyzer.html.HtmlAnalyzer;
import gui.analyzer.windows.WindowsAnalyzer;
import gui.editor.DomainModelEditor;
import gui.editor.InputFileDialog;
import gui.tools.exception.ExtractionException;
import gui.tools.exception.ParsingException;

import javax.swing.JOptionPane;

public class Main {
	public static boolean ASPECTJ_MODE = true;
	
	public Main() {
		ASPECTJ_MODE = false;
	}
	
	public static void main(String[] args) {
		boolean success = false;
		while(!success) {
			InputFileDialog dialog = new InputFileDialog();
			dialog.showDialog();
			String url = dialog.getValidatedText();
			
			try {
				tryParse(url);
				success = true;
			} catch (ParsingException e) {
				JOptionPane.showMessageDialog(DomainModelEditor.getInstance(), "Parsing error, " + e.getMessage());
			} catch (ExtractionException e) {
				JOptionPane.showMessageDialog(DomainModelEditor.getInstance(), "Extraction error, " + e.getMessage());
			}
		}
	}
	
	private static void tryParse(String url) throws ParsingException, ExtractionException {
		if(url != null) {
			if (url.contains("http://") || url.contains("https://")) {
				HtmlAnalyzer wpa = new HtmlAnalyzer(url);
				wpa.analyze();
			} else {
				WindowsAnalyzer wa = new WindowsAnalyzer(url);
				wa.analyze();
			}
		}
	}
}
