package gui.analyzer;

public class ProcessingException extends RuntimeException {
    private static final long serialVersionUID = 1;
    
    /**
     * Creates a new instance of <code>ProcessingException</code> without detail message.
     */
    public ProcessingException() {
    }


    /**
     * Constructs an instance of <code>ProcessingException</code> with the specified detail message.
     * @param message the detail message.
     */
    public ProcessingException(String message) {
        super(message);
    }
    
    /**
     * Constructs an instance of <code>ProcessingException</code> with the specified detail message 
     * and cause.
     * @param message the detail message.
     * @param cause cause.
     */
    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }    
}
