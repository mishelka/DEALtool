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
	
	public static void log(String s) {
		System.out.println(">>>> " + s);
	}
	
	public static void log(Object o) {
		System.out.println(">>> " + o.toString());
	}
}
