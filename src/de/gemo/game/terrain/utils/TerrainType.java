package de.gemo.game.terrain.utils;

public enum TerrainType {

    AIR(0, 0, 0, 0, false),

    TERRAIN(255, 255, 255, 255, true),

    CRATER(0, 0, 0, 255, true),

    INVALID(0, 0, 0, 0, true);

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

    public static TerrainType byRGBA(byte r, byte g, byte b, byte a) {
        for (TerrainType type : TerrainType.values()) {
            if (type.r == r && type.g == g && type.b == b && type.a == a) {
                return type;
            }
        }
        return TerrainType.INVALID;
    }
}
