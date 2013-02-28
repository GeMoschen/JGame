package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileManager;

public class Tile_Bulldozer extends IsoTile {

    public Tile_Bulldozer() {
        super(TileType.BULLDOZER, TextureManager.getTexture("bulldozer").toAnimation(), false, 5, 4);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            IsoTile removal = isoMap.getOverlay(tileX, tileY);
            isoMap.removeOverlay(tileX, tileY);
            removal.onRemove(tileX, tileY, isoMap);
            this.informAllNeighbours(tileX, tileY, isoMap);
            return;
        }
        isoMap.setTile(tileX, tileY, TileManager.getTile("grass"), false);
        this.informAllNeighbours(tileX, tileY, isoMap);
    }

    @Override
    public boolean canBePlacedAt(int tileX, int tileY, IsoMap isoMap) {
        return !super.canBePlacedAt(tileX, tileY, isoMap);
    }
}
