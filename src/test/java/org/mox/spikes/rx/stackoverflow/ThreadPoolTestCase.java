package org.mox.spikes.rx.stackoverflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ThreadPoolTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTestCase.class);

    private AtomicLong counter;

    @Test
    public void testName() throws Exception {

        counter = new AtomicLong(0L);

        final ExecutorService executorService = Executors.newFixedThreadPool(4);

        final Runnable aRunnable = new Runnable() {
            @Override
            public void run() {

                while (true) {

                    counter.incrementAndGet();
                }

            }
        };

        for (int i = 0; i < 4; i++) {
            executorService.submit(aRunnable);
        }

        Thread.sleep(100L);

        executorService.shutdown();

        LOGGER.info("counter: " + counter.get());

    }
}
