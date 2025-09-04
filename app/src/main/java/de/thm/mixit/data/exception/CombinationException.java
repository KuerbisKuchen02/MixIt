package de.thm.mixit.data.exception;

public class CombinationException extends RuntimeException {

    public CombinationException(String message) {
        super(message);
    }
    public CombinationException(String message, Throwable err) {
        super(message, err);
    }
}
