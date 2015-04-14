package fm.mox.spikes.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
import rx.observables.StringObservable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    public List<String> query(String q, int request) {

        return Arrays.asList("a", "b");
    }

    public Observable<String> queryObservable(final String q, final int request) {

        return Observable.just(q).map(new Func1<String, List<String>>() {
            @Override
            public List<String> call(String str) {

                return query(str, request);
            }
        }).filter(new Func1<List<String>, Boolean>() {
            @Override
            public Boolean call(List<String> item) {

                return true;
            }
        }) // Remove some stale items
                .collect(new Func0<List<String>>() {
                    @Override
                    public List<String> call() {

                        return new ArrayList<>();
                    }
                }, new Action2<List<String>, List<String>>() {
                    @Override
                    public void call(List<String> l, List<String> item) {

                        l.addAll(item);
                    }
                }).flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> results) {

                        if (results.size() < request) {
                            return Observable
                                    .from(results)
                                    .concatWith(queryObservable(q, request - results.size()));
                        } else {
                            return Observable.from(results);
                        }
                    }
                }).take(request);
    }

    /* example by Shixiong Zhu <zsxwing@gmail.com> 26/3/205 7:58 AM (2 hours ago) to Todd, rxjava */
    //TODO can be similar to a pagination?
    @Test
    public void foo() {

        queryObservable("foo", 20).subscribe(new Action1<String>() {
            @Override
            public void call(String x) {

                LOGGER.info("" + x);
            }
        });
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
