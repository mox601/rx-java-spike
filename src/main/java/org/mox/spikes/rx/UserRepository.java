package org.mox.spikes.rx;

import org.mox.spikes.rx.model.User;
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
public class UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            UserRepository.class);

    public final ExecutorService executorService;

    private final BlockingDbRepository databaseRepository;

    public UserRepository(final ExecutorService executorService) {

        this.executorService = executorService;
        this.databaseRepository = new BlockingDbRepository();
    }

    public Observable<User> getUser(final String userId) {

        return Observable.create(new Observable.OnSubscribeFunc<User>() {

            @Override
            public Subscription onSubscribe(final Observer<? super User> observer) {

                Runnable userProducer = new Runnable() {

                    @Override
                    public void run() {

                        final User aUser;

                        try {

                            aUser = databaseRepository.loadById(userId);
                            observer.onNext(aUser);
                            observer.onCompleted();

                        } catch (InterruptedException e) {
                            observer.onError(e);
                        }
                    }
                };

                //uses an executor
                final Future<?> submitted = executorService.submit(userProducer);

                return new Subscription() {

                    @Override
                    public void unsubscribe() {

                        submitted.cancel(true);
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

    // this acts as a classic, blocking backend repository
    private class BlockingDbRepository {

        public User loadById(String userId) throws InterruptedException {

            final User aUser = new User(userId, "es-us");
            Thread.sleep(100L);
            return aUser;
        }
    }
}
