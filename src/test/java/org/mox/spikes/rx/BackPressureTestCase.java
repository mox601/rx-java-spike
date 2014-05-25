package org.mox.spikes.rx;

import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class BackPressureTestCase {

    @Test
    public void testBackPressure() throws Exception {

        final AtomicInteger sentCount = new AtomicInteger();
        final AtomicInteger receivedCount = new AtomicInteger();

        Observable.from(Sources.million()).map(new Func1<Long, Object>() {

            @Override
            public Object call(Long i) {

                sentCount.incrementAndGet();
                return "Value_" + i;
            }
        }).observeOn(Schedulers.newThread()).map(new Func1<Object, Object>() {

            @Override
            public Object call(Object s) {

                receivedCount.incrementAndGet();
                // simulate doing computational work
                return Util.busyWork(1000);
            }
        }).throttleFirst(500, TimeUnit.MILLISECONDS)
                  .take(5)
                  .toBlockingObservable()
                  .forEach(new Action1<Object>() {

                      @Override
                      public void call(Object s) {

                          long diff = sentCount.get() - receivedCount.get();
                          System.out.println(Thread.currentThread().getName() + " " +
                                  "Sent: " + sentCount.get() + "  Received: "
                                          + receivedCount.get() + "  => " + diff
                          );
                      }
                  });
        long diff = sentCount.get() - receivedCount.get();
        if (diff > 10) {
//            fail("No back pressure. " + diff + " items buffered.");
            System.out.println("No back pressure. " + diff + " items buffered.");
        }

    }
}
