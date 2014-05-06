package org.mox.spikes.rx.nio;

import org.testng.annotations.Test;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class NonBlockingIo {

    @Test
    public void testReadFile() throws Exception {

        //TODO how to set encoding?
        //how to demonstrate non-blocking behaviour?

        final RandomAccessFile aFile = new RandomAccessFile(
                "src/test/resources/log4j.properties", "rw");
        final FileChannel inChannel = aFile.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(8);

        //non-blocking
        int bytesRead = inChannel.read(buf);

        while (bytesRead != -1) {

            buf.flip();

            while (buf.hasRemaining()) {
                final char c = (char) buf.get();
                System.out.print(c);
            }

            buf.clear();

            bytesRead = inChannel.read(buf);
        }

        aFile.close();


    }
}
