package de.gemo.game.tile;

import de.gemo.engine.manager.FontManager;
import de.gemo.game.manager.gui.MyGUIManager1;

import static org.lwjgl.opengl.GL11.*;

public class IsoMap_1 extends IsoMap {

    private int screenX, screenY, screenWidth, screenHeight;

    public IsoMap_1(int width, int height, int tileWidth, int tileHeight, int screenX, int screenY, int screenWidth, int screenHeight) {
        super(width, height, tileWidth, tileHeight);
        this.screenX = screenX;
        this.screenY = screenY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void render(int minX, int maxX, int minY, int maxY) {
        glDisable(GL_DEPTH_TEST);
        glPushMatrix();
        {
            glTranslatef(offsetX, offsetY + halfTileHeight, 0);
            IsoTile grassTile = TileManager.getTile("grass");

            int tlX = this.getTileXBitmask(screenX, screenY);
            int tlY = this.getTileYBitmask(screenX, screenY);
            int trX = this.getTileXBitmask(screenX + screenWidth, screenY);

            int maxRows = (screenHeight / this.tileHeight) + 2;
            int startX = tlX;
            int startY = tlY;
            int endX = trX;

            int dif = maxRows % 2;
            maxRows += dif;

            int maxOffY = 6;
            int maxOffX = 4;
            for (int i = 0; i < maxRows; i++) {
                // UPPER ROW
                int thisY = startY;
                for (int x = startX - 1; x <= endX; x++) {
                    int tX = this.getIsoX(x, thisY);
                    int tY = this.getIsoY(x, thisY);
                    glPushMatrix();
                    {
                        glTranslatef(tX, tY, 0);
                        if (x > -1 && x < this.width && thisY > -1 && thisY < this.height) {
                            if (tileMap[x][thisY].isDrawBackground()) {
                                grassTile.render();
                            }
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX <= x && MyGUIManager1.mouseTileY >= thisY - maxOffY && MyGUIManager1.mouseTileY <= thisY && tileMap[x][thisY].getType().ordinal() > 3) {
                                tileMap[x][thisY].setAlpha(0.15f);
                                tileMap[x][thisY].render();
                                tileMap[x][thisY].setAlpha(1f);
                            } else {
                                tileMap[x][thisY].render();
                            }
                            // FontManager.getStandardFont().drawString(-(FontManager.getStandardFont().getWidth(x + "/" + thisY) / 2), -8, x + "/" + thisY);
                        }
                    }
                    glPopMatrix();

                    thisY--;
                }

                // LOWER ROW
                thisY = startY;
                endX++;
                for (int x = startX; x <= endX; x++) {
                    int tX = this.getIsoX(x, thisY);
                    int tY = this.getIsoY(x, thisY);
                    glPushMatrix();
                    {
                        glTranslatef(tX, tY, 0);
                        if (x > -1 && x < this.width && thisY > -1 && thisY < this.height) {
                            if (tileMap[x][thisY].isDrawBackground()) {
                                grassTile.render();
                            }
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX <= x && MyGUIManager1.mouseTileY >= thisY - maxOffY && MyGUIManager1.mouseTileY <= thisY && tileMap[x][thisY].getType().ordinal() > 3) {
                                tileMap[x][thisY].setAlpha(0.15f);
                                tileMap[x][thisY].render();
                                tileMap[x][thisY].setAlpha(1f);
                            } else {
                                tileMap[x][thisY].render();
                            }
                            // FontManager.getStandardFont().drawString(-(FontManager.getStandardFont().getWidth(x + "/" + thisY) / 2), -8, x + "/" + thisY);
                        }
                    }
                    glPopMatrix();

                    thisY--;
                }

                startX++;
                startY++;
            }
        }
        glPopMatrix();
        glEnable(GL_DEPTH_TEST);

    }

    @Override
    public int getTileX(float x, float y, boolean bitMask) {
        x = x - offsetX;
        y = y - offsetY;
        return (int) (0.5f * (y / halfTileHeight + x / halfTileWidth));
    }

    @Override
    public int getTileY(float x, float y, boolean bitMask) {
        x = x - offsetX;
        y = y - offsetY;
        return (int) (0.5f * (y / halfTileHeight - x / halfTileWidth));
    }

    public int getTileXBitmask(float x, float y) {
        x = x - offsetX;
        y = y - offsetY;
        int tx = (int) (x) >> 5;
        int ty = (int) (y) >> 4;
        return (ty + tx) >> 1;
    }

    public int getTileYBitmask(float x, float y) {
        x = x - offsetX;
        y = y - offsetY;
        int tx = (int) (x) >> 5;
        int ty = (int) (y) >> 4;
        return (ty - tx) >> 1;
    }

    @Override
    public int getIsoX(int x, int y) {
        return (x * halfTileWidth) - (y * halfTileWidth);
    }

    @Override
    public int getIsoY(int x, int y) {
        return (x * halfTileHeight) + (y * halfTileHeight);
    }

    @Override
    public IsoTile getNorthEast(int tileX, int tileY) {
        return this.getTile(tileX, tileY - 1);
    }

    @Override
    public IsoTile getSouthEast(int tileX, int tileY) {
        return this.getTile(tileX + 1, tileY);
    }

    @Override
    public IsoTile getSouthWest(int tileX, int tileY) {
        return this.getTile(tileX, tileY + 1);
    }

    @Override
    public IsoTile getNorthWest(int tileX, int tileY) {
        return this.getTile(tileX - 1, tileY);
    }
}
