package de.gemo.game.tile;

import java.awt.Point;
import java.util.Random;

import org.newdawn.slick.Color;

import de.gemo.engine.entity.Entity2D;
import de.gemo.engine.textures.Animation;
import de.gemo.game.tile.manager.PowerManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public abstract class IsoTile extends Entity2D {
    protected Random generator = new Random();
    protected int dimX = 1, dimY = 1;
    private final TileType type;
    private final boolean drawBackground;
    protected int offsetX, offsetY;
    protected int buildPrice = 0;
    protected int removalPrice = 0;

    public IsoTile(TileType type, Animation animation) {
        this(type, animation, false);
    }

    public IsoTile(TileType type, Animation animation, boolean drawBackground) {
        this(type, animation, drawBackground, 0, 0);
    }

    public IsoTile(TileType type, Animation animation, boolean drawBackground, int offsetX, int offsetY) {
        super(0, 0, animation);
        this.type = type;
        this.animation.goToFrame(0);
        this.drawBackground = drawBackground;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void renderOutline(int halfTileWidth, int halfTileHeight) {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        Color.yellow.bind();
        glLineWidth(1f);
        // glBegin(GL_LINE_LOOP);
        glVertex2i(-halfTileWidth, 0);
        glVertex2i(0, -halfTileHeight);
        glVertex2i(+halfTileWidth, 0);
        glVertex2i(0, +halfTileHeight - 1);
        // glEnd();
        glEnable(GL_BLEND);
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public void renderFilled(int halfTileWidth, int halfTileHeight, float r, float g, float b, float alpha) {
        // glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glColor4f(r, g, b, alpha);
        glBegin(GL_QUADS);
        glVertex2i(-halfTileWidth, 0);
        glVertex2i(0, -halfTileHeight);
        glVertex2i(+halfTileWidth, 0);
        glVertex2i(0, +halfTileHeight);
        glEnd();
    }

    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, this, false);
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
    }

    public final boolean isGettingPowered(IsoMap isoMap, int tileX, int tileY) {
        for (Point tile : PowerManager.getPowersourceTiles()) {
            if (isoMap.getPowerPath(tileX, tileY, tile.x, tile.y) != null) {
                return true;
            }
        }
        return false;
    }

    public final boolean isGettingPoweredByNeighbour(IsoMap isoMap, int tileX, int tileY) {
        return isoMap.getNorthEastInfo(tileX, tileY).isPowered() || isoMap.getNorthWestInfo(tileX, tileY).isPowered() || isoMap.getSouthWestInfo(tileX, tileY).isPowered() || isoMap.getNorthWestInfo(tileX, tileY).isPowered();
    }

    protected final void setPowerOfAllTiles(IsoMap isoMap, int tileX, int tileY, boolean power) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                isoMap.getTileInformation(tileInfo.getFatherX() + x, tileInfo.getFatherY() - y).setPowered(power);
            }
        }
    }

    public final void informAllNeighbours(IsoMap isoMap, int tileX, int tileY) {
        // inform neighbours
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                this.informNeighbours(isoMap, tileInfo.getFatherX() + x, tileInfo.getFatherY() - y);
            }
        }
    }

    public final void informAllNeighboursAboutPowerchange(IsoMap isoMap, int tileX, int tileY) {
        // inform neighbours
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                this.informNeighboursAboutPowerchange(isoMap, tileInfo.getFatherX() + x, tileInfo.getFatherY() - y);
            }
        }
    }

    private final void informNeighbours(IsoMap isoMap, int tileX, int tileY) {
        isoMap.getNorthEast(tileX, tileY).onNeighbourChange(isoMap, tileX, tileY - 1, tileX, tileY);
        isoMap.getSouthEast(tileX, tileY).onNeighbourChange(isoMap, tileX + 1, tileY, tileX, tileY);
        isoMap.getSouthWest(tileX, tileY).onNeighbourChange(isoMap, tileX, tileY + 1, tileX, tileY);
        isoMap.getNorthWest(tileX, tileY).onNeighbourChange(isoMap, tileX - 1, tileY, tileX, tileY);
    }

    private final void informNeighboursAboutPowerchange(IsoMap isoMap, int tileX, int tileY) {
        isoMap.getNorthEast(tileX, tileY).onNeighbourPowerChange(isoMap, tileX, tileY - 1, tileX, tileY);
        isoMap.getSouthEast(tileX, tileY).onNeighbourPowerChange(isoMap, tileX + 1, tileY, tileX, tileY);
        isoMap.getSouthWest(tileX, tileY).onNeighbourPowerChange(isoMap, tileX, tileY + 1, tileX, tileY);
        isoMap.getNorthWest(tileX, tileY).onNeighbourPowerChange(isoMap, tileX - 1, tileY, tileX, tileY);
    }

    public void onNeighbourChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
    }

    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
    }

    public void select() {
        TileDimension.setSelectedTile(this);
        TileDimension.setSize(dimX, dimY);
    }

    public void renderBuildPlace(IsoMap isoMap, int tileX, int tileY) {
        glPushMatrix();
        {
            this.setAlpha(0.5f);
            super.render();
            this.setAlpha(1f);
        }
        glPopMatrix();
    }

    @Override
    public void render(float r, float g, float b) {
        glPushMatrix();
        {
            glTranslatef(offsetX, offsetY, 0);
            super.render(r, g, b);
        }
        glPopMatrix();
    }

    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        for (int x = 0; x < this.dimX; x++) {
            for (int y = 0; y < this.dimY; y++) {
                if (isoMap.isTileUsed(tileX + x, tileY - y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public TileType getType() {
        return type;
    }

    public int getBuildPrice() {
        return buildPrice;
    }

    public int getRemovalPrice() {
        return removalPrice;
    }

    public void doTick(IsoMap isoMap, int tileX, int tileY) {
    }
}
