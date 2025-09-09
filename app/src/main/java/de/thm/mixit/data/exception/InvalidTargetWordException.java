package de.thm.mixit.data.exception;

public class InvalidTargetWordException extends RuntimeException {

    public InvalidTargetWordException(String message) {
        super(message);
    }
    public InvalidTargetWordException(String message, Throwable err) {
        super(message, err);
    }
}
