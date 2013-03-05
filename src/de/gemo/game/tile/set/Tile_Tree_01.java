package de.gemo.game.tile.set;

import de.gemo.engine.manager.TextureManager;
import de.gemo.game.tile.IsoTile;

public class Tile_Tree_01 extends IsoTile {

    public Tile_Tree_01() {
        super(TileType.TREE_01, TextureManager.getTexture("tile_tree_01").toAnimation(), true, 0, -24);
        this.buildPrice = 25;
        this.removalPrice = 50;
    }
}
