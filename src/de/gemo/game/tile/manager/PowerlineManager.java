package de.gemo.game.tile.manager;

import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;

public abstract class PowerlineManager {

    private static PowerlineManager INSTANCE = new PowerlineManager_1();

    public abstract boolean canIsoOverlayBePlaced(int tileX, int tileY, IsoMap isoMap);

    public abstract int getAmountPowerlinesAroundForOverlay(int tileX, int tileY, IsoMap isoMap);

    public abstract int getAmountPowerlinesAround(int tileX, int tileY, IsoMap isoMap);

    public abstract IsoTile getIsoTile(int tileX, int tileY, IsoMap isoMap);

    public abstract IsoTile getIsoTileForOverlay(int tileX, int tileY, IsoMap isoMap);

    public static boolean canOverlayBePlaced(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.canIsoOverlayBePlaced(tileX, tileY, isoMap);
    }

    public static int getPowerlinesAround(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getAmountPowerlinesAround(tileX, tileY, isoMap);
    }

    public static IsoTile getTile(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getIsoTile(tileX, tileY, isoMap);
    }

    public static int getPowerlinesAroundForOverlay(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getAmountPowerlinesAround(tileX, tileY, isoMap);
    }

    public static IsoTile getTileForOverlay(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getIsoTileForOverlay(tileX, tileY, isoMap);
    }
}
