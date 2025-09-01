package de.thm.mixit.data.exception;

public class InvalidGoalWordException extends RuntimeException {

    public InvalidGoalWordException(String message) {
        super(message);
    }
    public InvalidGoalWordException(String message, Throwable err) {
        super(message, err);
    }
}
