package de.gemo.game.sim.core;

public enum EnumDir {

    //@formatter:off
    UNKNOWN(-21),
    TOP(0), 
    RIGHT(6), 
    BOTTOM(2), 
    LEFT(8),
    TOP_RIGHT(1), 
    BOTTOM_RIGHT(7), 
    BOTTOM_LEFT(3), 
    TOP_LEFT(9);
    //@formatter:on

    private final int value;

    private EnumDir(int value) {
        this.value = value;
    }

    public boolean isLineEqual(EnumDir otherDir) {
        return (Math.max(this.value, otherDir.value) - Math.min(this.value, otherDir.value) == 2);
    }

    public boolean isLine() {
        return (this.value != EnumDir.UNKNOWN.value && this.value % 2 == 0);
    }

    public boolean isDiagonal() {
        return (this.value != EnumDir.UNKNOWN.value && this.value % 2 == 1);
    }

    public boolean isVertical() {
        return (this.equals(EnumDir.TOP) || this.equals(EnumDir.BOTTOM));
    }

    public boolean isHorizontal() {
        return (this.equals(EnumDir.LEFT) || this.equals(EnumDir.RIGHT));
    }

    public boolean isNWToSE() {
        return (this.equals(EnumDir.TOP_LEFT) || this.equals(EnumDir.BOTTOM_RIGHT));
    }

    public boolean isNEToSW() {
        return (this.equals(EnumDir.TOP_RIGHT) || this.equals(EnumDir.BOTTOM_LEFT));
    }
}
