package org.hmdms.hmmanager.sys.exceptions.auth;

/**
 * Exception that signifies, that a user has not been found
 */
public class UserNotFoundException extends Exception {

    /**
     * Message of the exception
     */
    private String message;
    /**
     * Throwable that caused this exception to be thrown
     */
    private Throwable cause;

    /**
     * Default constructor. Calls super constructor
     */
    public UserNotFoundException() {
        super();
    }

    /**
     * Constructor that also sets cause of the exception
     * @param cause cause of the exception
     */
    public UserNotFoundException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    /**
     * Constructor that also sets message of the exception
     * @param message Message of the exception
     */
    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Constructor that sets cause and message of the exception
     * @param cause Cause of the exception
     * @param message Message of the exception
     */
    public UserNotFoundException(Throwable cause, String message) {
        this();
        this.cause = cause;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * @return Message of the exception
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     * @return Cause for the exception
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}
