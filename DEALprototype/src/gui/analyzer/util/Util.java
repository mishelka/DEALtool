package gui.analyzer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class Util {

	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		return (s.trim().length() == 0);
	}

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
}
