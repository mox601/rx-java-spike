package org.mox.spikes.rx.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class User {

    private String name;

    private String preferredLanguage;

    public User(String name, String preferredLanguage) {

        this.name = name;
        this.preferredLanguage = preferredLanguage;
    }

    public String getName() {

        return name;
    }

    public String getPreferredLanguage() {

        return preferredLanguage;
    }
}
