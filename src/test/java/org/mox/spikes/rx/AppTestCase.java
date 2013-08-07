package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observer;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class AppTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTestCase.class);

    @Test
    public void shouldBeGreen() {

        LOGGER.info("abc");
        assertTrue(true);

        final Observer<String> printer = new Observer<String>() {

            @Override
            public void onCompleted() {
                LOGGER.info("completed");
                assertTrue(true);
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("error");
            }

            @Override
            public void onNext(String item) {
                LOGGER.info(item);
            }
        };

        App.hello().subscribe(printer);

    }

}
