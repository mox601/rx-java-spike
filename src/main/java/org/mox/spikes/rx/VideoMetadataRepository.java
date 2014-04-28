package org.mox.spikes.rx;

import org.mox.spikes.rx.model.VideoId;
import org.mox.spikes.rx.model.VideoMetadata;
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
public class VideoMetadataRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            VideoMetadataRepository.class);

    private final ExecutorService executorService;

    public VideoMetadataRepository(final ExecutorService executorService) {

        this.executorService = executorService;
    }

    public Observable<VideoMetadata> getVideoMetadata(final String videoId,
                                                      final String language) {

        final VideoId id = new VideoId(videoId);
        final VideoMetadata houseOfCards =
                new VideoMetadata(id, "House of Cards, episode 1 in " + language,
                                  "David Fincher", 3365L);

        return Observable.create(new Observable.OnSubscribeFunc<VideoMetadata>() {

            @Override
            public Subscription onSubscribe(
                    final Observer<? super VideoMetadata> observer) {

                Runnable videoMetadataProducer = new Runnable() {

                    @Override
                    public void run() {

                        observer.onNext(houseOfCards);
                        observer.onCompleted();
                    }
                };

                final Future<?> submit = executorService.submit(
                        videoMetadataProducer);

                return new Subscription() {

                    @Override
                    public void unsubscribe() {

                        submit.cancel(true);

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
