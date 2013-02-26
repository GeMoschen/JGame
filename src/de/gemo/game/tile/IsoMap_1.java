package de.gemo.game.tile;

import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class IsoMap_1 extends IsoMap {

    private static IsoTile whiteTile = TileManager.getTile("white");

    private int screenX, screenY, screenWidth, screenHeight;
    private final int maxOffY = 6, maxOffX = 4;;

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

            // render normal tiles

            UnicodeFont font = FontManager.getStandardFont();

            TextureManager.getTexture("tilesheet_01").getTexture(0).startUse();
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
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX <= x && MyGUIManager1.mouseTileY >= thisY - maxOffY && MyGUIManager1.mouseTileY <= thisY && tileMap[x][thisY].getType().getIndex() >= TileType.OVERLAY_START) {
                                if (IsoMap.SHOW_SECURITY && this.getUnsafeTileInformation(x, thisY).getSecureLevel() > 0) {
                                    this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                }
                                if (IsoMap.SHOW_POWER) {
                                    this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                }
                                tileMap[x][thisY].renderOutline(this.halfTileWidth, this.halfTileHeight);
                                tileMap[x][thisY].setAlpha(0.2f);
                                tileMap[x][thisY].render(0.5f, 0.5f, 0.5f);
                                tileMap[x][thisY].setAlpha(1f);
                            } else {
                                if (IsoMap.SHOW_SECURITY) {
                                    if (tileMap[x][thisY].getType().getIndex() < TileType.OVERLAY_START) {
                                        tileMap[x][thisY].render();
                                        this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                    } else {
                                        this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                        tileMap[x][thisY].render();
                                    }
                                } else if (IsoMap.SHOW_POWER) {
                                    if (tileMap[x][thisY].getType().getIndex() < TileType.OVERLAY_START) {
                                        tileMap[x][thisY].render();
                                        this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                    } else {
                                        this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                        tileMap[x][thisY].render();
                                    }
                                } else {
                                    tileMap[x][thisY].render();
                                }
                            }
                            // font.drawString(-(font.getWidth(x + "/" + thisY) / 2), -8, x + "/" + thisY);
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
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX <= x && MyGUIManager1.mouseTileY >= thisY - maxOffY && MyGUIManager1.mouseTileY <= thisY && tileMap[x][thisY].getType().getIndex() >= TileType.OVERLAY_START) {
                                if (IsoMap.SHOW_SECURITY && this.getUnsafeTileInformation(x, thisY).getSecureLevel() > 0) {
                                    this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                }
                                if (IsoMap.SHOW_POWER) {
                                    this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                }
                                tileMap[x][thisY].renderOutline(this.halfTileWidth, this.halfTileHeight);
                                tileMap[x][thisY].setAlpha(0.2f);
                                tileMap[x][thisY].render(0.5f, 0.5f, 0.5f);
                                tileMap[x][thisY].setAlpha(1f);
                            } else {
                                if (IsoMap.SHOW_SECURITY) {
                                    if (tileMap[x][thisY].getType().getIndex() < TileType.OVERLAY_START) {
                                        tileMap[x][thisY].render();
                                        this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                    } else {
                                        this.renderSecurityLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                        tileMap[x][thisY].render();
                                    }
                                } else if (IsoMap.SHOW_POWER) {
                                    if (tileMap[x][thisY].getType().getIndex() < TileType.OVERLAY_START) {
                                        tileMap[x][thisY].render();
                                        this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                    } else {
                                        this.renderPowerLevel(tileMap[x][thisY], this.getUnsafeTileInformation(x, thisY));
                                        tileMap[x][thisY].render();
                                    }
                                } else {
                                    tileMap[x][thisY].render();
                                }
                            }
                            // font.drawString(-(font.getWidth(x + "/" + thisY) / 2), -8, x + "/" + thisY);
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

    private void renderSecurityLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (tileInfo.getSecureLevel() > 0) {
            whiteTile.setAlpha(tileInfo.getSecureLevelAlpha());
            whiteTile.render(0f, 1f, 1f);
            whiteTile.setAlpha(1f);
        }
    }

    private void renderPowerLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (PowerManager.isPowersource(tileInfo.getOriginalX(), tileInfo.getOriginalY())) {
            whiteTile.setAlpha(1f);
            whiteTile.render(1f, 0, 0);
            whiteTile.setAlpha(1f);
            UnicodeFont font = FontManager.getStandardFont();
            font.drawString(-(font.getWidth("1") / 2), -8, "2");
        } else if (tileInfo.isPowered()) {
            whiteTile.setAlpha(1f);
            whiteTile.render(1f, 0, 0);
            whiteTile.setAlpha(1f);
            UnicodeFont font = FontManager.getStandardFont();
            font.drawString(-(font.getWidth("1") / 2), -8, "1");
        }
    }

    @Override
    public int getTileX(float x, float y) {
        x = x - offsetX;
        y = y - offsetY;
        return (int) (0.5f * (y / halfTileHeight + x / halfTileWidth));
    }

    @Override
    public int getTileY(float x, float y) {
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
        TileInformation tileInfo = this.getTileInformation(tileX, tileY - 1);
        return this.getTile(tileInfo.getFatherX(), tileInfo.getFatherY());
    }

    @Override
    public IsoTile getSouthEast(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX + 1, tileY);
        return this.getTile(tileInfo.getFatherX(), tileInfo.getFatherY());
    }
    @Override
    public IsoTile getSouthWest(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX, tileY + 1);
        return this.getTile(tileInfo.getFatherX(), tileInfo.getFatherY());
    }

    @Override
    public IsoTile getNorthWest(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX - 1, tileY);
        return this.getTile(tileInfo.getFatherX(), tileInfo.getFatherY());
    }

    @Override
    public TileInformation getNorthEastInfo(int tileX, int tileY) {
        return this.getTileInformation(tileX, tileY - 1);
    }

    @Override
    public TileInformation getSouthEastInfo(int tileX, int tileY) {
        return this.getTileInformation(tileX + 1, tileY);
    }

    @Override
    public TileInformation getSouthWestInfo(int tileX, int tileY) {
        return this.getTileInformation(tileX, tileY + 1);
    }

    @Override
    public TileInformation getNorthWestInfo(int tileX, int tileY) {
        return this.getTileInformation(tileX - 1, tileY);
    }
}
