package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.manager.TileManager;

public class Tile_House_Mid_01 extends IsoTile {

    public Tile_House_Mid_01() {
        super(TileType.HOUSE_MID_01, TextureManager.getTexture("tile_house_mid_01").toAnimation(), false, 32, -6);
        this.dimX = 2;
        this.dimY = 2;
        this.buildPrice = 250;
        this.removalPrice = 150;
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, this, true);
        boolean isConnected = isoMap.isTileConnectedToPowersource(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                if (x != 0 || y != 0) {
                    isoMap.setTile(tileX + x, tileY - y, TileManager.getTile("none"), true);
                    isoMap.setTileUsed(tileX + x, tileY - y, tileX, tileY);
                    isConnected = isConnected || isoMap.isTileConnectedToPowersource(tileX + x, tileY - y);
                }
            }
        }

        if (isConnected) {
            this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
        }
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public void renderBuildPlace(IsoMap isoMap, int tileX, int tileY) {

        super.renderBuildPlace(isoMap, tileX, tileY);
    }

    @Override
    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);

        // unpower and set unused
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                isoMap.setTile(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y, TileManager.getTile("grass"), false);
                isoMap.setTileUnused(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y);
                isoMap.getTileInformation(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y).setPowered(false);
            }
        }

        // inform neighbours
        this.informAllNeighbours(isoMap, tileX, tileY);
        this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
    }

    @Override
    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            // power up
            this.setPowerOfAllTiles(isoMap, tileX, tileY, true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            // power down
            this.setPowerOfAllTiles(isoMap, tileX, tileY, false);

            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    @Override
    public void doTick(IsoMap isoMap, int tileX, int tileY) {
    }

    private void grow(IsoMap isoMap, int tileX, int tileY) {
    }
}
