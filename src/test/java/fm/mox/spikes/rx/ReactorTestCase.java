package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import reactor.Environment;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.rx.broadcast.Broadcaster;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ReactorTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorTestCase.class);

    static {
        // Only done once, statically, and shared across this classloader
        Environment.initialize();
    }

    @Test
    public void testName() throws Exception {
        // Create a Stream subclass we can sink values into
        Broadcaster<String> b = Broadcaster.create();

        b
                // dispatch onto a Thread other than 'main'
                .dispatchOn(Environment.cachedDispatcher())
                        // transform input to UC
                .map(new Function<String, String>() {
                    @Override
                    public String apply(final String s) {

                        return s.toUpperCase();
                    }
                })
                        // only let certain values pass through
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(final String s) {

                        return s.startsWith("HELLO");
                    }
                })
                        // produce demand
                .consume(new Consumer<String>() {
                    @Override
                    public void accept(final String s) {

                        System.out.println(Thread.currentThread() + ": " + s);
                    }
                });

        // Sink values into this Broadcaster
        b.onNext("Hello World!");
        // This won't print
        b.onNext("Goodbye World!");

        // Must wait for tasks in other threads to complete
        Thread.sleep(500);

    }
}
