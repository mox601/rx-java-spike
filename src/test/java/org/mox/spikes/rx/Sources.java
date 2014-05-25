package org.mox.spikes.rx;

import java.util.Iterator;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class Sources {

    public static Iterable<Long> numbers(final long num) {

        return new Iterable<Long>() {

            @Override
            public Iterator<Long> iterator() {

                return new Iterator<Long>() {

                    long l = 0;

                    @Override
                    public boolean hasNext() {

                        return l < num;
                    }

                    @Override
                    public Long next() {

                        return l++;
                    }

                    @Override
                    public void remove() {
                        //nop
                    }

                };
            }

        };
    }

    public static Iterable<Long> million() {

        return numbers(1000000);
    }

    /**
     * Runs forever ... will loop over Long.MAX_VALUE if needed.
     */
    public static Iterable<Long> infinite() {

        return new Iterable<Long>() {

            @Override
            public Iterator<Long> iterator() {

                return new Iterator<Long>() {

                    long l = 0;

                    @Override
                    public boolean hasNext() {

                        return true;
                    }

                    @Override
                    public Long next() {

                        return l++;
                    }

                    @Override
                    public void remove() {
                        //nop
                    }

                };
            }

        };
    }

}
