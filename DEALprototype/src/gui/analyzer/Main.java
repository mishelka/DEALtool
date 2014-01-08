package gui.analyzer;

import gui.analyzer.html.HtmlAnalyzer;
import gui.editor.UrlDialog;

public class Main {
	public static void main(String[] args) {
		UrlDialog dialog = new UrlDialog();
		dialog.showDialog();
		String url = dialog.getValidatedText();
		System.out.println(">>>" + url);
		
		HtmlAnalyzer wpa = new HtmlAnalyzer(url);
		wpa.analyze();
		
		
	}
}
