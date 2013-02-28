package de.gemo.game.tile;

public class PowerlineManager_1 extends PowerlineManager {

    public final int getAmountPowerlinesAround(int tileX, int tileY, IsoMap isoMap) {
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > 2 || isoMap.getNorthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > 2 || isoMap.getSouthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > 2 || isoMap.getSouthWestOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > 2 || isoMap.getNorthWestOverlay(tileX, tileY).getType().getIndex() > 2;

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
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > 2 || isoMap.getNorthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > 2 || isoMap.getSouthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > 2 || isoMap.getSouthWestOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > 2 || isoMap.getNorthWestOverlay(tileX, tileY).getType().getIndex() > 2;

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
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > 2;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > 2;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > 2;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > 2;

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
        boolean NE = isoMap.getNorthEast(tileX, tileY).getType().getIndex() > 2;
        boolean SE = isoMap.getSouthEast(tileX, tileY).getType().getIndex() > 2;
        boolean SW = isoMap.getSouthWest(tileX, tileY).getType().getIndex() > 2;
        boolean NW = isoMap.getNorthWest(tileX, tileY).getType().getIndex() > 2;

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
        boolean NE = isoMap.getNorthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SE = isoMap.getSouthEastOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean SW = isoMap.getSouthWestOverlay(tileX, tileY).getType().getIndex() > 2;
        boolean NW = isoMap.getNorthWestOverlay(tileX, tileY).getType().getIndex() > 2;
        if (!NE && !SW && !SE && !NW) {
            return true && StreetManager.getStreetsAround(tileX, tileY, isoMap) < 3;
        }
        return false;
    }
}
