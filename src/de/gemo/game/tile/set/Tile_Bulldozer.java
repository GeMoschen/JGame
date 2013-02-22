package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileManager;

public class Tile_Bulldozer extends IsoTile {

    public Tile_Bulldozer() {
        super(TileType.GRASS, TextureManager.getTexture("bulldozer").toAnimation(), false, 5, 4);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, TileManager.getTile("grass"), false);
        this.informNeighbours(tileX, tileY, isoMap);
    }
}
