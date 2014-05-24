package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
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
    public void shouldConsumeRepeatingStream() throws Exception {

        final List<String> aList = Arrays.asList("1", "2");
        final Observable<String> repeat = Observable.from(aList)
                                                    .repeat(Schedulers.io());

        final Counter counter = new Counter(this.counter);
        final Subscription aSubscription = repeat.subscribe(counter);

        Thread.sleep(1000L);

        aSubscription.unsubscribe();

        LOGGER.info("count: " + this.counter.get());

    }

    private static class Counter implements Action1<String> {

        private AtomicLong counter;

        private Counter(final AtomicLong counter) {

            this.counter = counter;
        }

        @Override
        public void call(final String s) {

            this.counter.incrementAndGet();
        }
    }
}
