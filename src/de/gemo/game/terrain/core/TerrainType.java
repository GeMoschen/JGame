package de.gemo.game.terrain.core;

public enum TerrainType {

    AIR(0, 0, 0, 0, false),

    TERRAIN(1, 1, 1, 1, false),

    CRATER(0, 0, 0, 1, false);

    private final byte r, g, b, a;
    private final boolean solid;

    private TerrainType(int r, int g, int b, int a, boolean solid) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }

    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }

    public byte getA() {
        return a;
    }
}
