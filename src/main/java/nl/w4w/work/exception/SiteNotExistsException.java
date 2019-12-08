package nl.w4w.work.exception;

public class SiteNotExistsException extends Exception {

    public SiteNotExistsException() {
        super();
    }

    public SiteNotExistsException(String message) {
        super(message);
    }

    public SiteNotExistsException(Throwable cause) {
        super(cause);
    }

    public SiteNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }


}
