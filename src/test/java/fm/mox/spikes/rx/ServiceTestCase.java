package fm.mox.spikes.rx;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fm.mox.spikes.rx.model.VideoDTO;
import rx.Observable;
import rx.Subscription;

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
            videoDTO -> LOGGER.info("received videoDto " + videoDTO.toString()),
            throwable -> LOGGER.info("emsg: " + throwable.getMessage()),
            () -> {
                long elapsedTimeNanosec = System.nanoTime() - startNsec;
                LOGGER.info("" + elapsedTimeNanosec);
                assertTrue(true);
            }
        );

        final Long end = System.nanoTime();
        final Long elapsedNsec = (end - startNsec);

        LOGGER.info("test finished after nsec: '" + elapsedNsec + "'");

        Thread.sleep(1000L);

    }
}

