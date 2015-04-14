package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class OperatorSuppressError<T> implements Observable.Operator<T, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorSuppressError.class);

    private final Action1<Throwable> onError;

    public OperatorSuppressError(final Action1<Throwable> onError) {

        this.onError = onError;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {

        return new Subscriber<T>(subscriber) {

            @Override
            public void onNext(T t) {

                subscriber.onNext(t);
            }

            @Override
            public void onError(Throwable e) {

                onError.call(e);
            }

            @Override
            public void onCompleted() {

                subscriber.onCompleted();
            }

        };
    }
}
