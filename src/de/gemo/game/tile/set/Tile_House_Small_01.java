package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;

public class Tile_House_Small_01 extends IsoTile {

    public Tile_House_Small_01() {
        super(TileType.HOUSE, TextureManager.getTexture("tile_house_small_01").toAnimation(), true, 0, 2);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, this, true);
    }
}
