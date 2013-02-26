package de.gemo.game.tile.set;

public enum TileType {

    // TILES ON ONE TEXTURE : INDEX < 100
    UNKNOWN(-1, false),

    MOUSE(0, false),

    GRASS(1, false),

    STREET(2, true),

    // OVERLAYS : INDEX >= 100

    BULLDOZER(100, false),

    HOUSE(101, true),

    POWERPLANT_01(102, false),

    POLICE_01(103, false);

    public static int OVERLAY_START = 100;

    private final int index;
    private final boolean draggable;

    private TileType(int index, boolean draggable) {
        this.index = index;
        this.draggable = draggable;
    }

    public int getIndex() {
        return index;
    }

    public boolean isDraggable() {
        return draggable;
    }

}
