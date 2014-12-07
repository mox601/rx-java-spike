package fm.mox.spikes.rx;

import fm.mox.spikes.rx.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutorService;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

    public final ExecutorService executorService;

    private final BlockingDbRepository databaseRepository;

    public UserRepository(final ExecutorService executorService) {

        this.executorService = executorService;
        this.databaseRepository = new BlockingDbRepository();
    }

    public Observable<User> getUser(final String userId) {

        final Observable.OnSubscribe<User> userOnSubscribe = new Observable.OnSubscribe<User>() {

            @Override
            public void call(final Subscriber<? super User> subscriber) {

                final Runnable userProducer = new Runnable() {

                    @Override
                    public void run() {

                        final User aUser;

                        try {

                            aUser = databaseRepository.loadById(userId);
                            subscriber.onNext(aUser);
                            subscriber.onCompleted();

                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                        }
                    }
                };

                executorService.submit(userProducer);

            }
        };

        return Observable.create(userOnSubscribe);

    }

    // this is acting as a classic, blocking backend repository
    private static class BlockingDbRepository {

        public User loadById(String userId) throws InterruptedException {

            final User aUser = new User(userId, "es-us");
            Thread.sleep(100L);
            return aUser;
        }
    }
}
