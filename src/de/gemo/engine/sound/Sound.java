package de.gemo.engine.sound;

import java.net.URL;

public class Sound {
    private String name;
    private URL url;

    public Sound(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return this.url;
    }
}
