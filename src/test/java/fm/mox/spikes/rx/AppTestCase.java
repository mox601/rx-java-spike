package fm.mox.spikes.rx;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class AppTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTestCase.class);

    @Test(enabled = false)
    public void producerAndConsumer() throws Exception {

        // http://stackoverflow.com/questions/19702025/non-blocking-buffer-in-java

        final int buffersAvailable = 2;

        final BlockingQueue<List<String>> emptyBuffers = new ArrayBlockingQueue<List<String>>(
                buffersAvailable);
        final BlockingQueue<List<String>> filledBuffers = new ArrayBlockingQueue<List<String>>(
                buffersAvailable);

        emptyBuffers.put(new ArrayList<>());

        final BufferProvider<List<String>> bufferProvider = new BufferProvider<List<String>>(
                emptyBuffers, filledBuffers);

        final Appender appender = new Appender(bufferProvider);

        final Producer producer = new Producer(appender);
        new Thread(producer).start();

        final Consumer consumer = new Consumer(bufferProvider);
        new Thread(consumer).start();

        Thread.sleep(10L);
        assertTrue(true);

    }

    /* not threadsafe */
    private static class Appender {

        private final BufferProvider<List<String>> bufferProvider;

        private final int maxElements;

        private List<String> currentBuffer;

        public Appender(final BufferProvider<List<String>> bufferProvider) {

            this.bufferProvider = bufferProvider;
            this.maxElements = 10;

        }

        //TODO find a way to remove this synchronization
        public synchronized void append(final String something) throws InterruptedException {

            if (currentBuffer == null) {
                currentBuffer = this.bufferProvider.acquireEmpty();
            } else {
                if (currentBuffer.size() >= this.maxElements) {
                    //release filled and acquire empty one
                    bufferProvider.releaseFilled(currentBuffer);
                    currentBuffer = bufferProvider.acquireEmpty();
                }
            }

            currentBuffer.add(something);

        }
    }

    private static class Consumer implements Runnable {

        private final BufferProvider<List<String>> bufferProvider;

        public Consumer(final BufferProvider<List<String>> bufferProvider) {

            this.bufferProvider = bufferProvider;
        }

        @Override
        public void run() {

            while (true) {

                //blocking
                try {
                    final List<String> buffer = bufferProvider.acquireFilled();

                    final int size = buffer.size();

                    writeToFile(buffer, new DateTime().toString() + ".txt");

                    //empty buffer
                    buffer.clear();

                    LOGGER.info("consumed " + size);

                    //buffer is empty, put reference in empty
                    bufferProvider.releaseEmpty(buffer);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

        private void writeToFile(final List<String> buffer, final String filename) {

            FileWriter fw = null;

            try {
                fw = new FileWriter("target/" + filename);

                for (final String line : buffer) {
                    try {
                        fw.write(line + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /* uses blocking methods */
    private static class BufferProvider<T> {

        private final BlockingQueue<T> empty;

        private final BlockingQueue<T> filled;

        public BufferProvider(final BlockingQueue<T> empty, final BlockingQueue<T> filled) {

            this.empty = empty;
            this.filled = filled;
        }

        public T acquireEmpty() throws InterruptedException {


            /* non blocking */
            //            return nonBlockingPoll(empty);

            //blocking
            return empty.take();

        }

        public void releaseFilled(final T buffer) throws InterruptedException {

            //non-blocking
            //            nonBlockingOffer(filled, buffer);

            //blocking
            filled.put(buffer);

        }

        public T acquireFilled() throws InterruptedException {

            //non blocking
            //            return nonBlockingPoll(this.filled);

            //blocking
            return filled.take();

        }

        public void releaseEmpty(final T buffer) throws InterruptedException {

            //non blocking
            //            nonBlockingOffer(empty, buffer);

            //blocking
            empty.put(buffer);
        }

        private T nonBlockingPoll(final BlockingQueue<T> empty) {

            T polled = empty.poll();

            while (polled == null) {
                polled = empty.poll();
            }

            return polled;
        }

        private void nonBlockingOffer(final BlockingQueue<T> filled, final T buffer) {

            boolean offered = filled.offer(buffer);
            while (!offered) {
                offered = filled.offer(buffer);
            }

        }
    }

    private static class Producer implements Runnable {

        private final Appender appender;

        public Producer(final Appender appender) {

            this.appender = appender;
        }

        @Override
        public void run() {

            while (true) {
                //produce something and append
                try {
                    appender.append("something");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
