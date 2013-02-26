package de.gemo.game.tile;

import java.awt.Point;

import org.newdawn.slick.Color;

import de.gemo.engine.entity.Entity2D;
import de.gemo.engine.textures.Animation;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public abstract class IsoTile extends Entity2D {

    private final TileType type;
    private final boolean drawBackground;
    protected int offsetX, offsetY;

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
        glBegin(GL_LINE_LOOP);
        glVertex2i(-halfTileWidth, 0);
        glVertex2i(0, -halfTileHeight);
        glVertex2i(+halfTileWidth, 0);
        glVertex2i(0, +halfTileHeight - 1);
        glEnd();
        glEnable(GL_BLEND);
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

    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, this, false);
        this.informNeighbours(tileX, tileY, isoMap);
    }

    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
    }

    public final boolean isGettingPowered(int tileX, int tileY, IsoMap isoMap) {
        for (Point tile : PowerManager.getPowersourceTiles()) {
            if (isoMap.getPowerPath(tileX, tileY, tile.x, tile.y) != null) {
                return true;
            }
        }
        return false;
    }

    public final boolean isGettingPoweredByNeighbour(int tileX, int tileY, IsoMap isoMap) {
        return isoMap.getNorthEastInfo(tileX, tileY).isPowered() || isoMap.getNorthWestInfo(tileX, tileY).isPowered() || isoMap.getSouthWestInfo(tileX, tileY).isPowered() || isoMap.getNorthWestInfo(tileX, tileY).isPowered();
    }

    public final void informNeighbours(int tileX, int tileY, IsoMap isoMap) {
        TileInformation tileInfo = isoMap.getTileInformation(tileX, tileY);
        isoMap.getNorthEast(tileX, tileY).onNeighbourChange(tileX, tileY - 1, tileX, tileY, isoMap);
        isoMap.getSouthEast(tileX, tileY).onNeighbourChange(tileX + 1, tileY, tileX, tileY, isoMap);
        isoMap.getSouthWest(tileX, tileY).onNeighbourChange(tileX, tileY + 1, tileX, tileY, isoMap);
        isoMap.getNorthWest(tileX, tileY).onNeighbourChange(tileX - 1, tileY, tileX, tileY, isoMap);
    }

    public void onNeighbourChange(int tileX, int tileY, int neighbourX, int neighbourY, IsoMap isoMap) {
    }

    public void select() {
        TileDimension.setSelectedTile(this);
        TileDimension.setSize(1, 1);
    }

    public void renderBuildPlace(int tileX, int tileY, IsoMap isoMap) {
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

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public TileType getType() {
        return type;
    }
}
