package fm.mox.spikes.rx.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class VideoMetadata {

    private VideoId id;

    private String title;

    private String director;

    private Long duration;

    public VideoMetadata(VideoId id, String title, String director, Long duration) {

        this.id = id;
        this.title = title;
        this.director = director;
        this.duration = duration;
    }

    public VideoId getId() {

        return id;
    }

    public String getTitle() {

        return title;
    }

    public String getDirector() {

        return director;
    }

    public Long getDuration() {

        return duration;
    }

    @Override
    public String toString() {

        return "VideoMetadata{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", duration=" + duration +
                '}';
    }
}
