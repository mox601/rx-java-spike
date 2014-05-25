package org.mox.spikes.rx;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class Util {

    public static long busyWork(long x) {

        long v = 0;
        for (int i = 0; i < x; i++) {
            v = factorial(x / 10);
        }
        return v;
    }

    public static long factorial(long x) {

        if (x == 1)
            return 1;
        return x * factorial(x - 1);
    }
}
