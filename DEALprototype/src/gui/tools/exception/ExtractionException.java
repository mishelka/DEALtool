package gui.tools.exception;

/**
 * Thrown when the extractor tries to extract
 * a domain model from a null scene or if the 
 * extracted domain model is null.
 * Used only in the tools package.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class ExtractionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExtractionException(String msg) {
		super(msg);
	}
}
