package de.thm.mixit.data.source;

/**
 * A generic object that encapsulates the outcome of an operation,
 * containing either data or an exception in case of failure.
 * <p>
 * Useful in asynchronous scenarios, such as when working with callbacks, where checked
 * exceptions cannot be thrown directly.
 *
 * @param <T> The type of the data contained in a successful result.
 *
 * @author Justin Wolek
 */
public class Result<T> {
    private final T data;
    private final Throwable error;

    /**
     * Constructor used to create a Result instance.
     *
     * @param data  the result data, or null if an error occurred.
     * @param error the exception, or null if the operation was successful.
     */
    private Result(T data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    /**
     * Creates a successful result.
     *
     * @param data the result data
     * @param <T>  the type of the result data
     * @return a {@link Result} representing a successful outcome
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    /**
     * Creates a failed result with the given exception.
     *
     * @param error an exception
     * @param <T>   the type of the expected result data
     * @return a {@link Result} representing a failure
     */
    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }
}
