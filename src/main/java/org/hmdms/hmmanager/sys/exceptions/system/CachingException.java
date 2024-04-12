package org.hmdms.hmmanager.sys.exceptions.system;

public class CachingException extends Exception {

    private String message;
    private Throwable cause;

    public CachingException() {
        super();
    }

    public CachingException(String message) {
        super(message);
        this.message = message;
    }

    public CachingException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    public CachingException(Throwable cause, String message) {
        this();
        this.cause = cause;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
