package de.thm.mixit;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import de.thm.mixit.data.source.ElementRemote;

public class TestMain {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);

        ElementRemote.combine("Acker", "Samen", response -> {
            System.out.println("Response received: " + response);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runMain() {
        main(null);
    }
}
