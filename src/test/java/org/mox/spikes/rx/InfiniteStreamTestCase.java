package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class InfiniteStreamTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            InfiniteStreamTestCase.class);

    private AtomicLong counter;

    @BeforeMethod
    public void setUp() throws Exception {

        this.counter = new AtomicLong(0L);

    }

    @Test
    public void testName() throws Exception {

        this.counter = new AtomicLong(0L);

        int amount = 4;
        int nThreads = 4;

        final ExecutorService executorService = Executors.newFixedThreadPool(
                nThreads);
        final InfiniteStream infiniteStream = new InfiniteStream(executorService);
        final Observable<String> start = infiniteStream.start();

        final Counter onNext = new Counter();

        List<Subscription> subscriptionList = new ArrayList<Subscription>();

        for (int i = 0; i < amount; i++) {
            subscriptionList.add(start.subscribe(onNext));
        }

        Thread.sleep(1000L);

        for (Subscription aSubscription : subscriptionList) {
            aSubscription.unsubscribe();
        }

        executorService.shutdown();
        LOGGER.info("count: " + counter.get());

    }

    @Test
    public void testRepeatingStream() throws Exception {

        int nThreads = 1;

        final ExecutorService executorService = Executors.newFixedThreadPool(
                nThreads);

        final Counter counter = new Counter();
        final Observable<String> repeat = Observable.from(Arrays.asList("1", "2"))
                                                    .repeat(Schedulers.computation());

        final Subscription aSubscription = repeat.subscribe(counter);

        Thread.sleep(1000L);

        aSubscription.unsubscribe();

        executorService.shutdown();

        LOGGER.info("count: " + this.counter.get());

    }

    private class Counter implements Action1<String> {

        @Override
        public void call(final String s) {

            counter.incrementAndGet();
        }
    }
}
