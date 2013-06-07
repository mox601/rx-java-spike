package org.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class AppTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTestCase.class);

    @Test
    public void shouldBeGreen() {
        LOGGER.info("abc");
        assertTrue(true);
    }

}
