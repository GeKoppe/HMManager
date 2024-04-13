package org.hmdms.hmmanager.sys.exceptions.system;

/**
 * Exception to indicate a problem with caching information
 */
public class CachingException extends Exception {

    /**
     * Message of the exception
     */
    private String message;
    /**
     * Throwable that caused this exception to be thrown
     */
    private Throwable cause;

    /**
     * Default constructor. Calls {@link Exception#Exception()} constructor.
     */
    public CachingException() {
        super();
    }

    /**
     * Constructor sets message of the exception after calling {@link Exception#Exception(String)}.
     * @param message Message of the exception
     */
    public CachingException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Constructor sets the cause for the exception after calling {@link Exception#Exception(Throwable)}.
     * @param cause Throwable that caused this exception to get thrown.
     */
    public CachingException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    /**
     * Calls {@link CachingException#CachingException()} and sets cause and message.
     * @param cause Throwable that caused this exception to get thrown
     * @param message Message of the exception
     */
    public CachingException(Throwable cause, String message) {
        this();
        this.cause = cause;
        this.message = message;
    }

    /**
     * Returns message of the exception
     * @return Message of the exception
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Returns cause of the exception
     * @return Cause of the exception
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}
