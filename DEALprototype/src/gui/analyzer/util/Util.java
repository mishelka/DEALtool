package gui.analyzer.util;

import java.awt.Image;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;

/**
 * A utility class for String operations.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Util {

	/**
	 * Finds out, if the given string is empty.
	 * @param s the string
	 * @return true if the given string is null or it's length is 0, false otherwise
	 */
	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		return (s.trim().isEmpty());
	}

	/**
	 * Transforms HTML code to a simple text
	 * @param s the string to be transformed from HTML to simple text.
	 * @return the string without any HTML tags and escape characters
	 */
	public static String htmlToText(String s) {
		if (s == null)
			return s;

		Pattern htmlTagPattern = Pattern.compile("(<.*?>)");

		Matcher dataMatcher = htmlTagPattern.matcher(s);
		while (dataMatcher.find()) {
			String toRemove = dataMatcher.group();
			s = s.replace(toRemove, "");
			dataMatcher = htmlTagPattern.matcher(s);
		}

		s = StringEscapeUtils.unescapeHtml3(s);
		s = StringEscapeUtils.unescapeHtml4(s);

		return s;
	}
	
	public static Icon imageToIcon(Image image) {
		if(image == null) return null;
		ImageIcon icon = new ImageIcon(image);
		return icon;
	}

	public static String removeBadCharacters(String str) {
		if(!isEmpty(str)) {
			str = str.replaceAll("[^\\w\\s-]*", "");
		}
		return str;
	}
	
	public static String nodeToString(Node node) {
		String nodeText = node.getNodeValue();
		if(Util.isEmpty(nodeText)) {
			nodeText = node.getTextContent();
		}
		if(Util.isEmpty(nodeText)) {
			nodeText = node.getNodeName();
		}
		if(node.hasChildNodes()) {
			if(Util.isEmpty(nodeText)) {
				nodeText = node.getFirstChild().getTextContent();
			}
			if(Util.isEmpty(nodeText)) {
				nodeText = node.getFirstChild().getNodeValue();
			}
		}
		return nodeText;
	}
	
	public static String nodeToClass(Node node) {
		String nodeText = "";
		String nodeName = node.getNodeName();
		
		switch(node.getNodeType()) {
			case Node.DOCUMENT_NODE:
			case Node.ELEMENT_NODE: nodeText = "<" + nodeName + ">"; break;
			
			case Node.ATTRIBUTE_NODE: nodeText = "attribute " + nodeName; break;
			case Node.TEXT_NODE: nodeText = "text"; break;
			
			case Node.CDATA_SECTION_NODE: nodeText = "CDATA"; break;
			
			case Node.ENTITY_REFERENCE_NODE: nodeText = "entityRef"; break;
			case Node.ENTITY_NODE: nodeText = "entity"; break;
			
			case Node.COMMENT_NODE: nodeText = "<!-- -->"; break;
			
			case Node.NOTATION_NODE: nodeText = "dtdNotation"; break;
			
			default: nodeText = nodeName;
		}
		
		return nodeText;
	}
}
