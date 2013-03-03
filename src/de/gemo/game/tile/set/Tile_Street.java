package de.gemo.game.tile.set;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.manager.PowerlineManager;
import de.gemo.game.tile.manager.StreetManager;

public abstract class Tile_Street extends IsoTile {

    public Tile_Street(int frame) {
        super(TileType.STREET, TextureManager.getTexture("tile_street").toAnimation(), true);
        this.animation.goToFrame(frame);
    }

    @Override
    public void select() {
        super.select();
    }

    @Override
    public void renderBuildPlace(IsoMap isoMap, int tileX, int tileY) {
        glPushMatrix();
        {
            IsoTile tile = StreetManager.getTile(tileX, tileY, isoMap);
            tile.setAlpha(0.5f);
            tile.render();
            tile.setAlpha(1f);
        }
        glPopMatrix();
    }

    @Override
    public void onRemove(IsoMap isoMap, int tileX, int tileY) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.getOverlay(tileX, tileY).onRemove(isoMap, tileX, tileY);
        }
    }

    @Override
    public void onPlace(IsoMap isoMap, int tileX, int tileY) {
        isoMap.setTile(tileX, tileY, StreetManager.getTile(tileX, tileY, isoMap), true);
        this.informAllNeighbours(isoMap, tileX, tileY);
    }

    @Override
    public boolean canBePlacedAt(IsoMap isoMap, int tileX, int tileY) {
        return super.canBePlacedAt(isoMap, tileX, tileY) && PowerlineManager.getPowerlinesAroundForOverlay(tileX, tileY, isoMap) < 4;
    }

    @Override
    public void onNeighbourChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        isoMap.setTile(tileX, tileY, StreetManager.getTile(tileX, tileY, isoMap), true);
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.getOverlay(tileX, tileY).onNeighbourChange(isoMap, tileX, tileY, neighbourX, neighbourY);
            isoMap.getOverlay(tileX, tileY).onNeighbourPowerChange(isoMap, tileX, tileY, neighbourX, neighbourY);
        }
    }

    @Override
    public void onNeighbourPowerChange(IsoMap isoMap, int tileX, int tileY, int neighbourX, int neighbourY) {
        if (isoMap.hasOverlay(tileX, tileY)) {
            isoMap.getOverlay(tileX, tileY).onNeighbourPowerChange(isoMap, tileX, tileY, neighbourX, neighbourY);
        }
    }

}
