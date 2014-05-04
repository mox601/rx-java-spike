package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class InfiniteStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            InfiniteStream.class);

    private final ExecutorService executorService;

    private boolean running = true;

    private String empty;

    public InfiniteStream(final ExecutorService executorService) {

        this.running = true;
        this.executorService = executorService;
        this.empty = "";

    }

    public Observable<String> start() {

        final Observable.OnSubscribe<String> stringOnSubscribe = new Observable.OnSubscribe<String>() {

            @Override
            public void call(final Subscriber<? super String> subscriber) {

                final Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                        while (running) {

                            subscriber.onNext(empty);
                        }

                        subscriber.onCompleted();
                    }
                };

                final Future<?> future = executorService.submit(runnable);

            }
        };

        return Observable.create(stringOnSubscribe);

    }
}
