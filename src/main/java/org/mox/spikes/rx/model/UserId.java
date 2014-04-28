package org.mox.spikes.rx.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class UserId {

    private String id;

    public UserId(String id) {

        this.id = id;
    }

    public String getId() {

        return id;
    }

    @Override
    public String toString() {

        return "UserId{" +
                "id='" + id + '\'' +
                '}';
    }
}
