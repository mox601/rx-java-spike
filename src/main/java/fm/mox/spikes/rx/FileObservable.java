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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static rx.Observable.OnSubscribe;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class FileObservable {

    public static Observable<File> create(final File aFile) {

        //TODO refine threading
        return Observable.create(new OnSubscribe<File>() {
            @Override
            public void call(final Subscriber<? super File> subscriber) {

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

                                try {
                                    raf.close();
                                    subscriber.onCompleted();
                                } catch (IOException e) {
                                    subscriber.onError(e);
                                }
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

    /* stream all files in a directory */
    public static Observable<Path> ls(final String aDirectory) {

        return Observable.create(new Observable.OnSubscribe<Path>() {
            @Override
            public void call(final Subscriber<? super Path> subscriber) {

                final Path startPath = Paths.get(aDirectory);
                try {
                    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {

                            subscriber.onNext(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    subscriber.onError(e);
                }

                subscriber.onCompleted();
            }

        });
    }
}
