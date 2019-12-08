package nl.w4w.work.exception;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;

public class InvalidArgumentsException extends Exception {

    public InvalidArgumentsException() {
        super();
    }

    public InvalidArgumentsException(String message) {
        super(message);
    }

    public InvalidArgumentsException(Throwable cause) {
        super(cause);
    }

    public InvalidArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
