package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_None extends IsoTile {

    public Tile_None() {
        super(TileType.NONE, TextureManager.getTexture("tile_mouse").toAnimation(), false);
    }

    @Override
    public void render() {
    }

    @Override
    public void render(float r, float g, float b) {
    }
}
