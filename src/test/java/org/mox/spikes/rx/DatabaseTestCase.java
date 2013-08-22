package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observer;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class DatabaseTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTestCase.class);

    private int expectedValuesAmount;

    @Test
    public void shouldBeGreen() throws InterruptedException {

        LOGGER.info("abc");

        final Observer<String> counter = new CountingObserver();

        Database.loadWholeDataset().subscribe(counter);
        //i don't block this thread!
        LOGGER.info(Thread.currentThread().getName() + " completed at " + System.nanoTime());
        Thread.sleep(1000);
        assertEquals(expectedValuesAmount, 750);
    }

    private class CountingObserver implements Observer<String> {

        @Override
        public void onCompleted() {

            LOGGER.info(Thread.currentThread().getName() + " completed at " + System.nanoTime());
        }

        @Override
        public void onError(Throwable throwable) {

            LOGGER.info("error: " + throwable.getMessage());

        }

        @Override
        public void onNext(String item) {

            LOGGER.info("item: " + item);
            expectedValuesAmount++;

        }

    }
}
