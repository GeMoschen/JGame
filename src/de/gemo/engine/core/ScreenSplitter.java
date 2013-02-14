package de.gemo.engine.core;

public class ScreenSplitter {

    private static int areaSize = 256;

    public static void setChunkSize(int chunkSize) {
        ScreenSplitter.areaSize = chunkSize;
    }

    public static int getAreaSize() {
        return areaSize;
    }

    public static int getScreenX(float x) {
        return (int) x >> 8;
    }

    public static int getScreenY(float y) {
        return (int) y >> 8;
    }
}
