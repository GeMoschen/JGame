package de.gemo.engine.core;

public class ScreenCoordinates {
    private int areaX, areaY;

    public static ScreenCoordinates create(float x, float y) {
        return new ScreenCoordinates(ScreenSplitter.getScreenX(x), ScreenSplitter.getScreenY(y));
    }

    private ScreenCoordinates(int areaX, int areaY) {
        this.areaX = areaX;
        this.areaY = areaY;
    }

    public void update(int x, int y) {
        this.areaX = ScreenSplitter.getScreenX(x);
        this.areaY = ScreenSplitter.getScreenY(y);
    }

    public int getAreaX() {
        return areaX;
    }

    public int getAreaY() {
        return areaY;
    }

    public boolean isNeighbour(ScreenCoordinates other) {
        return (areaX >= other.areaX - 1 && areaX <= other.areaX + 1 && areaY >= other.areaY - 1 && areaY <= other.areaY + 1);
    }

    @Override
    public String toString() {
        return "ScreenCoordinates { " + areaX + " ; " + areaY + " }";
    }
}
