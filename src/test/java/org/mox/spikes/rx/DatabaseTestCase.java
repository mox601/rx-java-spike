package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class DatabaseTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTestCase.class);

    private int expectedValuesAmount;

    @Test
    public void shouldBeGreen() throws Exception {

        final Observer<String> aCounter = new CountingObserver();

        Database.loadWholeDataset().subscribe(aCounter);
        //previous call doesn't block this thread!
        assertEquals(expectedValuesAmount, 0);
        LOGGER.info(Thread.currentThread().getName() + " completed at " + System.nanoTime());
        //waiting for a while to wait for the observer to finish
        Thread.sleep(1000);
        assertEquals(expectedValuesAmount, 750);
    }

    private class CountingObserver implements Observer<String> {

        @Override
        public void onCompleted() {

            LOGGER.info(Thread.currentThread().getName() + " completed at " + System.nanoTime());
            assertTrue(true);
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
