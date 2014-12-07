package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Subscriber;
import rx.Subscription;

import static rx.Observable.just;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class BackPressureTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackPressureTestCase.class);

    @Test
    public void testName() throws Exception {

        final Subscription subscription = just(1, 2, 3, 4).subscribe(new Subscriber<Integer>() {

            @Override
            public void onStart() {

                request(1);
            }

            @Override
            public void onCompleted() {

                System.out.println("done");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {

                System.out.println(integer);

                try {
                    LOGGER.info("sleeping");
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                request(1);
            }
        });

        Thread.sleep(1000L);
    }
}
