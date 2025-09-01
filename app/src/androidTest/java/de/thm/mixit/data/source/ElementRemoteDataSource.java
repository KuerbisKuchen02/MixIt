package de.thm.mixit.data.source;

import androidx.test.espresso.idling.CountingIdlingResource;

import java.util.function.Consumer;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.model.Result;

/**
 * Test implementation of element remote data source. Overrides all implementations.
 * <p>
 * This implementation uses a CountingIdlingResource to synchronise the ui test thread
 * and returns dummy values.
 *
 * @author Josia Menger
 */
public class ElementRemoteDataSource {

    private static final CountingIdlingResource idling =
            new CountingIdlingResource("ElementRemoteDataSource");

    public static CountingIdlingResource getIdlingResource() {
        return idling;
    }

    public static void combine(String element1, String element2,
                               Consumer<Result<Element>> callback) {
        idling.increment();
        new Thread(() -> {
            try {
                // Simulate ai request
                Thread.sleep(300);
                callback.accept(Result.success(new Element("FakeResult", "\uD83D\uDC40")));
            } catch (InterruptedException e) {
                callback.accept(Result.failure(e));
            } finally {
                idling.decrement();
            }
        }).start();
    }

    public static void generateNewGoalWord(Consumer<Result<String[]>> callback) {
        idling.increment();
        new Thread(() -> {
            try {
                Thread.sleep(300);
                callback.accept(Result.success(new String[]{
                        "Kerze", "Kerzen", "Wachskerze"
                }));
            } catch (InterruptedException e) {
                callback.accept(Result.failure(e));
            } finally {
                idling.decrement();
            }
        }).start();
    }
}
