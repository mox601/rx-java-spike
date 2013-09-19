package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    public static Observable<String> loadWholeDataset() {

        final Observable.OnSubscribeFunc<String> func = new Observable.OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(final Observer<? super String> observer) {

                final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        for (int i = 0; i < 750; i++) {
                            observer.onNext("a_value-" + i);
                        }
                        observer.onCompleted();
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