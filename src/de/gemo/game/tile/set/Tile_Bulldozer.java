package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.manager.TileManager;

public class Tile_Bulldozer extends IsoTile {

    public Tile_Bulldozer() {
        super(TileType.BULLDOZER, TextureManager.getTexture("bulldozer").toAnimation(), false, 0, -2);
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            IsoTile removal = isoMap.getOverlay(tileX, tileY);
            isoMap.removeOverlay(tileX, tileY);
            removal.onRemove(isoMap, tileX, tileY);
            this.informAllNeighbours(isoMap, tileX, tileY);
            return;
        }
        isoMap.setTile(tileX, tileY, TileManager.getTile("grass"), false);
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        return !super.canBePlacedAt(isoMap, tileX, tileY);
    }
}
