package de.gemo.game.tile;

public abstract class StreetManager {

    private static StreetManager INSTANCE = new StreetManager_1();

    public abstract int getAmountStreetsAround(int tileX, int tileY, IsoMap isoMap);

    public abstract IsoTile getIsoTile(int tileX, int tileY, IsoMap isoMap);

    public static int getStreetsAround(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getAmountStreetsAround(tileX, tileY, isoMap);
    }

    public static IsoTile getTile(int tileX, int tileY, IsoMap isoMap) {
        return INSTANCE.getIsoTile(tileX, tileY, isoMap);
    }
}
