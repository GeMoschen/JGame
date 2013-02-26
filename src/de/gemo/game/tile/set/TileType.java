package de.gemo.game.tile.set;

public enum TileType {

    // TILES ON ONE TEXTURE : INDEX < 100
    UNKNOWN(-2, false, false),

    NONE(-1, false, false),

    MOUSE(0, false, false),

    GRASS(1, false, false),

    STREET(2, true, false),

    // OVERLAYS : INDEX >= 100

    BULLDOZER(100, false, false),

    HOUSE(101, true, true),

    POWERPLANT_01(102, false, false),

    POLICE_01(103, false, true);

    public static int OVERLAY_START = 100;

    private final int index;
    private final boolean draggable;
    private final boolean needsPower;

    private TileType(int index, boolean draggable, boolean needsPower) {
        this.index = index;
        this.draggable = draggable;
        this.needsPower = needsPower;
    }

    public int getIndex() {
        return index;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public boolean needsPower() {
        return needsPower;
    }

}
