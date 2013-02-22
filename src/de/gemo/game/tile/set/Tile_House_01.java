package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.TileDimension;

public class Tile_House_01 extends IsoTile {

    public Tile_House_01() {
        super(TileType.QUARDER, TextureManager.getTexture("tile_house_01").toAnimation(), true, 25, -4);
    }

    @Override
    public void onPlace(int tileX, int tileY, IsoMap isoMap) {
        isoMap.setTile(tileX, tileY, this, true);
        for (int x = 0; x > -2; x--) {
            for (int y = 0; y > -3; y--) {
                if (x != 0 || y != 0) {
                    isoMap.setTileUsed(tileX + x, tileY + y, tileX, tileY);
                }
            }
        }
        this.informNeighbours(tileX, tileY, isoMap);
    }

    @Override
    public void onRemove(int tileX, int tileY, IsoMap isoMap) {
        for (int x = 0; x > -2; x--) {
            for (int y = 0; y > -3; y--) {
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
        TileDimension.setSize(2, 3);
    }

}
