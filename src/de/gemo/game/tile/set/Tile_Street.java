package de.gemo.game.tile.set;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.StreetManager;

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
    public void renderBuildPlace(int tileX, int tileY, IsoMap isoMap) {
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
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, StreetManager.getTile(tileX, tileY, isoMap), true);
        this.informNeighbours(tileX, tileY, isoMap);
    }

    @Override
    public void onNeighbourChange(int tileX, int tileY, IsoMap isoMap, IsoTile otherTile) {
        isoMap.setTile(tileX, tileY, StreetManager.getTile(tileX, tileY, isoMap), true);
    }

}
