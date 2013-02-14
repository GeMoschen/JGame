package de.gemo.engine.core;

public class ScreenCoordinates {
    private static int neighbourRange = 1;
    private int areaX, areaY;

    /**
     * Set the current neighbourRange
     * 
     * @param neighbourRange
     *            - the new neighbourRange (default is <b>1</b>)
     */
    public static void setNeighbourRange(int neighbourRange) {
        ScreenCoordinates.neighbourRange = neighbourRange;
    }

    /**
     * Get the current neighbourRange
     * 
     * @return the neighbourRange (default is <b>1</b>)
     */
    public static int getNeighbourRange() {
        return neighbourRange;
    }

    /**
     * Static method to create a new ScreenCoordinate, based on the given x/y-positions
     * 
     * @param x
     * @param y
     * @return a new ScreenCoordinate
     */
    public static ScreenCoordinates create(float x, float y) {
        return new ScreenCoordinates(ScreenSplitter.getScreenX(x), ScreenSplitter.getScreenY(y));
    }

    /**
     * Private constructor
     * 
     * @param areaX
     * @param areaY
     */
    private ScreenCoordinates(int areaX, int areaY) {
        this.areaX = areaX;
        this.areaY = areaY;
    }

    /**
     * Update the ScreenCoordinates.
     * 
     * @param x
     *            - the x-position of the object
     * @param y
     *            - the y-position of the object
     * @return <b>true</b> if the coordinates have changed, otherwise <b>false</b>
     */
    public boolean update(int x, int y) {
        int oldAreaX = this.areaX;
        int oldAreaY = this.areaY;
        this.areaX = ScreenSplitter.getScreenX(x);
        this.areaY = ScreenSplitter.getScreenY(y);
        return oldAreaX != areaX || oldAreaY != areaY;
    }

    /**
     * Get the areaX
     * 
     * @return the current areaX
     */
    public int getAreaX() {
        return areaX;
    }

    /**
     * Get the areaY
     * 
     * @return the current areaY
     */
    public int getAreaY() {
        return areaY;
    }

    /**
     * Check if another ScreenCoordinate is a neighbour of the current.
     * 
     * @param other
     *            - the other ScreenCoordinate
     * @return <b>true</b> if the other ScreenCoordinate is within the neighbourrange, otherwise <b>false</b>
     */
    public boolean isNeighbour(ScreenCoordinates other) {
        return (areaX >= other.areaX - neighbourRange && areaX <= other.areaX + neighbourRange && areaY >= other.areaY - neighbourRange && areaY <= other.areaY + neighbourRange);
    }

    /**
     * Check if another ScreenCoordinate is a neighbour of the current.
     * 
     * @param other
     *            - the other ScreenCoordinate
     * @param neighbourRange
     *            - the range to check within
     * @return <b>true</b> if the other ScreenCoordinate is within the neighbourrange, otherwise <b>false</b>
     */
    public boolean isNeighbour(ScreenCoordinates other, int neighbourRange) {
        return (areaX >= other.areaX - neighbourRange && areaX <= other.areaX + neighbourRange && areaY >= other.areaY - neighbourRange && areaY <= other.areaY + neighbourRange);
    }

    @Override
    public String toString() {
        return "ScreenCoordinates { " + areaX + " ; " + areaY + " }";
    }
}
