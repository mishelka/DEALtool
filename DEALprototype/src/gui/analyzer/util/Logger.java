package gui.analyzer.util;

/**
 * Used for logging and debugging.
 * Logs different objects and errors into console.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Logger {
	/**
	 * Logs error into the console using the given Exception instance
	 * @param e the exception to be logged
	 */
	public static void logError(Exception e) {
		System.err.println(e.getMessage());
	}
	
	/**
	 * Logs error into the console using the given String message
	 * @param s the error message to be logged
	 */
	public static void logError(String s) {
		System.err.println(s);
	}

	/**
	 * Logs error into the console using the given Error instance
	 * @param e the error to be logged
	 */
	public static void logError(Error e) {
		System.err.println(e);
	}
	
	/**
	 * Logs the given String message into the console
	 * @param s the message to be logged
	 */
	public static void log(String s) {
		System.out.println(">>>> " + s);
	}
	
	/**
	 * Writes the given Object into the console (calls its toString() method) if the given Object is not null.
	 * @param o the object to be logged
	 */
	public static void log(Object o) {
		if(o == null) {
			log("null"); return;
		} else {
			log(o.toString());
		}
	}
}
