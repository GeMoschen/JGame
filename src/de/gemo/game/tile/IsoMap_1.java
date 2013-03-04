package de.gemo.game.tile;

import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.SingleTexture;
import de.gemo.game.core.MyEngine;
import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.tile.manager.TileManager;
import de.gemo.game.tile.set.TileType;

import static org.lwjgl.opengl.GL11.*;

public class IsoMap_1 extends IsoMap {

    public static SingleTexture noPower;
    static {
        noPower = TextureManager.getTexture("icon_nopower").getTexture(0);
    }

    private static IsoTile whiteTile = TileManager.getTile("white");

    private int screenX, screenY, screenWidth, screenHeight;
    private final int maxOffY = 4, maxOffX = 4;

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
            int trX = this.getTileXBitmask((screenX + screenWidth) / MyEngine.SCALE, screenY);

            int maxRows = (int) ((screenHeight / MyEngine.SCALE) / this.tileHeight) + 2;
            int startX = tlX - 1;
            int startY = tlY;
            int endX = trX + 1;

            // render normal tiles
            UnicodeFont font = FontManager.getStandardFont();

            int renderX, renderY;

            for (int i = 0; i < maxRows; i++) {
                // UPPER ROW
                int thisY = startY;
                for (int x = startX - 1; x <= endX; x++) {
                    glPushMatrix();
                    {
                        if (x > -1 && x < this.width && thisY > -1 && thisY < this.height) {
                            renderX = x;
                            renderY = thisY;
                            int tX = this.getIsoX(renderX, renderY);
                            int tY = this.getIsoY(renderX, renderY);
                            glTranslatef(tX, tY, 0);
                            if (tileMap[renderX][renderY].isDrawBackground()) {
                                grassTile.render();
                            }
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX < x + maxOffX && MyGUIManager1.mouseTileY <= thisY && MyGUIManager1.mouseTileY >= thisY - maxOffY && tileMap[renderX][renderY].getType().getIndex() >= TileType.OVERLAY_START) {
                                tileMap[renderX][renderY].renderOutline(this.halfTileWidth, this.halfTileHeight);
                                tileMap[renderX][renderY].setAlpha(0.4f);
                                tileMap[renderX][renderY].render(0.5f, 0.5f, 0.5f);
                                tileMap[renderX][renderY].setAlpha(1f);
                                if (overlayMap[renderX][renderY] != null) {
                                    overlayMap[renderX][renderY].setAlpha(0.4f);
                                    overlayMap[renderX][renderY].render();
                                    overlayMap[renderX][renderY].setAlpha(1f);
                                }
                                if (tileMap[renderX][renderY].getType().needsPower() || (overlayMap[renderX][renderY] != null && overlayMap[renderX][renderY].getType().needsPower())) {
                                    if (!this.getUnsafeTileInformation(renderX, renderY).isPowered()) {
                                        float offX = this.halfTileWidth * (tileMap[renderX][renderY].getDimX() - 1) + 10;
                                        glTranslatef(offX, 0, 0);
                                        noPower.render(1, 1, 1, 1);
                                        glTranslatef(-offX, 0, 0);
                                    }
                                }
                            } else {
                                tileMap[renderX][renderY].render();
                                if (overlayMap[renderX][renderY] != null) {
                                    overlayMap[renderX][renderY].render();
                                }
                                if (tileMap[renderX][renderY].getType().needsPower() || (overlayMap[renderX][renderY] != null && overlayMap[renderX][renderY].getType().needsPower())) {
                                    if (!this.getUnsafeTileInformation(renderX, renderY).isPowered()) {
                                        float offX = this.halfTileWidth * (tileMap[renderX][renderY].getDimX() - 1) + 10;
                                        glTranslatef(offX, 0, 0);
                                        noPower.render(1, 1, 1, 1);
                                        glTranslatef(-offX, 0, 0);
                                    }
                                }
                            }
                            if (tileMap[renderX][renderY].getType().getIndex() == TileType.POWERPLANT_01.getIndex()) {
                                IsoMap.smokeEmitter.createParticles(1, tX + 50, tY - 115);
                                IsoMap.smokeEmitter.createParticles(1, tX + 74, tY - 105);
                                IsoMap.smokeEmitter.createParticles(1, tX + 98, tY - 93);
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
                    glPushMatrix();
                    {
                        if (x > -1 && x < this.width && thisY > -1 && thisY < this.height) {
                            renderX = x;
                            renderY = thisY;

                            int tX = this.getIsoX(renderX, renderY);
                            int tY = this.getIsoY(renderX, renderY);

                            glTranslatef(tX, tY, 0);
                            if (tileMap[renderX][renderY].isDrawBackground()) {
                                grassTile.render();
                            }
                            if (MyGUIManager1.mouseTileX >= x - maxOffX && MyGUIManager1.mouseTileX < x + maxOffX && MyGUIManager1.mouseTileY <= thisY && MyGUIManager1.mouseTileY >= thisY - maxOffY && tileMap[renderX][renderY].getType().getIndex() >= TileType.OVERLAY_START) {
                                tileMap[renderX][renderY].renderOutline(this.halfTileWidth, this.halfTileHeight);
                                tileMap[renderX][renderY].setAlpha(0.4f);
                                tileMap[renderX][renderY].render(0.5f, 0.5f, 0.5f);
                                tileMap[renderX][renderY].setAlpha(1f);
                                if (overlayMap[renderX][renderY] != null) {
                                    overlayMap[renderX][renderY].setAlpha(0.4f);
                                    overlayMap[renderX][renderY].render();
                                    overlayMap[renderX][renderY].setAlpha(1f);
                                }
                                if (tileMap[renderX][renderY].getType().needsPower() || (overlayMap[renderX][renderY] != null && overlayMap[renderX][renderY].getType().needsPower())) {
                                    if (!this.getUnsafeTileInformation(renderX, renderY).isPowered()) {
                                        float offX = this.halfTileWidth * (tileMap[renderX][renderY].getDimX() - 1) + 10;
                                        glTranslatef(offX, 0, 0);
                                        noPower.render(1, 1, 1, 1);
                                        glTranslatef(-offX, 0, 0);
                                    }
                                }
                            } else {
                                tileMap[renderX][renderY].render();
                                if (overlayMap[renderX][renderY] != null) {
                                    overlayMap[renderX][renderY].render();
                                }
                                if (tileMap[renderX][renderY].getType().needsPower() || (overlayMap[renderX][renderY] != null && overlayMap[renderX][renderY].getType().needsPower())) {
                                    if (!this.getUnsafeTileInformation(renderX, renderY).isPowered()) {
                                        float offX = this.halfTileWidth * (tileMap[renderX][renderY].getDimX() - 1) + 10;
                                        glTranslatef(offX, 0, 0);
                                        noPower.render(1, 1, 1, 1);
                                        glTranslatef(-offX, 0, 0);
                                    }
                                }
                            }

                            if (tileMap[renderX][renderY].getType().getIndex() == TileType.POWERPLANT_01.getIndex()) {
                                IsoMap.smokeEmitter.createParticles(1, tX + 50, tY - 115);
                                IsoMap.smokeEmitter.createParticles(1, tX + 74, tY - 105);
                                IsoMap.smokeEmitter.createParticles(1, tX + 98, tY - 93);
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
            IsoMap.smokeEmitter.render();
        }
        glPopMatrix();

        glEnable(GL_DEPTH_TEST);

        SHOW_JOBS = false;
        SHOW_POLLUTION = false;

        if (IsoMap.SHOW_POWER) {
            this.renderExtra(0);
        }

        if (IsoMap.SHOW_SECURITY) {
            this.renderExtra(1);
        }
        if (IsoMap.SHOW_POLLUTION) {
            this.renderExtra(2);
        }
        if (IsoMap.SHOW_JOBS) {
            this.renderExtra(3);
        }

        IsoMap.smokeEmitter.updateParticles();
    }

    public void renderExtra(int mode) {
        glDisable(GL_DEPTH_TEST);
        glPushMatrix();
        {
            glTranslatef(offsetX, offsetY + halfTileHeight, 0);

            int tlX = this.getTileXBitmask(screenX, screenY);
            int tlY = this.getTileYBitmask(screenX, screenY);
            int trX = this.getTileXBitmask((screenX + screenWidth) / MyEngine.SCALE, screenY);

            int maxRows = (int) ((screenHeight / MyEngine.SCALE) / this.tileHeight) + 2;
            int startX = tlX - 1;
            int startY = tlY;
            int endX = trX + 1;

            for (int i = 0; i < maxRows; i++) {
                // UPPER ROW
                renderIterationExtra(startX - 1, startY, endX, mode);

                // LOWER ROW
                endX++;
                renderIterationExtra(startX, startY, endX, mode);
                startX++;
                startY++;
            }
        }
        glPopMatrix();

        glEnable(GL_DEPTH_TEST);
    }

    private void renderIterationExtra(int startX, int startY, int endX, int mode) {
        // UPPER ROW
        int renderX, renderY;
        int thisY = startY;
        for (int x = startX; x <= endX; x++) {
            glPushMatrix();
            {
                if (x > -1 && x < this.width && thisY > -1 && thisY < this.height) {
                    renderX = x;
                    renderY = thisY;
                    int tX = this.getIsoX(renderX, renderY);
                    int tY = this.getIsoY(renderX, renderY);
                    glTranslatef(tX, tY, 0);
                    if (mode == 0) {
                        this.renderPowerLevel(tileMap[renderX][renderY], this.getUnsafeTileInformation(renderX, renderY));
                    } else if (mode == 1) {
                        this.renderSecurityLevel(tileMap[renderX][renderY], this.getUnsafeTileInformation(renderX, renderY));
                    } else if (mode == 2) {
                        this.renderPollutionLevel(tileMap[renderX][renderY], this.getUnsafeTileInformation(renderX, renderY));
                    } else if (mode == 3) {
                        this.renderJobLevel(tileMap[renderX][renderY], this.getUnsafeTileInformation(renderX, renderY));
                    }
                }
            }
            glPopMatrix();
            thisY--;
        }
    }

    private void renderSecurityLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (tileInfo.getSecureLevel() > 0) {
            whiteTile.setAlpha(tileInfo.getSecureLevelAlpha());
            whiteTile.render(0f, 1f, 1f);
            whiteTile.setAlpha(1f);
            UnicodeFont font = FontManager.getStandardFont();
            // font.drawString(-(font.getWidth("" + (int) tileInfo.getSecureLevel()) / 2), -8, "" + (int) tileInfo.getSecureLevel());
        }
    }

    private void renderPollutionLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (tileInfo.getPollutionLevel() > 0) {
            whiteTile.setAlpha(tileInfo.getPollutionLevelAlpha());
            whiteTile.render(0.2f - tileInfo.getPollutionLevelAlpha(), 0.2f - tileInfo.getPollutionLevelAlpha(), 0.2f - tileInfo.getPollutionLevelAlpha());
            whiteTile.setAlpha(1f);
            UnicodeFont font = FontManager.getStandardFont();
            // font.drawString(-(font.getWidth("" + (int) tileInfo.getPollutionLevel()) / 2), -8, "" + (int) tileInfo.getPollutionLevel());
        }
    }

    private void renderJobLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (tileInfo.getJobLevel() > 0) {
            whiteTile.setAlpha(tileInfo.getJobLevelAlpha());
            whiteTile.render(1f - tileInfo.getJobLevelAlpha() * 0.5f, 1f - tileInfo.getJobLevelAlpha() * 0.5f, 0f);
            whiteTile.setAlpha(1f);
            UnicodeFont font = FontManager.getStandardFont();
            // font.drawString(-(font.getWidth("" + (int) tileInfo.getJobLevel()) / 2), -8, "" + (int) tileInfo.getJobLevel());
        }
    }

    private void renderPowerLevel(IsoTile isoTile, TileInformation tileInfo) {
        if (tileInfo.isPowered()) {
            whiteTile.setAlpha(0.7f);
            whiteTile.render(1f, 0, 0);
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
    public IsoTile getNorthEastOverlay(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX, tileY - 1);
        return this.getOverlayNotNull(tileInfo.getFatherX(), tileInfo.getFatherY());
    }

    @Override
    public IsoTile getSouthEastOverlay(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX + 1, tileY);
        return this.getOverlayNotNull(tileInfo.getFatherX(), tileInfo.getFatherY());
    }
    @Override
    public IsoTile getSouthWestOverlay(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX, tileY + 1);
        return this.getOverlayNotNull(tileInfo.getFatherX(), tileInfo.getFatherY());
    }

    @Override
    public IsoTile getNorthWestOverlay(int tileX, int tileY) {
        TileInformation tileInfo = this.getTileInformation(tileX - 1, tileY);
        return this.getOverlayNotNull(tileInfo.getFatherX(), tileInfo.getFatherY());
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
