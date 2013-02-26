package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileDimension;
import de.gemo.game.tile.TileInformation;

import static org.lwjgl.opengl.GL11.*;

public class Tile_Police_01 extends IsoTile {

    private final int dimX = -2, dimY = -2;

    public Tile_Police_01() {
        super(TileType.POLICE_01, TextureManager.getTexture("tile_police_01").toAnimation(), true, 62, -28);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, this, true);
        boolean isConnected = isoMap.isTileConnectedToPowersource(tileX, tileY);
        for (int x = 0; x > dimX; x--) {
            for (int y = 0; y > dimY; y--) {
                if (x != 0 || y != 0) {
                    isoMap.setTileUsed(tileX + x, tileY + y, tileX, tileY);
                    isConnected = isConnected || isoMap.isTileConnectedToPowersource(tileX + x, tileY + y);
                }
            }
        }

        if (isConnected) {
            for (int x = 0; x > dimX; x--) {
                for (int y = 0; y > dimY; y--) {
                    isoMap.getTileInformation(tileX + x, tileY + y).setPowered(true);
                    this.informNeighbours(tileX + x, tileY + y, isoMap);
                }
            }
        }

        // add securitylevel
        updateSecurityLevel(isoMap, tileX, tileY, 10, true);
    }

    @Override
    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 1, 1);
        glLineWidth(2f);
        glBegin(GL_LINE_LOOP);
        glVertex2i(-2 * halfTileWidth, -1 * halfTileHeight);
        glVertex2i(0, -3 * halfTileHeight);
        glVertex2i(+2 * halfTileWidth, -1 * halfTileHeight);
        glVertex2i(0, +halfTileHeight - 1);
        glEnd();
        glEnable(GL_BLEND);
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);

        // unpower and set unused
        for (int x = 0; x > dimX; x--) {
            for (int y = 0; y > dimY; y--) {
                isoMap.setTileUnused(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y);
                isoMap.getTileInformation(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y).setPowered(false);
            }
        }

        // inform neighbours
        for (int x = 0; x > dimX; x--) {
            for (int y = 0; y > dimY; y--) {
                this.informNeighbours(tileX + x, tileY + y, isoMap);
            }
        }

        // remove securitylevel
        updateSecurityLevel(isoMap, tileX, tileY, 10, false);
    }

    private void updateSecurityLevel(IsoMap isoMap, int tileX, int tileY, int radius, boolean add) {
        // remove securitylevel
        float distance = 0;
        int sqrRad = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                distance = (float) Math.abs((x + 0.5f) * (x + 0.5f) + (y + 0.5f) * (y + 0.5f));
                if (distance * 0.9f <= sqrRad) {
                    TileInformation tileInfo = isoMap.getTileInformation(tileX + x, tileY + y);
                    if (!tileInfo.isValid()) {
                        continue;
                    }
                    if (add) {
                        tileInfo.addSecureLevel(110 - distance);
                    } else {
                        tileInfo.addSecureLevel(-(110 - distance));
                    }
                }
            }
        }
    }

    @Override
    public void renderBuildPlace(int tileX, int tileY, IsoMap isoMap) {
        super.renderBuildPlace(tileX, tileY, isoMap);
    }

    @Override
    public void select() {
        super.select();
        TileDimension.setSize(-dimX, -dimY);
    }

    @Override
    public void onNeighbourChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
        boolean isNowPowered = isoMap.isTileConnectedToPowersource(tileX, tileY);
        boolean wasPowered = isoMap.getTileInformation(tileX, tileY).isPowered();
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        if (isNowPowered) {
            // power up
            for (int x = 0; x > dimX; x--) {
                for (int y = 0; y > dimY; y--) {
                    isoMap.getTileInformation(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y).setPowered(true);
                }
            }
            // neighbours will only get informed, if the power wasn't there but now is
            if (!wasPowered) {
                for (int x = 0; x > dimX; x--) {
                    for (int y = 0; y > dimY; y--) {
                        this.informNeighbours(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y, isoMap);
                    }
                }
            }
        } else {
            // power down
            for (int x = 0; x > dimX; x--) {
                for (int y = 0; y > dimY; y--) {
                    isoMap.getTileInformation(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y).setPowered(false);
                }
            }
            // neighbours will only get informed, if the power was there but isn't anymore
            if (wasPowered) {
                for (int x = 0; x > dimX; x--) {
                    for (int y = 0; y > dimY; y--) {
                        this.informNeighbours(tileInfo.getFatherX() + x, tileInfo.getFatherY() + y, isoMap);
                    }
                }
            }
        }
    }

}
