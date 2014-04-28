package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

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

        return Observable.create(new Observable.OnSubscribeFunc<String>() {

            @Override
            public Subscription onSubscribe(
                    final Observer<? super String> observer) {

                final Future<?> future = executorService.submit(new Runnable() {

                    @Override
                    public void run() {

                        while (running) {

                            observer.onNext(empty);
                        }

//                        LOGGER.info("completed");

                        observer.onCompleted();
                    }
                });

                return new Subscription() {

                    @Override
                    public void unsubscribe() {

                        running = false;
                        future.cancel(true);
                    }

                    @Override
                    public boolean isUnsubscribed() {

                        throw new UnsupportedOperationException(
                                "not implemented yet");
                    }
                };
            }
        });
    }
}
