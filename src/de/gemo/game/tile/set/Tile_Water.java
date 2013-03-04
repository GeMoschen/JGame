package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_Water extends IsoTile {

    public Tile_Water() {
        super(TileType.WATER, TextureManager.getTexture("tile_water").toAnimation(), false);
        this.buildPrice = 150;
        this.removalPrice = -150;
    }
}
