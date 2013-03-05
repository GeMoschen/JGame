package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.manager.TileManager;

import static org.lwjgl.opengl.GL11.*;

public class Tile_Police_01 extends IsoTile {

    private final int SEC_RADIUS = 8, JOB_RADIUS = 4;

    public Tile_Police_01() {
        super(TileType.POLICE_01, TextureManager.getTexture("tile_police_01").toAnimation(), false, 32, -20);
        this.dimX = 2;
        this.dimY = 2;
        this.buildPrice = 500;
        this.removalPrice = 250;
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

        // remove securitylevel
        this.updateSecurityLevel(isoMap, tileX, tileY, SEC_RADIUS, false);
        this.updateJobLevel(isoMap, tileX, tileY, JOB_RADIUS, false);
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
                // add securitylevel
                this.updateSecurityLevel(isoMap, tileX, tileY, SEC_RADIUS, true);
                this.updateJobLevel(isoMap, tileX, tileY, JOB_RADIUS, true);
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        } else {
            // power down
            this.setPowerOfAllTiles(isoMap, tileX, tileY, false);

            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                // add securitylevel
                this.updateSecurityLevel(isoMap, tileX, tileY, SEC_RADIUS, false);
                this.updateJobLevel(isoMap, tileX, tileY, JOB_RADIUS, false);
                this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
            }
        }
    }

    private void updateSecurityLevel(IsoMap isoMap, int tileX, int tileY, int radius, boolean add) {
        // remove securitylevel
        float distance = 0;
        int sqrRad = radius * radius;
        float level;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                distance = (float) Math.abs((x + 0.5f) * (x + 0.5f) + (y + 0.5f) * (y + 0.5f));
                distance = (float) Math.sqrt(distance);
                if (distance * 0.9f <= sqrRad) {
                    TileInformation tileInfo = isoMap.getTileInformation(tileX + x + 1, tileY + y);
                    if (!tileInfo.isValid()) {
                        continue;
                    }
                    level = radius + 1 - distance;
                    if (level > 0) {
                        level *= 10;
                        if (add) {
                            tileInfo.addSecureLevel(level);
                        } else {
                            tileInfo.addSecureLevel(-level);
                        }
                    }
                }
            }
        }
    }

    private void updateJobLevel(IsoMap isoMap, int tileX, int tileY, int radius, boolean add) {
        float distance = 0;
        int sqrRad = radius * radius;
        float level;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                distance = (float) Math.abs((x) * (x) + (y + 1f) * (y + 1f));
                distance = (float) Math.sqrt(distance);
                if (distance * 0.9f <= sqrRad) {
                    TileInformation tileInfo = isoMap.getTileInformation(tileX + x + 1, tileY + y);
                    if (!tileInfo.isValid()) {
                        continue;
                    }
                    level = radius + 1 - distance;
                    if (level > 0) {
                        level *= 4;
                        if (add) {
                            tileInfo.addJobLevel(level);
                        } else {
                            tileInfo.addJobLevel(-level);
                        }
                    }
                }
            }
        }
    }

}
