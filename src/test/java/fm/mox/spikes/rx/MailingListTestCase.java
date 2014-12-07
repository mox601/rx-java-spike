package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;

/**
 * on rxjava mailing list shixiong zhu 17/09/2014
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class MailingListTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailingListTestCase.class);

    @Test
    public void testName() throws Exception {

        final Observable<Integer> orders = Observable.just(1, 2, 3, 4);

        final ConnectableObservable<Integer> publishedOrders = orders.publish();

        final Func1<Integer, Boolean> pair = new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {

                return integer % 2 == 0;
            }
        };

        final Func1<Integer, Boolean> odd = new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {

                return integer % 2 != 0;
            }
        };
        final Observer<Integer> brokerA = new Broker("A");
        publishedOrders.filter(pair).subscribe(brokerA);

        final Observer<Integer> brokerB = new Broker("B");
        publishedOrders.filter(odd).subscribe(brokerB);

        publishedOrders.connect();

        Thread.sleep(100L);

    }

    private class Broker implements Observer<Integer> {

        private String type;

        private Broker(final String type) {

            this.type = type;
        }

        @Override
        public void onCompleted() {

            LOGGER.info(this.type + " onCompleted");

        }

        @Override
        public void onError(Throwable e) {

            LOGGER.info("onError");
        }

        @Override
        public void onNext(Integer integer) {

            LOGGER.info(this.type + " onNext " + integer);

        }
    }
}
