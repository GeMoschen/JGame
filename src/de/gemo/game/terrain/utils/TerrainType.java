package de.gemo.game.terrain.utils;

public enum TerrainType {

    AIR(0, 0, 0, 0, false),

    TERRAIN(255, 255, 255, 255, true),

    CRATER(180, 90, 20, 255, true),

    INVALID(0, 0, 0, 0, true);

    private final int r, g, b, a;
    private final boolean solid;

    private TerrainType(int r, int g, int b, int a, boolean solid) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
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
