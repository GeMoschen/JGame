package de.gemo.game.tile.set;

import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileDimension;

public class Tile_PowerPlant_01 extends IsoTile {

    private final int dimX = -3, dimY = -3;

    public Tile_PowerPlant_01() {
        super(TileType.POWERPLANT_01, TextureManager.getTexture("powerplant_01").toAnimation(), true, 32, -32);
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
    }

    @Override
    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        Color.yellow.bind();
        glLineWidth(1f);
        glBegin(GL_LINE_LOOP);
        glVertex2i(-3 * halfTileWidth, -2 * halfTileHeight);
        glVertex2i(0, -5 * halfTileHeight);
        glVertex2i(+3 * halfTileWidth, -2 * halfTileHeight);
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
