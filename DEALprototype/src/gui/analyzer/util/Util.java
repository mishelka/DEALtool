package gui.analyzer.util;

public class Util {

	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		return (s.trim().length() == 0);
	}
}
