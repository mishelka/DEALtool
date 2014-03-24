package gui.analyzer.html.parser;

import gui.tools.exception.ParsingException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;


public class HtmlGUIParser {
//	private static final String URL_STRING = "http://diy-projectss.blogspot.sk/2014/03/15-creative-diy-patio-and-garden-ideas.html";
//	
//	private static URL url;
//	
//		
//	public static void main(String[] args) {
//	try {
//		parseURL(URL_STRING);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//}

	public Document parse(String stringUrl) throws ParsingException {
		
		// Tidy up HTML
		Tidy tidy = new Tidy();
		
		tidy.setXmlOut(true);
		tidy.setShowWarnings(false);
		tidy.setInputEncoding("UTF-8");
	    tidy.setOutputEncoding("UTF-8");
	    tidy.setWraplen(Integer.MAX_VALUE);
	    tidy.setHideComments(true);
	    tidy.setPrintBodyOnly(true);
	    tidy.setTidyMark(false);
        tidy.setDropEmptyParas(true);
        tidy.setHideComments(true);
        tidy.setTrimEmptyElements(true);
        tidy.setMakeBare(true);
        tidy.setMakeClean(true);
        tidy.setDropFontTags(true);
        tidy.setHideComments(true);
        tidy.setQuiet(true);
        tidy.setFixComments(true);
        
        Document doc;
        
        try {
	        URL url = new URL(stringUrl);
			URLConnection uc = url.openConnection();
			
						
			uc.addRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			uc.connect();
	        
			//prejdenie dokumentu parserom
	        //doc = tidy.parseDOM(uc.getInputStream(), System.out); -> System out znamena, ze ten vysledok len napise do vystupu. Neviem ci to nebude treba spravit tak ako Peter, ze vysledok zapisat do suboru a potom ten vysledny subor parsovat. Poradte sa s nim
			doc = tidy.parseDOM(uc.getInputStream(), null);
			doc.normalizeDocument();
			
			printNode(doc.getDocumentElement());
        } catch (IOException e) {
        	throw new ParsingException("Parsing unsuccessful");
        }
		return doc;
	}
	
	private void printNode(Node node) {
		System.out.println(node.getNodeName() + " value=\"" + node.getNodeValue() + "\"" + " type=" + node.getNodeType());
		if(node instanceof Element) {
			Element e = (Element) node;
			for(int i = 0; i < e.getChildNodes().getLength(); i++) {
				printNode(e.getChildNodes().item(i));
			}
		}
	}
}
