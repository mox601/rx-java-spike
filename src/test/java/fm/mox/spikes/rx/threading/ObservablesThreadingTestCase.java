package fm.mox.spikes.rx.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ObservablesThreadingTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ObservablesThreadingTestCase.class);

    @Test(enabled = true)
    public void testName() throws Exception {

        //functions
        final Func1<Long, Long> oneAdder = new FastAdder(1);
        final Func1<Long, Long> doubler = new SlowMultiplier(2);

        //aSource works on Schedulers.computation()
        final Observable<Long> aSource = Observable.just(1L, 2L, 3L, 4L, 5L).doOnNext(
                new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {

                        LOGGER.info("tick: " + aLong);
                    }
                }).subscribeOn(Schedulers.computation());

        //still computation()
        final Observable<Long> plusOne = aSource.map(oneAdder);

        //runs on io()
        final Observable<Long> doublePlusOneTick = plusOne.observeOn(Schedulers.io()).map(doubler);

        //runs on io()
        final Subscription aSubscription = doublePlusOneTick.subscribe(new Subscriber<Long>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

                LOGGER.info("consumed " + aLong);


            }

        });

        Thread.sleep(10000L);

    }

    private static class SlowMultiplier implements Func1<Long, Long> {

        private final int factor;

        public SlowMultiplier(final int factor) {

            this.factor = factor;
        }

        @Override
        public Long call(final Long aLong) {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //nop
            }

            //could overflow
            final long result = aLong * factor;
            LOGGER.info("multiplied: " + result);
            return result;
        }

    }

    private static class FastAdder implements Func1<Long, Long> {

        private final int increment;

        public FastAdder(final int increment) {

            this.increment = increment;
        }

        @Override
        public Long call(final Long item) {

            final long result = item + increment;
            LOGGER.info("added: " + result);
            return result;
        }
    }
}
