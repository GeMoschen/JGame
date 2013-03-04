package de.gemo.game.tile.set;

public enum TileType {

    // TILES ON ONE TEXTURE : INDEX < 100
    UNKNOWN(-2, false, false),

    NONE(-1, false, false),

    MOUSE(0, false, false),

    GRASS(1, false, false),

    TREE_01(2, false, false),

    WATER(3, false, false),

    STREET(10, true, false, true),

    POWERLINE(20, true, true),

    // OVERLAYS : INDEX >= 100

    BULLDOZER(100, false, false),

    HOUSE_SMALL(101, true, true),

    HOUSE_MID_01(102, false, true),

    POWERPLANT_01(120, false, false),

    POLICE_01(130, false, true);

    public static int OVERLAY_START = 100;

    private final int index;
    private final boolean draggable;
    private final boolean needsPower;
    private final boolean canHaveOverlay;

    private TileType(int index, boolean draggable, boolean needsPower) {
        this(index, draggable, needsPower, false);
    }

    private TileType(int index, boolean draggable, boolean needsPower, boolean canHaveOverlay) {
        this.index = index;
        this.draggable = draggable;
        this.needsPower = needsPower;
        this.canHaveOverlay = canHaveOverlay;
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

    public boolean canHaveOverlay() {
        return canHaveOverlay;
    }

}
