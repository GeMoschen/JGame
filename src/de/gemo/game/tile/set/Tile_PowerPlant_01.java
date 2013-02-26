package de.gemo.game.tile.set;

import org.newdawn.slick.Color;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.PowerManager;
import de.gemo.game.tile.TileInformation;
import de.gemo.game.tile.TileManager;

import static org.lwjgl.opengl.GL11.*;

public class Tile_PowerPlant_01 extends IsoTile {

    public Tile_PowerPlant_01() {
        super(TileType.POWERPLANT_01, TextureManager.getTexture("powerplant_01").toAnimation(), false, 96, 0);
        this.dimX = 3;
        this.dimY = 3;
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
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
        this.informAllNeighbours(tileX, tileY, isoMap);
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
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                isoMap.setTile(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y, TileManager.getTile("grass"), false);
                isoMap.setTileUnused(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y);
                isoMap.getTileInformation(tileX + x, tileY - y).setPowered(false);
                PowerManager.removePowersource(tileX + x, tileY - y);
            }
        }

        // inform neighbours
        this.informAllNeighbours(tileX, tileY, isoMap);
    }
}
