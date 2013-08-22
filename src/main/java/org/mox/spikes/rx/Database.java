package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;

public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    public static Observable<String> loadWholeDataset() {

        final Func1<Observer<String>, Subscription> func = new Func1<Observer<String>, Subscription>() {

            @Override
            public Subscription call(final Observer<String> stringObserver) {

                final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        for (int i = 0; i < 75; i++) {
                            stringObserver.onNext("a_value-" + i);
                        }
                        stringObserver.onCompleted();
                    }
                });

                t.start();

                final Subscription subscription = new Subscription() {

                    @Override
                    public void unsubscribe() {

                        t.interrupt();

                    }
                };

                return subscription;
            }
        };

        return Observable.create(func);
    }

}