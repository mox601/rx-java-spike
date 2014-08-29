package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class BackPressureTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackPressureTestCase.class);

    @Test
    public void testName() throws Exception {

        Observable.just(1, 2, 3, 4).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

                request(1);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {

                System.out.println(integer);

                request(1);
            }
        });

    }
}
