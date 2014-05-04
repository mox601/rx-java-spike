package org.mox.spikes.rx;

import org.mox.spikes.rx.model.VideoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ServiceTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ServiceTestCase.class);

    private Service service;

    private ExecutorService executorService;

    @BeforeClass
    public void setUp() throws Exception {

        final Runtime runtime = Runtime.getRuntime();
        final int availableProcessors = runtime.availableProcessors();
        this.executorService = Executors.newFixedThreadPool(availableProcessors);
        final UserRepository aUserRepository = new UserRepository(
                this.executorService);
        final VideoMetadataRepository videoMetadataRepository = new VideoMetadataRepository(
                this.executorService);
        final BookmarkRepository bookmarkRepository = new BookmarkRepository(
                this.executorService);

        this.service = new Service(aUserRepository, videoMetadataRepository,
                                   bookmarkRepository);
    }

    @AfterClass
    public void tearDown() throws Exception {

        this.executorService.shutdown();

    }

    @Test
    public void testVideoForUser() throws Exception {

        final Long startNsec = System.nanoTime();

        final Observable<VideoDTO> videoForUser = this.service
                .getVideoForUser("12345", "78965");

        final Subscription subscription = videoForUser.subscribe(
                new ItemLogger(),
                new ThrowableMessageLogger(),
                new ElapsedTimeLogger(startNsec)
        );

        final Long end = System.nanoTime();
        final Long elapsedNsec = (end - startNsec);

        LOGGER.info("test finished after nsec: '" + elapsedNsec + "'");

        Thread.sleep(1000L);

    }

    private class ElapsedTimeLogger implements Action0 {

        private final long startNsec;

        private ElapsedTimeLogger(long startNsec) {

            this.startNsec = startNsec;
        }

        @Override
        public void call() {

            long elapsedTimeNanosec = System.nanoTime() - this.startNsec;
            LOGGER.info("" + elapsedTimeNanosec);
            assertTrue(true);
        }
    }

    private class ThrowableMessageLogger implements Action1<Throwable> {

        @Override
        public void call(Throwable throwable) {

//            LOGGER.info("emsg: " + throwable.getMessage());
        }
    }

    private class ItemLogger implements Action1<VideoDTO> {

        @Override
        public void call(VideoDTO videoDTO) {

//            LOGGER.info("received videoDto " + videoDTO.toString());
        }
    }
}

