package fm.mox.spikes.rx.stackoverflow;

import static org.testng.Assert.assertTrue;
import static rx.Observable.OnSubscribe;
import static rx.Observable.create;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * http://stackoverflow.com/questions/22284380/composing-async-observables-that-have-dependencies-using-rxjava
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class Q22284380TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(Q22284380TestCase.class);

    private AtomicBoolean completed = new AtomicBoolean(false);

    @Test
    public void testName() throws Exception {

        final OnSubscribe<Integer> onSubProduceTwoValues = subscriber -> {

            final Thread thread = new Thread(new Runnable() {

                public Integer i = 0;

                @Override
                public void run() {

                    final Integer max = 2;
                    while (i < max) {
                        subscriber.onNext(i);
                        i++;
                    }

                    subscriber.onCompleted();
                }
            });

            thread.start();
        };

        final Observable<Integer> values = create(onSubProduceTwoValues);

        final Observable<Integer> byTwoMultiplier = values
                .flatMap((Func1<Integer, Observable<Integer>>) this::doubleIt);

        byTwoMultiplier.subscribe(new Subscriber<Integer>() {

            @Override
            public void onNext(Integer a) {

                LOGGER.info("" + a);

            }

            @Override
            public void onCompleted() {

                completed.set(true);

            }

            @Override
            public void onError(Throwable e) {

                LOGGER.error(e.getMessage());
            }
        });

        Thread.sleep(1000L);
        assertTrue(completed.get());

    }

    private Observable<Integer> doubleIt(final Integer value) {

        return create(subscriber -> {

            final Thread thread = new Thread(() -> {

                try {
                    subscriber.onNext(value * 2);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            });

            thread.start();

        });
    }
}
