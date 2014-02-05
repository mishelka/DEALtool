package gui.generator;

public class GeneratorException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public GeneratorException(String msg) {
        super(msg);
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
