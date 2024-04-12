package org.hmdms.hmmanager.sys.exceptions.auth;

/**
 * Exception that signifies, that a user has not been found
 */
public class UserNotFoundException extends Exception {

    private String message;
    private Throwable cause;
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    public UserNotFoundException(String message) {
        super(message);
        this.message = message;
    }

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
