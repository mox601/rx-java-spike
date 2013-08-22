package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observer;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class GreeterTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterTestCase.class);

    private boolean completed = false;

    @Test
    public void shouldBeGreen() throws InterruptedException {

        LOGGER.info("abc");

        final Observer<String> printer = new PrintingObserver();

        Greeter.hello().subscribe(printer);
        //i don't block!
        Thread.sleep(1000);
        assertTrue(completed);
    }

    private class PrintingObserver implements Observer<String> {

        @Override
        public void onCompleted() {

            LOGGER.info("completed");
            completed = true;
        }

        @Override
        public void onError(Throwable throwable) {

            LOGGER.info("error: " + throwable.getMessage());
        }

        @Override
        public void onNext(String item) {

            LOGGER.info("item: " + item);
        }

    }
}
