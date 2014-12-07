package fm.mox.spikes.rx.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class VideoId {

    private final String id;

    public VideoId(String id) {

        this.id = id;
    }

    public String getId() {

        return id;
    }

    @Override
    public String toString() {

        return "VideoId{" +
                "id='" + id + '\'' +
                '}';
    }
}
