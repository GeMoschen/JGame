package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_Grass extends IsoTile {

    public Tile_Grass() {
        super(TileType.GRASS, TextureManager.getTexture("tile_grass").toAnimation());
        this.buildPrice = 150;
    }
}
