package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
import rx.observables.StringObservable;

import java.nio.file.Path;
import java.util.Iterator;

import static fm.mox.spikes.rx.FileObservable.create;
import static fm.mox.spikes.rx.FileObservable.stream;
import static java.lang.Integer.valueOf;
import static org.testng.Assert.assertEquals;
import static rx.Observable.just;
import static rx.observables.StringObservable.byLine;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RxTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RxTestCase.class);

    @Test
    public void shouldDouble() throws Exception {

        final Observable<Integer> twoItems = just(1, 2);
        final Observable<Integer> doubler = twoItems.map(new Func1<Integer, Integer>() {

            @Override
            public Integer call(Integer integer) {

                return integer * 2;
            }
        });

        final BlockingObservable<Integer> integerBlockingObservable = doubler.toBlocking();

        final Iterator<Integer> iterator = integerBlockingObservable.getIterator();
        assertEquals(iterator.next(), valueOf(2));
        assertEquals(iterator.next(), valueOf(4));
    }

    @Test
    public void shouldCalculateLength() throws Exception {

        final Observable<Integer> aLength = length(just("this"));
        final BlockingObservable<Integer> blockingObservable = aLength.toBlocking();
        assertEquals(blockingObservable.single(), valueOf(4));
    }

    @Test
    public void testFilesObservable() throws Exception {

        final String aDirectory = "src/test/resources";

        //TODO refine threading
        final Observable<StringObservable.Line> linesObservable = FileObservable.ls(aDirectory)
                .filter(new Func1<Path, Boolean>() {
                    @Override
                    public Boolean call(final Path path) {

                        return path.toFile().isFile();
                    }
                }).flatMap(new Func1<Path, Observable<StringObservable.Line>>() {
                    @Override
                    public Observable<StringObservable.Line> call(final Path path) {

                        return byLine(stream(create(path.toFile()), 8));
                    }
                });
        linesObservable.subscribe(new Action1<StringObservable.Line>() {
            @Override
            public void call(StringObservable.Line s) {

                LOGGER.info("line: " + s.getText());
            }
        });

        Thread.sleep(1000L);

    }

    private static Observable<Integer> length(final Observable<String> stringObservable) {

        return stringObservable.map(new Func1<String, Integer>() {

            @Override
            public Integer call(final String s) {

                LOGGER.info("" + s);

                return s.length();
            }
        });

    }
}