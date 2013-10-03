package gui.generator.itask;

public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
