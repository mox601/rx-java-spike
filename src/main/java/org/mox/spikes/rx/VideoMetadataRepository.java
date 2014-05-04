package org.mox.spikes.rx;

import org.mox.spikes.rx.model.VideoId;
import org.mox.spikes.rx.model.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

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

        final Observable.OnSubscribe<VideoMetadata> videoMetadataObservable =
                new Observable.OnSubscribe<VideoMetadata>() {

                    @Override
                    public void call(
                            final Subscriber<? super VideoMetadata> subscriber) {

                        Runnable videoMetadataProducer = new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    subscriber.onNext(houseOfCards);
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }

                            }
                        };

                        final Future<?> submit = executorService.submit(
                                videoMetadataProducer);
                    }
                };

        return Observable.create(videoMetadataObservable);

    }
}
