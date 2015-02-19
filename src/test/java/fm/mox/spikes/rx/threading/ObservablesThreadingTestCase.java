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
        final Func1<Long, Long> oneAdder = new FastOp();
        final Func1<Long, Long> doubler = new SlowOp();

        //aSource works on Schedulers.computation()
        final Observable<Long> aSource = Observable.just(1L, 2L, 3L, 4L, 5L).doOnNext(
                new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {

                        LOGGER.info("tick: " + aLong);
                    }
                });

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

        //runs on computation()
        final Observable<Long> afterLogging = doublePlusOneTick.observeOn(Schedulers.computation())
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {

                        LOGGER.info("on computation " + aLong);
                        return aLong;
                    }
                });

        //runs on computation()
        final Subscription anotherSubscription = afterLogging.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {

                LOGGER.info("on computation " + aLong);
            }
        });

        Thread.sleep(10000L);

        aSubscription.unsubscribe();
        anotherSubscription.unsubscribe();

    }

    private static class SlowOp implements Func1<Long, Long> {

        @Override
        public Long call(final Long aLong) {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //nop
            }

            LOGGER.info("slow: " + aLong);
            return aLong;
        }

    }

    private static class FastOp implements Func1<Long, Long> {

        @Override
        public Long call(final Long item) {

            LOGGER.info("fast: " + item);
            return item;
        }
    }
}
