package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;

import java.io.File;

import static org.mox.spikes.rx.FileObservable.create;
import static org.mox.spikes.rx.FileObservable.stream;
import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class FileObservableTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileObservableTestCase.class);

    @Test
    public void testName() throws Exception {

        final File aFile = new File("src/test/resources/log4j.properties");
        final Observable<String> stream = stream(create(aFile), 8);
        //TODO finish it
        assertEquals(stream.toBlocking().first(), "log4j.ro");

    }
}
