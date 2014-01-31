package gui.analyzer.util;

import java.awt.Image;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringEscapeUtils;

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
}
