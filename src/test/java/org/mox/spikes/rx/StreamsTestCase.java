package org.mox.spikes.rx;

import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StreamsTestCase {

    @Test
    public void testName() throws Exception {

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
