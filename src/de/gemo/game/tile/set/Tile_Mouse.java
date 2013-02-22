package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_Mouse extends IsoTile {

    public Tile_Mouse() {
        super(TileType.MOUSE, TextureManager.getTexture("tile_mouse").toAnimation(), true, 0, +16);
    }

}
