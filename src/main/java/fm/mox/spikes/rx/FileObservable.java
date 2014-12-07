package fm.mox.spikes.rx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static rx.Observable.OnSubscribe;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class FileObservable {

    public static Observable<File> create(final File aFile) {

        //TODO refine threading
        return Observable.create(new OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {

                subscriber.onNext(aFile);
            }
        });
    }

    public static Observable<String> stream(final Observable<File> aFileObservable,
            final int bufferSize) {

        return aFileObservable.flatMap(new Func1<File, Observable<String>>() {
            @Override
            public Observable<String> call(final File aFile) {

                return Observable.create(new OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {

                        RandomAccessFile raf = null;
                        try {
                            raf = new RandomAccessFile(aFile, "r");
                        } catch (FileNotFoundException e) {
                            subscriber.onError(e);
                        }

                        if (raf != null) {
                            final FileChannel inChannel = raf.getChannel();

                            final ByteBuffer buf = ByteBuffer.allocate(bufferSize);

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
            }
        });

    }

    //TODO could be a Path observable
    /* stream all files in a directory */
    public static Observable<Path> ls(final String aDirectory) {

        return Observable.create(new OnSubscribe<Path>() {
            @Override
            public void call(final Subscriber<? super Path> subscriber) {

                final Path dir = Paths.get(aDirectory);

                DirectoryStream<Path> directoryStream = null;

                try {
                    directoryStream = Files.newDirectoryStream(dir);

                    final Iterator<Path> it = directoryStream.iterator();

                    while (it.hasNext() && !subscriber.isUnsubscribed()) {
                        final Path p = it.next();
                        subscriber.onNext(p);
                    }

                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    if (directoryStream != null) {
                        try {
                            directoryStream.close();
                        } catch (IOException e) {
                            //NOP
                            e.printStackTrace();
                        }
                    }
                }
                subscriber.onCompleted();
            }

        });
    }
}
