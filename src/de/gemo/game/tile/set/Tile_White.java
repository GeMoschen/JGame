package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_White extends IsoTile {

    public Tile_White() {
        super(TileType.UNKNOWN, TextureManager.getTexture("tile_white").toAnimation());
    }
}
