package gui.analyzer.util;

public class Logger {
	public static void logError(Exception e) {
		System.err.println(e.getMessage());
	}

	public static void logError(String s) {
		System.err.println(s);
	}

	public static void logError(Error e) {
		System.err.println(e);
	}
}
