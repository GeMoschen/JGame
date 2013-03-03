package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;

public class Tile_Unknown extends IsoTile {

    public Tile_Unknown() {
        super(TileType.UNKNOWN, TextureManager.getTexture("tile_path").toAnimation());
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        super.onPlace(isoMap, tileX, tileY);
    }

}
