package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;

/**
 * https://groups.google.com/forum/#!topic/rxjava/trm2n6S4FSc
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class OperatorSuppressErrorTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            OperatorSuppressErrorTestCase.class);

    @Test
    public void testName() throws Exception {

        Observable.just(1).lift(new OperatorSuppressError<>(log())).doOnNext(someStuff())
                .subscribe();

    }

    private Action1<? super Object> someStuff() {

        return new Action1<Object>() {
            @Override
            public void call(Object o) {
                //TODO
            }
        };
    }

    private Action1<Throwable> log() {

        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                LOGGER.info(throwable.toString());
            }
        };
    }
}
