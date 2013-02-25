package de.gemo.game.tile.set;

import org.newdawn.slick.Color;

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
        for (int x = 0; x > dimX; x--) {
            for (int y = 0; y > dimY; y--) {
                if (x != 0 || y != 0) {
                    isoMap.setTileUsed(tileX + x, tileY + y, tileX, tileY);
                }
            }
        }
        this.informNeighbours(tileX, tileY, isoMap);

        // add securitylevel
        updateSecurityLevel(isoMap, tileX, tileY, 10, true);
    }

    @Override
    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        Color.yellow.bind();
        glLineWidth(1f);
        glBegin(GL_LINE_LOOP);
        glVertex2i(-2 * halfTileWidth, -1 * halfTileHeight);
        glVertex2i(0, -3 * halfTileHeight);
        glVertex2i(+2 * halfTileWidth, -1 * halfTileHeight);
        glVertex2i(0, +halfTileHeight);
        glEnd();
        glEnable(GL_BLEND);
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        for (int x = 0; x > dimX; x--) {
            for (int y = 0; y > dimY; y--) {
                isoMap.setTileUnused(tileX + x, tileY + y);
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

}
