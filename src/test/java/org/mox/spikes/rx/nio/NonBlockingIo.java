package org.mox.spikes.rx.nio;

import org.testng.annotations.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static org.mox.spikes.rx.FileObservable.stream;
import static org.testng.Assert.assertEquals;
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
    }

    @Test
    public void testCase() throws Exception {

        final File aFile = new File("src/test/resources/log4j.properties", "r");
        assertEquals(byLine(stream(aFile)).first().toBlocking().single().getText(),
                "log4j.rootLogger=INFO, A1");

    }

}
