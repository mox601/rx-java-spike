package fm.mox.spikes.rx;

import fm.mox.spikes.rx.model.Bookmark;
import fm.mox.spikes.rx.model.User;
import fm.mox.spikes.rx.model.UserId;
import fm.mox.spikes.rx.model.VideoDTO;
import fm.mox.spikes.rx.model.VideoId;
import fm.mox.spikes.rx.model.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

import java.util.HashMap;
import java.util.Map;

import static rx.Observable.zip;

/**
 * an example from
 * https://github.com/Netflix/RxJava/wiki/How-To-Use
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final UserRepository userRepository;

    private final VideoMetadataRepository videoMetadataRepository;

    private final BookmarkRepository bookmarkRepository;

    public Service(final UserRepository userRepository,
                   final VideoMetadataRepository videoMetadataRepository,
                   final BookmarkRepository bookmarkRepository) {

        this.userRepository = userRepository;
        this.videoMetadataRepository = videoMetadataRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public Observable<VideoDTO> getVideoForUser(final String userId,
                                                final String videoId) {

        final Observable<UserDTO> userDTOObservable = this.userRepository
                .getUser(userId)
                .map(new UserToDTO());

        final Observable<VideoMetadata> videoMetadataObservable = userDTOObservable
                .flatMap(
                        new Func1<UserDTO, Observable<VideoMetadata>>() {

                            @Override
                            public Observable<VideoMetadata> call(
                                    final UserDTO userDTO) {

                                return videoMetadataRepository.getVideoMetadata(
                                        videoId,
                                        userDTO.preferredLanguage);
                            }
                        });

        final Observable<BookmarkDTO> bookmarkObservable =
                this.bookmarkRepository.getVideoBookmark(userId, videoId)
                                       .map(new BookmarkToDTO());

        final Observable<Map<String, Object>> zippedObservablesAsMap =
                zip(bookmarkObservable, videoMetadataObservable, userDTOObservable,
                        new ZipObjectsInMap());

        return zippedObservablesAsMap
                .map(new MapToVideoDTO(videoId, userId));
    }

    private static class UserDTO {

        protected final String name;

        protected final String preferredLanguage;

        public UserDTO(String name, String preferredLanguage) {

            this.name = name;
            this.preferredLanguage = preferredLanguage;
        }
    }

    private static class BookmarkDTO {

        protected final Long position;

        public BookmarkDTO(Long position) {

            this.position = position;
        }
    }

    private static class MapToVideoDTO implements Func1<Map<String, Object>, VideoDTO> {

        private final String videoId;

        private final String userId;

        //takes some params, just for testing purpose
        private MapToVideoDTO(String videoId, String userId) {

            this.videoId = videoId;
            this.userId = userId;
        }

        @Override
        public VideoDTO call(final Map<String, Object> stringObjectMap) {

            final VideoMetadata metadata = (VideoMetadata) stringObjectMap
                    .get("metadata-map");
            final String language = ((UserDTO) stringObjectMap
                    .get("user-map")).preferredLanguage;
            final Long position = ((BookmarkDTO) stringObjectMap.get(
                    "bookmark-map")).position;

            return new VideoDTO(new VideoId(this.videoId), metadata,
                                new UserId(this.userId),
                                language,
                                position);
        }
    }

    private static class UserToDTO implements Func1<User, UserDTO> {

        @Override
        public UserDTO call(final User user) {

            return new UserDTO(user.getName(),
                               user.getPreferredLanguage());
        }
    }

    private static class BookmarkToDTO implements Func1<Bookmark, BookmarkDTO> {

        @Override
        public BookmarkDTO call(final Bookmark bookmark) {

            return new BookmarkDTO(
                    bookmark.getPosition());
        }
    }

    private static class ZipObjectsInMap implements
                                  Func3<BookmarkDTO, VideoMetadata, UserDTO, Map<String, Object>> {

        @Override
        public Map<String, Object> call(
                final BookmarkDTO bookmarkMap,
                final VideoMetadata videoMetadata,
                final UserDTO userDto) {

            final Map<String, Object> stringMapMap =
                    new HashMap<String, Object>(3);

            stringMapMap.put("bookmark-map", bookmarkMap);
            stringMapMap.put("metadata-map",
                             videoMetadata);
            stringMapMap.put("user-map", userDto);

            return stringMapMap;
        }
    }
}
