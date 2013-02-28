package de.gemo.game.tile;

import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class TileDimension {

    private static IsoTile selectedTile = TileManager.getTile("unknown");
    private static int sizeX = 1, sizeY = 1;
    private static boolean isFree = false;
    private static IsoMap isoMap = null;

    public static void setIsoMap(IsoMap isoMap) {
        TileDimension.isoMap = isoMap;
    }

    public static boolean isFree() {
        return isFree;
    }

    public static void setSize(int sizeX, int sizeY) {
        TileDimension.sizeX = sizeX;
        TileDimension.sizeY = sizeY;
        isFree(MyGUIManager1.mouseTileX, MyGUIManager1.mouseTileY, isoMap);
    }

    public static void place(int tileX, int tileY, IsoMap isoMap) {
        if (selectedTile != null && isFree(tileX, tileY, isoMap)) {
            selectedTile.onPlace(tileX, tileY, isoMap);
        }
        isFree = false;
    }

    public static void setSelectedTile(IsoTile selectedTile) {
        if (selectedTile == null) {
            selectedTile = TileManager.getTile("unknown");
        }
        TileDimension.selectedTile = selectedTile;
        isFree(MyGUIManager1.mouseTileX, MyGUIManager1.mouseTileY, isoMap);
    }

    public static IsoTile getSelectedTile() {
        return selectedTile;
    }

    public static void render(int tileX, int tileY, IsoMap isoMap) {
        IsoTile mouseTile = TileManager.getTile("mouse");

        int offsetY = 0;
        if (!isFree) {
            mouseTile = TileManager.getTile("unknown");
            offsetY = isoMap.getHalfTileHeight();
        }

        glPushMatrix();
        {
            glTranslatef(0, offsetY, 0);
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    int tX = isoMap.getIsoX(x, -y);
                    int tY = isoMap.getIsoY(x, -y);
                    glPushMatrix();
                    {
                        glTranslatef(tX, tY, 0);
                        mouseTile.render();
                    }
                    glPopMatrix();
                }
            }

            if (isFree && !selectedTile.getType().equals(TileType.UNKNOWN)) {
                glTranslatef(0, isoMap.getHalfTileHeight(), 0);
                selectedTile.renderBuildPlace(tileX, tileY, isoMap);
                glTranslatef(0, -isoMap.getHalfTileHeight(), 0);
            }
        }
        glPopMatrix();
    }

    public static int getSizeX() {
        return sizeX;
    }

    public static int getSizeY() {
        return sizeY;
    }

    public static boolean isFree(int tileX, int tileY, IsoMap isoMap) {
        isFree = selectedTile.canBePlacedAt(tileX, tileY, isoMap);
        return isFree;
    }
}
