package de.gemo.engine.core;

public class ScreenSplitter {

    private static int areaSize = 0;
    private static int shiftIndex = 0;

    static {
        setChunkSize(256);
    }

    public static void setChunkSize(int areaSize) {
        if (!isPowerOfTwo(areaSize) || areaSize < 1) {
            throw new RuntimeException("ERROR: '" + areaSize + "' is not a power of two!");
        }

        ScreenSplitter.areaSize = areaSize;
        calculateShiftIndex();
    }

    private static void calculateShiftIndex() {
        int index = 0;
        boolean found = false;
        while (!found) {
            if (Math.pow(2, index) == areaSize) {
                found = true;
                break;
            }
            index++;
        }
        shiftIndex = index;
    }

    private static boolean isPowerOfTwo(int number) {
        if ((number & -number) == number) {
            return true;
        } else {
            return false;
        }
    }

    public static int getAreaSize() {
        return areaSize;
    }

    public static int getScreenX(float x) {
        return (int) x >> shiftIndex;
    }

    public static int getScreenY(float y) {
        return (int) y >> shiftIndex;
    }
}
