package de.gemo.game.tile.manager;

import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;

public abstract class TubeManager {

    private static TubeManager INSTANCE = new TubeManager_1();

    public abstract int getAmountTubesAround(int tileX, int tileY, IsoMap isoMap);

    public abstract IsoTile getIsoTile(int tileX, int tileY, IsoMap isoMap);

    public static int getTubesAround(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getAmountTubesAround(tileX, tileY, isoMap);
    }

    public static IsoTile getTile(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getIsoTile(tileX, tileY, isoMap);
    }
}
