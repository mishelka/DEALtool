package temp.domain.io.helpers;

/**
 * Saves a warning with a line number where it occurred.
 */
public class ModelWarning {

	public final String message;

	public final int line;

	public ModelWarning(String message, int line) {
		this.message = message;
		this.line = line;
	}

}
