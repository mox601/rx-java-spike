package fm.mox.spikes.rx;

import static fm.mox.spikes.rx.FileObservable.create;
import static fm.mox.spikes.rx.FileObservable.stream;
import static java.lang.Integer.valueOf;
import static org.testng.Assert.assertEquals;
import static rx.Observable.just;
import static rx.observables.StringObservable.byLine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
import rx.observables.StringObservable;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RxTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RxTestCase.class);

    @Test
    public void shouldDouble() throws Exception {

        final Observable<Integer> twoItems = just(1, 2);
        final Observable<Integer> doubler = twoItems.map(integer -> integer * 2);

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
        Subscription subscribe = FileObservable.ls(aDirectory)
            .filter(path -> path.toFile().isFile())
            .flatMap((Func1<Path, Observable<StringObservable.Line>>) path -> byLine(stream(create(path.toFile()), 8)))
            .subscribe(s -> LOGGER.info("line: " + s.getText()));
        Thread.sleep(1000L);
    }

    public List<String> query(String q, int request) {

        return Arrays.asList("a", "b");
    }

    public Observable<String> queryObservable(final String q, final int request) {

        return Observable.just(q).map(str -> query(str, request)).filter(item -> true) // Remove some stale items
            .collect(ArrayList::new, (Action2<List<String>, List<String>>) List::addAll)
            .flatMap((Func1<List<String>, Observable<String>>) results -> {
                if (results.size() < request) {
                    return Observable
                        .from(results)
                        .concatWith(queryObservable(q, request - results.size()));
                } else {
                    return Observable.from(results);
                }
            })
            .take(request);
    }

    /* example by Shixiong Zhu <zsxwing@gmail.com> 26/3/205 7:58 AM (2 hours ago) to Todd, rxjava */
    //TODO can be similar to a pagination?
    @Test
    public void foo() {
        queryObservable("foo", 20).subscribe(x -> LOGGER.info("" + x));
    }

    private static Observable<Integer> length(final Observable<String> stringObservable) {
        return stringObservable.map(s -> {
            LOGGER.info("" + s);
            return s.length();
        });
    }
}
