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
        if (isoMap.isTileConnectedToPowersource(tileX, tileY)) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            this.informAllNeighbours(tileX, tileY, isoMap);
        }
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        isoMap.getTileInformation(tileX, tileY).setPowered(false);
    }

    @Override
    public void onNeighbourChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            isoMap.getTileInformation(tileX, tileY).setPowered(true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighbours(tileX, tileY, isoMap);
            }
        } else {
            isoMap.getTileInformation(tileX, tileY).setPowered(false);
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighbours(tileX, tileY, isoMap);
            }
        }
    }
}
