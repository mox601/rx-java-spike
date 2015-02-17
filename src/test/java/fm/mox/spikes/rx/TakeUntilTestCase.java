package fm.mox.spikes.rx;

import org.testng.annotations.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

public class TakeUntilTestCase {

    @Test
    @SuppressWarnings("unchecked")
    public void testTakeUntil() throws InterruptedException {

        Subscription sSource = createMock(Subscription.class);
        Subscription sOther = createMock(Subscription.class);
        Observer<String> result = createMock(Observer.class);

        reset(sSource, sOther, result);

        result.onNext("one");
        expectLastCall().times(1);
        result.onNext("two");
        expectLastCall().times(1);

        result.onCompleted();
        expectLastCall().times(1);

        sSource.unsubscribe();
        expectLastCall().times(1);

        sOther.unsubscribe();
        expectLastCall().times(1);

        sOther.isUnsubscribed();
        expectLastCall().andReturn(true).times(1);

        sSource.isUnsubscribed();
        expectLastCall().andReturn(true).anyTimes();

        replay(sSource, sOther, result);

        final TestObservable source = new TestObservable(sSource);
        final TestObservable other = new TestObservable(sOther);

        final Observable<String> stringObservable = Observable.create(source).takeUntil(
                Observable.create(other));

        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnNext("three");
        source.sendOnNext("four");

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!source.s.isUnsubscribed()) {
                    source.sendOnNext("still going on");
                }

                source.sendOnCompleted();
                System.out.println("completed sent");

            }
        }, "producer").start();

        if (!source.s.isUnsubscribed()) {
            source.sendOnCompleted();
        }
        if (!other.s.isUnsubscribed()) {
            other.sendOnCompleted();
        }

        verify(sSource);
        verify(sOther);
        verify(result);

    }

    private static class TestObservable implements Observable.OnSubscribe<String> {

        Observer<? super String> observer = null;
        Subscription s;

        public TestObservable(Subscription s) {

            this.s = s;
        }

        /* used to simulate subscription */
        public void sendOnCompleted() {

            observer.onCompleted();
        }

        /* used to simulate subscription */
        public void sendOnNext(String value) {

            observer.onNext(value);
            System.out.println(value);

        }

        /* used to simulate subscription */
        public void sendOnError(Throwable e) {

            observer.onError(e);
        }

        @Override
        public void call(Subscriber<? super String> observer) {

            this.observer = observer;
            observer.add(s);
        }
    }
}
