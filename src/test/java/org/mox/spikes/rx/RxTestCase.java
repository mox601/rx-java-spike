package org.mox.spikes.rx;

import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RxTestCase {

    @Test
    public void shouldDouble() throws Exception {

        final Observable<Integer> twoItems = Observable.from(1, 2);
        final Observable<Integer> doubler = twoItems.map(
                new Func1<Integer, Integer>() {

                    @Override
                    public Integer call(Integer integer) {

                        return integer * 2;
                    }
                }
        );

        final BlockingObservable<Integer> integerBlockingObservable = doubler
                .toBlocking();

        final Iterator<Integer> iterator = integerBlockingObservable.getIterator();
        assertEquals(iterator.next(), Integer.valueOf(2));
        assertEquals(iterator.next(), Integer.valueOf(4));
    }

    @Test
    public void shouldCalculateLength() throws Exception {

        final Observable<String> aName = Observable.just("this");
        final Observable<Integer> length = length(aName);
        final BlockingObservable<Integer> blockingObservable = length
                .toBlockingObservable();
        assertEquals(blockingObservable.single(), Integer.valueOf(4));
    }

    private Observable<Integer> length(final Observable<String> stringObservable) {

        return stringObservable.map(new Func1<String, Integer>() {

            @Override
            public Integer call(final String s) {

                return s.length();
            }
        });

    }

}
