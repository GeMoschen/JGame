package de.gemo.game.tile.set;

import org.newdawn.slick.Color;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.manager.PowerManager;
import de.gemo.game.tile.manager.TileManager;

import static org.lwjgl.opengl.GL11.*;

public class Tile_PowerPlant_01 extends IsoTile {

    private int POLLUTION_RADIUS = 10, JOB_RADIUS = 10;

    public Tile_PowerPlant_01() {
        super(TileType.POWERPLANT_01, TextureManager.getTexture("powerplant_01").toAnimation(), false, 64, -40);
        this.dimX = 3;
        this.dimY = 3;
        this.buildPrice = 1000;
        this.removalPrice = 450;
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, this, true);
        PowerManager.addPowersource(tileX, tileY);
        isoMap.getTileInformation(tileX, tileY).setPowered(true);

        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                if (x != 0 || y != 0) {
                    isoMap.setTile(tileX + x, tileY - y, TileManager.getTile("none"), true);
                    isoMap.setTileUsed(tileX + x, tileY - y, tileX, tileY);
                    PowerManager.addPowersource(tileX + x, tileY - y);
                    isoMap.getTileInformation(tileX + x, tileY - y).setPowered(true);
                }
            }
        }
        // inform neighbours
        this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);

        this.informAllNeighbours(isoMap, tileX, tileY);

        this.updatePollutionLevel(isoMap, tileX, tileY, POLLUTION_RADIUS, true);
        this.updateJobLevel(isoMap, tileX, tileY, JOB_RADIUS, true);
    }

    @Override
    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        Color.red.bind();
        glLineWidth(2f);
        glBegin(GL_LINE_LOOP);
        glVertex2i(-halfTileWidth + 1, 0);
        glVertex2i(+2 * halfTileWidth, -3 * halfTileHeight + 1);
        glVertex2i(+5 * halfTileWidth - 1, 0);
        glVertex2i(+2 * halfTileWidth, +3 * halfTileHeight - 1);
        glEnd();
        glEnable(GL_BLEND);
    }

    @Override
    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                isoMap.setTile(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y, TileManager.getTile("grass"), false);
                isoMap.setTileUnused(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y);
                isoMap.getTileInformation(tileX + x, tileY - y).setPowered(false);
                PowerManager.removePowersource(tileX + x, tileY - y);
            }
        }

        updatePollutionLevel(isoMap, tileX, tileY, POLLUTION_RADIUS, false);
        updateJobLevel(isoMap, tileX, tileY, JOB_RADIUS, false);

        // inform neighbours
        this.informAllNeighbours(isoMap, tileX, tileY);
        this.informAllNeighboursAboutPowerchange(isoMap, tileX, tileY);
    }

    private void updatePollutionLevel(IsoMap isoMap, int tileX, int tileY, int radius, boolean add) {
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
                        level *= 15;
                        if (add) {
                            tileInfo.addPollutionLevel(level);
                        } else {
                            tileInfo.addPollutionLevel(-level);
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
                        level *= 12;
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
