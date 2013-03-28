package de.gemo.game.tile.set.watertubes;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.manager.TubeManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public abstract class Tile_Watertube extends IsoTile {

    public Tile_Watertube(int frame) {
        this(frame, 0, 0);
    }
    public Tile_Watertube(int frame, int offsetX, int offsetY) {
        super(TileType.WATERTUBE, TextureManager.getTexture("tile_watertube").toAnimation(), true, offsetX, offsetY);
        this.animation.goToFrame(frame);
        this.buildPrice = 75;
        this.removalPrice = 25;
    }

    @Override
    public void select() {
        super.select();
    }

    @Override
    public void render(float r, float g, float b) {
        this.offsetX = 0;
        this.offsetY = -12;
        super.render(r, g, b);
    }

    @Override
    public void renderBuildPlace(IsoMap isoMap, int tileX, int tileY) {
        glPushMatrix();
        {
            IsoTile tile = TubeManager.getTile(tileX, tileY, isoMap);
            tile.setAlpha(0.5f);
            tile.render();
            tile.setAlpha(1f);
        }
        glPopMatrix();
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, TubeManager.getTile(tileX, tileY, isoMap), true);
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        return super.canBePlacedAt(isoMap, tileX, tileY);
    }

    @Override
    public void onNeighbourChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        isoMap.setTile(tileX, tileY, TubeManager.getTile(tileX, tileY, isoMap), true);
    }
}
