package org.mox.spikes.rx.nio;

import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.StringObservable;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static org.mox.spikes.rx.FileLinesObservable.scan;
import static org.testng.Assert.assertEquals;
import static rx.Observable.from;
import static rx.observables.StringObservable.byLine;

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

        final RandomAccessFile aFile = new RandomAccessFile("src/test/resources/log4j.properties",
                "r");
        assertEquals(byLine(scan(aFile)).flatMap(new LineToString()).first().toBlocking().single(),
                "log4j.rootLogger=INFO, A1");

    }

    private static class LineToString implements Func1<StringObservable.Line, Observable<String>> {

        @Override
        public Observable<String> call(final StringObservable.Line line) {

            return from(line.getText());
        }
    }

}
