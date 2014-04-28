package org.mox.spikes.rx.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class VideoDTO {

    private VideoId id;

    private VideoMetadata metadata;

    private UserId userId;

    private String language;

    private Long bookmark;

    public VideoDTO(VideoId id, VideoMetadata metadata, UserId userId,
                    String language, Long bookmark) {

        this.id = id;
        this.metadata = metadata;
        this.userId = userId;
        this.language = language;
        this.bookmark = bookmark;
    }

    public VideoId getId() {

        return id;
    }

    public VideoMetadata getMetadata() {

        return metadata;
    }

    public UserId getUserId() {

        return userId;
    }

    public String getLanguage() {

        return language;
    }

    public Long getBookmark() {

        return bookmark;
    }

    @Override
    public String toString() {

        return "VideoDTO{" +
                "id=" + id +
                ", metadata=" + metadata +
                ", userId=" + userId +
                ", language='" + language + '\'' +
                ", bookmark=" + bookmark +
                '}';
    }
}
