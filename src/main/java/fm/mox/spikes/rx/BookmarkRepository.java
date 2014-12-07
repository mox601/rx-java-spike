package fm.mox.spikes.rx;

import fm.mox.spikes.rx.model.Bookmark;
import rx.Observable;

import java.util.concurrent.ExecutorService;

import static rx.Observable.just;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class BookmarkRepository {

    private final ExecutorService executorService;

    public BookmarkRepository(ExecutorService executorService) {

        this.executorService = executorService;
    }

    public Observable<Bookmark> getVideoBookmark(final String userId,
                                                 final String videoId) {

        return just(new Bookmark(0L));
    }
}
