package org.mox.spikes.rx;

import rx.Observable;
import rx.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static rx.Observable.OnSubscribe;
import static rx.Observable.create;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class FileObservable {

    public static Observable<String> stream(final File aFile) {

        //TODO refine threading
        return create(new OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {

                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        RandomAccessFile raf = null;
                        try {
                            raf = new RandomAccessFile(aFile, "r");
                        } catch (FileNotFoundException e) {
                            subscriber.onError(e);
                        }

                        if (raf != null) {
                            final FileChannel inChannel = raf.getChannel();

                            final ByteBuffer buf = ByteBuffer.allocate(8);

                            try {

                                //non-blocking or async?
                                int bytesRead = inChannel.read(buf);

                                StringBuilder sb = new StringBuilder();

                                while (bytesRead != -1 && !subscriber.isUnsubscribed()) {

                                    buf.flip();

                                    while (buf.hasRemaining() && !subscriber.isUnsubscribed()) {
                                        final char c = (char) buf.get();
                                        sb.append(c);
                                    }

                                    // don't emit if unsubscribed

                                    if (!subscriber.isUnsubscribed()) {

                                        //buffer finished, emit it
                                        subscriber.onNext(sb.toString());
                                        //reset sb
                                        sb = new StringBuilder();

                                        buf.clear();

                                        bytesRead = inChannel.read(buf);
                                    }

                                }

                                //TODO does the order count?
                                try {
                                    raf.close();
                                } catch (IOException e) {
                                    //nop
                                }
                                subscriber.onCompleted();

                            } catch (FileNotFoundException e) {
                                subscriber.onError(e);
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }

                        } else {
                            subscriber.onError(new RuntimeException());
                        }

                    }
                });

                thread.start();
            }
        });

    }
}
