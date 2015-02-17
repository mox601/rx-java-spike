package fm.mox.spikes.rx.stackoverflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class NonBlockingQueueTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingQueueTestCase.class);

    @Test
    public void testName() throws Exception {

        //TODO use arrayblockingueue and compare latencies , since contention here is high
        final ConcurrentLinkedQueue<Long> longLinkedQueue = new ConcurrentLinkedQueue<Long>();

        final Producer producer = new Producer(longLinkedQueue);

        final Consumer consumer = new Consumer(longLinkedQueue);

        final ExecutorService executors = Executors.newFixedThreadPool(2);

        executors.submit(producer);

        executors.submit(consumer);

        Thread.sleep(1000L);
        executors.shutdown();
        LOGGER.info("size: '" + longLinkedQueue.size() + "'");

    }

    private class Producer implements Runnable {

        private final Queue<Long> longLinkedQueue;

        public Producer(Queue<Long> longLinkedQueue) {

            this.longLinkedQueue = longLinkedQueue;
        }

        @Override
        public void run() {

            while (true) {
                final long l = System.nanoTime();
                this.longLinkedQueue.add(1L);
                final long elapsed = System.nanoTime() - l;
                //                LOGGER.info("added in: " + elapsed);
            }
        }
    }

    private static class Consumer implements Runnable {

        private final Queue<Long> longLinkedQueue;

        private Consumer(Queue<Long> longLinkedQueue) {

            this.longLinkedQueue = longLinkedQueue;
        }

        @Override
        public void run() {

            while (true) {
                final long l = System.nanoTime();

                this.longLinkedQueue.remove();
                final long elapsed = System.nanoTime() - l;
                //                LOGGER.info("removed in: " + elapsed);
            }
        }
    }
}
