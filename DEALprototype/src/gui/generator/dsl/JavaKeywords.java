package gui.generator.dsl;

import java.util.Arrays;
import java.util.List;

public class JavaKeywords {
	private static final List<String> keywords = Arrays.asList(new String[] {"abstract", "continue", "for", "new", "switch",
	"assert", "default", "goto", "package", "synchronized",
	"boolean", "do", "if", "private", "this",
	"break", "double", "implements", "protected", "throw",
	"byte", "else", "import", "public", "throws",
	"case", "enum", "instanceof", "return", "transient",
	"catch", "extends", "int", "short", "try",
	"class", "finally", "long", "strictfp", "volatile",
	"const", "float", "native", "super", "while"});
	
	
	public static boolean isJavaKeyword(String string) {
		for(String keyword : keywords) {
			if(string.equalsIgnoreCase(keyword)) return true;
		}
		return false;
	}
}
