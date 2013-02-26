package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.TileManager;

import static org.lwjgl.opengl.GL11.*;

public class Tile_Police_01 extends IsoTile {

    public Tile_Police_01() {
        super(TileType.POLICE_01, TextureManager.getTexture("tile_police_01").toAnimation(), false, 94, -12);
        this.dimX = 2;
        this.dimY = 2;
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
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
            this.informAllNeighbours(tileX, tileY, isoMap);
        }

        // add securitylevel
        updateSecurityLevel(isoMap, tileX, tileY, 8, true);
    }

    @Override
    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        glLineWidth(2f);
        glBegin(GL_LINE_LOOP);
        glVertex2i(-halfTileWidth + 1, 0);
        glVertex2i(+1 * halfTileWidth, -2 * halfTileHeight + 1);
        glVertex2i(+3 * halfTileWidth - 1, 0);
        glVertex2i(+1 * halfTileWidth, +2 * halfTileHeight - 1);
        glEnd();
        glEnable(GL_BLEND);
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
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
        this.informAllNeighbours(tileX, tileY, isoMap);

        // remove securitylevel
        updateSecurityLevel(isoMap, tileX, tileY, 8, false);
    }

    private void updateSecurityLevel(IsoMap isoMap, int tileX, int tileY, int radius, boolean add) {
        // remove securitylevel
        float distance = 0;
        int sqrRad = radius * radius;
        int t = 10 * radius - 10;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                distance = (float) Math.abs((x + 0.5f) * (x + 0.5f) + (y + 0.5f) * (y + 0.5f));
                if (distance * 0.9f <= sqrRad) {
                    TileInformation tileInfo = isoMap.getTileInformation(tileX + x + 1, tileY + y);
                    if (!tileInfo.isValid()) {
                        continue;
                    }
                    if (add) {
                        tileInfo.addSecureLevel(t - distance * 0.9f);
                    } else {
                        tileInfo.addSecureLevel(-(t - distance * 0.9f));
                    }
                }
            }
        }
    }

    @Override
    public void onNeighbourChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        if (isNowPowered) {
            // power up
            this.setPowerOfAllTiles(tileX, tileY, isoMap, true);
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                this.informAllNeighbours(tileX, tileY, isoMap);
            }
        } else {
            // power down
            this.setPowerOfAllTiles(tileX, tileY, isoMap, false);

            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                this.informAllNeighbours(tileX, tileY, isoMap);
            }
        }
    }

}
