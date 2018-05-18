package fm.mox.spikes.rx.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ObservablesThreadingTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ObservablesThreadingTestCase.class);

    @Test
    public void testName() throws Exception {

        //functions
        final Func1<Long, Long> fastNop = aLong -> {
            LOGGER.info("fast: " + aLong);
            return aLong;
        };
        final Func1<Long, Long> sleepingNop = aLong -> {
            sleepSilently(1000L);
            LOGGER.info("slow: " + aLong);
            return aLong;
        };

        //aSource works on Schedulers.computation()
        final Observable<Long> aSource = Observable.just(1L, 2L, 3L, 4L, 5L)
            .doOnNext(aLong -> LOGGER.info("tick: " + aLong));

        //still computation()
        final Observable<Long> plusOne = aSource.map(fastNop);

        //runs on io()
        final Observable<Long> doublePlusOneTick = plusOne.observeOn(Schedulers.io()).map(sleepingNop);

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
            .map(aLong -> {
                LOGGER.info("on computation " + aLong);
                return aLong;
            });

        //runs on computation()
        final Subscription anotherSubscription = afterLogging
            .subscribe(aLong -> LOGGER.info("on computation " + aLong));

        sleepSilently(10000L);

        aSubscription.unsubscribe();
        anotherSubscription.unsubscribe();
    }

    private static void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //nop
        }
    }
}
