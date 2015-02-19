package fm.mox.spikes.rx;

import fj.data.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class EitherTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(EitherTestCase.class);

    @Test
    public void testName() throws Exception {

        Either<RuntimeException, Integer> a = doThis(2);
        Either<RuntimeException, Integer> b = doThis(1);

        assertEquals(1, (long) a.right().value());
        assertEquals("", b.left().value().getMessage());

        //compose results and exceptions

    }

    @Test
    public void catching_other_people_exceptions() {

        Either<Exception, Integer> result = divide(4, 2);
        assertEquals((long) 2, (long) result.right().value());
        Either<Exception, Integer> failure = divide(4, 0);
        assertEquals("/ by zero", failure.left().value().getMessage());
    }

    private static Either<RuntimeException, Integer> doThis(int i) {

        if (i % 2 == 0) {
            return Either.right(1);
        } else {
            return Either.left(new RuntimeException(""));
        }

    }

    public static Either<Exception, Integer> divide(int x, int y) {

        try {
            return Either.right(x / y);
        } catch (Exception e) {
            return Either.left(e);
        }
    }

}
