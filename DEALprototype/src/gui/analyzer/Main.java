package gui.analyzer;

import gui.analyzer.html.HtmlAnalyzer;
import gui.analyzer.windows.WindowsAnalyzer;
import gui.editor.InputFileDialog;

public class Main {
	public static boolean ASPECTJ_MODE = true;
	
	public Main() {
		ASPECTJ_MODE = false;
	}
	
	public static void main(String[] args) {
		InputFileDialog dialog = new InputFileDialog();
		dialog.showDialog();
		String url = dialog.getValidatedText();
		
		if(url != null) {
			if (url.contains("http://")) {
				HtmlAnalyzer wpa = new HtmlAnalyzer(url);
				wpa.analyze();
			} else {
				WindowsAnalyzer wa = new WindowsAnalyzer(url);
				wa.analyze();
			}
		}
	}
}
