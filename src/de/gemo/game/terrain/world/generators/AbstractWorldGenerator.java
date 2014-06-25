package de.gemo.game.terrain.world.generators;

import de.gemo.game.terrain.utils.*;

public abstract class AbstractWorldGenerator {

    private static final int BYTES_PER_PIXEL = 4;
    private final int width, height;
    protected final TerrainSettings terrainSettings;

    public AbstractWorldGenerator(TerrainSettings terrainSettings, int width, int height) {
        this.terrainSettings = terrainSettings;
        this.width = width;
        this.height = height;
    }

    protected int getBufferPosition(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
            return (y * this.getWidth() + x) * BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    protected int getHeight() {
        return height;
    }

    protected int getWidth() {
        return width;
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    public final boolean[][] generate() {
        return this.createPerlinWorld();
    }

    public final boolean[][] generate(long seed) {
        this.terrainSettings.setSeed(seed);
        return this.generate();
    }

    public final boolean[][] generate(String seed) {
        this.terrainSettings.setSeed(seed);
        return this.generate();
    }

    protected abstract boolean[][] createPerlinWorld();
}
