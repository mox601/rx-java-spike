package fm.mox.spikes.rx.stackoverflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ThreadPoolTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTestCase.class);

    private AtomicLong counter;

    @Test
    public void testName() throws Exception {

        counter = new AtomicLong(0L);

        ThreadFactory aNamedThreadFactory = new NamedThreadFactory("poolname-");
        final ExecutorService executorService = Executors.newFixedThreadPool(2,
                aNamedThreadFactory);

        final Runnable aRunnable = () -> {

            while (true) {
                counter.incrementAndGet();
                LOGGER.info("hey!");
            }

        };

        for (int i = 0; i < 4; i++) {
            executorService.submit(aRunnable);
        }

        final DateTimeFormatter datasiftDateTimeFormat = DateTimeFormat.forPattern(
                "E', 'dd MMM yyyy HH:mm:ss Z");
        final DateTime now = new DateTime();
        String str = datasiftDateTimeFormat.print(now);

        Thread.sleep(100L);

        executorService.shutdown();

        LOGGER.info("counter: " + counter.get());

    }

    private class NamedThreadFactory implements ThreadFactory {

        private String poolName;

        private long incrementing;

        public NamedThreadFactory(final String poolName) {

            this.poolName = poolName;
            this.incrementing = 0L;
        }

        @Override
        public Thread newThread(Runnable r) {

            return new Thread(null, r, poolName + nextThreadNum(), 0);

        }

        private synchronized Long nextThreadNum() {

            this.incrementing++;
            return this.incrementing;
        }
    }
}
