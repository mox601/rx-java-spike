package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class TutorialTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TutorialTestCase.class);

    @Test
    public void testBuildObservables() throws Exception {

        Observable<String> strings = Observable.from(Arrays.asList("a", "b", "c"));

        //single item
        Integer anyObject = 3;
        Observable<Integer> just = Observable.from(anyObject);

        //from scratch
        Observable.OnSubscribeFunc<Integer> aSingleItem = new Observable.OnSubscribeFunc<Integer>() {

            @Override
            public Subscription onSubscribe(Observer<? super Integer> observer) {

                observer.onNext(1);
                observer.onCompleted();

                return new Subscription() {

                    @Override
                    public void unsubscribe() {
                        //nop
                    }

                    @Override
                    public boolean isUnsubscribed() {

                        throw new UnsupportedOperationException(
                                "not implemented yet");
                    }
                };
            }
        };
        Observable<Integer> integerObservable = Observable.create(aSingleItem);

        Observer<Integer> aLoggingObserver = new Observer<Integer>() {

            @Override
            public void onCompleted() {
                //
            }

            @Override
            public void onError(Throwable e) {
                //
            }

            @Override
            public void onNext(Integer args) {

//                LOGGER.info(args + "");

            }
        };
        integerObservable.subscribe(aLoggingObserver);

        //
        Observable<Long> interval = Observable.interval(10, TimeUnit.MILLISECONDS);

    }

    @Test
    public void testObservables() throws Exception {

        final Observable<Integer> someInts = Observable.from(Arrays.asList(1, 2));
        final Completable observer = new Completable();
        assertFalse(observer.completed);
        final Func1<? super Integer, String> userLoader = new Func1<Integer, String>() {

            @Override
            public String call(Integer anId) {

                return "" + anId;
            }
        };

        final Observable<String> users = someInts.map(userLoader);

        final Subscription subscription = users.subscribe(observer);
        Thread.sleep(100L);

        assertTrue(observer.completed);

    }

    private class Completable implements Observer<String> {

        private boolean completed;

        @Override
        public void onCompleted() {

            this.completed = true;

        }

        @Override
        public void onError(Throwable e) {

            LOGGER.error("" + e.toString());
        }

        @Override
        public void onNext(String args) {

            LOGGER.info("" + args);

        }
    }
}
