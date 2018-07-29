package de.gemo.game.terrain.world.generators;

import de.gemo.game.terrain.utils.*;

public abstract class AbstractWorldGenerator {

    private final int _width, _height;
    protected final TerrainSettings _terrainSettings;

    public AbstractWorldGenerator(TerrainSettings terrainSettings, int width, int height) {
        _terrainSettings = terrainSettings;
        _width = width;
        _height = height;
    }

    protected int getBufferPosition(int x, int y) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            return (y * getWidth() + x) * CONSTANTS.BYTES_PER_PIXEL;
        } else {
            return 0;
        }
    }

    protected int getHeight() {
        return _height;
    }

    protected int getWidth() {
        return _width;
    }

    public TerrainSettings getTerrainSettings() {
        return _terrainSettings;
    }

    public final boolean[][] generate() {
        return createPerlinWorld();
    }

    public final boolean[][] generate(long seed) {
        _terrainSettings.setSeed(seed);
        return generate();
    }

    public final boolean[][] generate(String seed) {
        _terrainSettings.setSeed(seed);
        return generate();
    }

    protected abstract boolean[][] createPerlinWorld();
}
