package de.thm.mixit.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Code taken from <a href="https://gist.github.com/JoseAlcerreca/1e9ee05dcdd6a6a6fa1cbfc125559bba">GitHub Gist</a>
 * <br>
 * For background information on why this is needed, read
 * <a href="https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04">
 *     Unit-testing LiveData and other common observability problems</a>
 * @author Josia Menger
 */
@SuppressWarnings("unchecked")
public class LiveDataTestUtil {
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = value -> {
            data[0] = value;
            latch.countDown();
        };
        liveData.observeForever(observer);
        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        liveData.removeObserver(observer);
        return (T) data[0];
    }
}
