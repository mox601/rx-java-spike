package fm.mox.spikes.rx;

import fj.F;
import fj.P1;
import fj.data.Either;
import fj.data.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class EitherTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(EitherTestCase.class);

    @Test
    public void testName() throws Exception {

        final Either<RuntimeException, Integer> a = doThis(2);
        final Either<RuntimeException, Integer> b = doThis(1);
        assertEquals(1, (long) a.right().value());
        assertEquals("", b.left().value().getMessage());

        //compose results and exceptions

    }

    @Test
    public void catching_other_people_exceptions() {

        final Either<Exception, Integer> result = divide(4, 2);
        final Either<Exception, Integer> failure = divide(4, 0);
        assertEquals((long) 2, (long) result.right().value());
        assertEquals("/ by zero", failure.left().value().getMessage());
    }

    @Test
    public void option_test_success() {

        final Option result = divide(4.0, 2);
        assertEquals(2.0, (Double) result.some(), 0.1);
    }

    @Test
    public void option_test_failure() {

        final Option result = divide(4.0, 0);
        assertEquals(Option.none(), result);

    }

    //https://apocalisp.wordpress.com/2008/06/04/throwing-away-throws/
    @Test
    public void testLiftLength() throws Exception {

        final String b = "b";
        final P1<Either<Exception, Integer>> liftLenghtProduct =
                new P1<Either<Exception, Integer>>() {
                    @Override
                    public Either<Exception, Integer> _1() {

                        try {
                            return Either.right(length(b));
                        } catch (Exception e) {
                            return Either.left(e);
                        }

                    }
                };

        assertFalse(liftLenghtProduct._1().isLeft());
        assertEquals((int) liftLenghtProduct._1().right().value(), 1);

        final F<String, Either<Exception, Integer>> liftedLengthF =
                new F<String, Either<Exception, Integer>>() {
                    @Override
                    public Either<Exception, Integer> f(final String s) {

                        try {
                            return Either.right(length(s));
                        } catch (Exception e) {
                            return Either.left(e);
                        }
                    }
                };

        assertTrue(liftedLengthF.f("a").isRight());

        final Either<Exception, Integer> anException = liftedLengthF.f(null);
        assertTrue(anException.isLeft());
        assertNotNull(anException.left().value());
        final Exception value = anException.left().value();
        assertTrue(value instanceof NullPointerException);

    }

    private static Integer length(final String a) {

        return a.length();
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

    public static Option<Double> divide(double x, double y) {

        if (y == 0) {
            return Option.none();
        }
        return Option.some(x / y);
    }

}
