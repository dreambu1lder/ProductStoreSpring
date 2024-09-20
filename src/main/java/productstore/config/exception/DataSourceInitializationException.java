package productstore.config.exception;

public class DataSourceInitializationException extends RuntimeException {
    public DataSourceInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
