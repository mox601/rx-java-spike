package fm.mox.spikes.rx.stackoverflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * from michael busch @ lucene revolution 2011
 * {@link https://vimeo.com/31195040}
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class MemoryBarrier {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryBarrier.class);

    private static int counter = 0;

    private static final int MAX = 1000;

    private static volatile int memoryBarrier;

    private static class Reader extends Thread {

        @Override
        public void run() {

            while (counter < MAX) {
//                int dummy = memoryBarrier;
            }

            System.out.println("thread " + getId() + " done!");
        }
    }

    public static void main(String[] args) throws Exception {

        Thread writer = new Thread() {
            @Override
            public void run() {

                while (counter < MAX) {
                    counter++;
                }

                memoryBarrier = 1;

            }
        };

        for (int i = 0; i < 10; i++) {

            new Reader().start();

        }

        System.out.println("all reader threads started");

        writer.start();
        writer.join(1000L);

    }
}
