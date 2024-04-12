package org.hmdms.hmmanager.sys.exceptions.auth;

/**
 * Exception that signals a mismatch in user and password
 */
public class InvalidUserPwCombinationException extends Exception {

    /**
     * Message of the exception
     */
    private String message;
    /**
     * Throwable that caused this exception to get thrown
     */
    private Throwable cause;

    /**
     * Default constructor
     */
    public InvalidUserPwCombinationException() {
        super();
    }

    /**
     * Constructor that sets the cause of this exception
     * @param cause Throwable that caused this exception to get thrown
     */
    public InvalidUserPwCombinationException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    /**
     * Constructor that sets the message of this exception
     * @param message Message of this exception
     */
    public InvalidUserPwCombinationException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Constructor that sets both message and cause of this exception
     * @param cause Throwable that caused this exception to get thrown
     * @param message Message of this exception
     */
    public InvalidUserPwCombinationException(Throwable cause, String message) {
        this();
        this.message = message;
        this.cause = cause;
    }

    /**
     * {@inheritDoc}
     * @return Throwable that caused this exception to get thrown
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

    /**
     * {@inheritDoc}
     * @return Message of the exception
     */
    @Override
    public String getMessage() {
        return message;
    }
}
