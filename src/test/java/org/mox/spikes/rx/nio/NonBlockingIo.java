package org.mox.spikes.rx.nio;

import org.testng.annotations.Test;
import rx.Observable;
import rx.Subscriber;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class NonBlockingIo {

    @Test
    public void testNonBlockingFileReading() throws Exception {

        //TODO how to set encoding?

        final RandomAccessFile aFile = new RandomAccessFile("src/test/resources/log4j.properties",
                "rw");
        final FileChannel inChannel = aFile.getChannel();

        final ByteBuffer buf = ByteBuffer.allocate(8);

        //non-blocking
        int bytesRead = inChannel.read(buf);

        StringBuilder sb = new StringBuilder();

        while (bytesRead != -1) {

            buf.flip();

            while (buf.hasRemaining()) {
                final char c = (char) buf.get();

                sb.append(c);
                if (c == '\n') {
                    //line ended, print it and reset string builder
                    System.out.print(sb.toString());
                    sb = new StringBuilder();
                }
            }

            buf.clear();

            bytesRead = inChannel.read(buf);
        }

        aFile.close();

        //TODO wrap all this file reading in an Observable<String>
    }

    @Test
    public void testCase() throws Exception {

        final Observable<String> fileLinesObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {

                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                final RandomAccessFile aFile;

                                try {
                                    aFile = new RandomAccessFile(
                                            "src/test/resources/log4j.properties", "rw");

                                    final FileChannel inChannel = aFile.getChannel();

                                    final ByteBuffer buf = ByteBuffer.allocate(8);

                                    //non-blocking
                                    int bytesRead = inChannel.read(buf);

                                    StringBuilder sb = new StringBuilder();

                                    while (bytesRead != -1 && !subscriber.isUnsubscribed()) {

                                        buf.flip();

                                        // TODO read https://github.com/Netflix/RxJava/wiki/String-Observables#byline
                                        // emit each buffer contents, and then use byLine to emit one line at a time
                                        while (buf.hasRemaining() && !subscriber.isUnsubscribed()) {
                                            final char c = (char) buf.get();

                                            sb.append(c);
                                            if (c == '\n') {
                                                //line ended, emit
                                                subscriber.onNext(sb.toString());
                                                sb = new StringBuilder();
                                            }
                                        }

                                        buf.clear();

                                        bytesRead = inChannel.read(buf);
                                    }

                                    aFile.close();

                                    subscriber.onCompleted();

                                } catch (FileNotFoundException e) {
                                    subscriber.onError(e);
                                } catch (IOException e) {
                                    subscriber.onError(e);
                                }

                            }
                        };

                        //TODO fix threading behaviour
                        final ExecutorService executor = Executors.newFixedThreadPool(4);
                        executor.submit(runnable);

                    }
                });

    }
}
