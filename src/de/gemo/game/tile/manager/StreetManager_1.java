package de.gemo.game.tile.manager;

import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.set.TileType;

public class StreetManager_1 extends StreetManager {

    private TileType tileType = TileType.STREET;

    public final int getAmountStreetsAround(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType() == tileType;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType() == tileType;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType() == tileType;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType() == tileType;

        int streetsAround = 0;
        if (NE)
            streetsAround++;
        if (NW)
            streetsAround++;
        if (SE)
            streetsAround++;
        if (SW)
            streetsAround++;

        return streetsAround;
    }

    public final IsoTile getIsoTile(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType() == tileType;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType() == tileType;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType() == tileType;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType() == tileType;

        int streetsAround = getStreetsAround(tileX, tileY, isoMap);
        if (streetsAround < 2) {
            if (NE || SW) {
                return TileManager.getTile("street_ne");
            }
        } else if (streetsAround == 2) {
            if (SE && SW) {
                return TileManager.getTile("street_n");
            }
            if (NE && NW) {
                return TileManager.getTile("street_s");
            }
            if (NE && SE) {
                return TileManager.getTile("street_w");
            }
            if (NW && SW) {
                return TileManager.getTile("street_e");
            }
            if (NE && SW) {
                return TileManager.getTile("street_ne");
            }
        } else if (streetsAround == 3) {
            if (!NE) {
                return TileManager.getTile("street_tintercept_ne");
            }
            if (!NW) {
                return TileManager.getTile("street_tintercept_nw");
            }
            if (!SW) {
                return TileManager.getTile("street_tintercept_sw");
            }
            if (!SE) {
                return TileManager.getTile("street_tintercept_se");
            }
        } else if (streetsAround == 4) {
            return TileManager.getTile("street_intercept");
        }
        return TileManager.getTile("street_nw");
    }
}
