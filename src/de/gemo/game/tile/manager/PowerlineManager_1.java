package de.gemo.game.tile.manager;

import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoTile;
import de.gemo.game.tile.set.TileType;

public class PowerlineManager_1 extends PowerlineManager {

    public final int getAmountPowerlinesAround(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getNorthEastOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getSouthEastOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getSouthWestOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getNorthWestOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;

        int powerlinesAround = 0;
        if (NE)
            powerlinesAround++;
        if (NW)
            powerlinesAround++;
        if (SE)
            powerlinesAround++;
        if (SW)
            powerlinesAround++;

        return powerlinesAround;
    }

    public final IsoTile getIsoTile(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getNorthEastOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getSouthEastOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getSouthWestOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1 || isoMap.getNorthWestOverlay(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;

        int powerlinesAround = getPowerlinesAround(tileX, tileY, isoMap);
        if (powerlinesAround < 2) {
            if (NE || SW) {
                return TileManager.getTile("powerline_ne");
            }
        } else if (powerlinesAround == 2) {
            if (SE && SW) {
                return TileManager.getTile("powerline_n");
            }
            if (NE && NW) {
                return TileManager.getTile("powerline_s");
            }
            if (NE && SE) {
                return TileManager.getTile("powerline_w");
            }
            if (NW && SW) {
                return TileManager.getTile("powerline_e");
            }
            if (NE && SW) {
                return TileManager.getTile("powerline_ne");
            }
        } else if (powerlinesAround == 3) {
            if (!NE) {
                return TileManager.getTile("powerline_tintercept_ne");
            }
            if (!NW) {
                return TileManager.getTile("powerline_tintercept_nw");
            }
            if (!SW) {
                return TileManager.getTile("powerline_tintercept_sw");
            }
            if (!SE) {
                return TileManager.getTile("powerline_tintercept_se");
            }
        } else if (powerlinesAround == 4) {
            return TileManager.getTile("powerline_intercept");
        }
        return TileManager.getTile("powerline_nw");
    }

    @Override
    public int getAmountPowerlinesAroundForOverlay(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;

        int powerlinesAround = 0;
        if (NE)
            powerlinesAround++;
        if (NW)
            powerlinesAround++;
        if (SE)
            powerlinesAround++;
        if (SW)
            powerlinesAround++;

        return powerlinesAround;
    }

    @Override
    public IsoTile getIsoTileForOverlay(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > TileType.POWERLINE.getIndex() - 1;

        int powerlinesAround = getPowerlinesAround(tileX, tileY, isoMap);
        if (powerlinesAround < 2) {
            if (NE || SW) {
                return TileManager.getTile("powerline_ne_overlay");
            }
        } else if (powerlinesAround == 2) {
            if (SE && NW) {
                return TileManager.getTile("powerline_nw_overlay");
            }
            if (NE && SW) {
                return TileManager.getTile("powerline_ne_overlay");
            }
        }
        return TileManager.getTile("powerline_nw_overlay");
    }

    @Override
    public boolean canIsoOverlayBePlaced(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().canHaveOverlay();
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().canHaveOverlay();
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().canHaveOverlay();
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().canHaveOverlay();
        if (NE || SW || SE || NW) {
            return StreetManager.getStreetsAround(tileX, tileY, isoMap) < 3;
        }
        return false;
    }
}
