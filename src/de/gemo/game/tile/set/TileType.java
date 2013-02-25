package de.gemo.game.tile.set;

public enum TileType {
    MOUSE(false),

    GRASS(false),

    STREET(true),

    UNKNOWN(false),

    BULLDOZER(false),

    HOUSE(true),

    POWERPLANT_01(false),

    POLICE_01(false);

    private final boolean draggable;

    private TileType(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isDraggable() {
        return draggable;
    }

}
