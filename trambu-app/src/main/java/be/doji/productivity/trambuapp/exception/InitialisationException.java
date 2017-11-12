package be.doji.productivity.trambuapp.exception;

/**
 * @author Doji
 */
public class InitialisationException extends Exception {

    public InitialisationException(String errorMessage) {
        super(errorMessage);
    }

    public InitialisationException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
