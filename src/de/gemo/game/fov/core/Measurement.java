package de.gemo.game.fov.core;

public class Measurement {
    private long start, end, duration;

    public Measurement() {
        this.start = System.nanoTime();
    }

    public void stop() {
        this.end = System.nanoTime();
        this.duration = this.end - this.start;
    }

    public long getDuration() {
        return duration;
    }

    public float getMS() {
        return duration / 1000000f;
    }
}
