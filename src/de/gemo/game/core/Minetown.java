package de.gemo.game.core;

import java.awt.Font;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.MultiTexture;
import de.gemo.engine.textures.SingleTexture;
import de.gemo.game.gamestates.GameState;
import de.gemo.game.manager.gui.GamePausedMenu;
import de.gemo.game.manager.gui.MaingameGUIManager;
import de.gemo.game.manager.gui.Mainmenu;
import de.gemo.game.tile.IsoMap;
import de.gemo.game.tile.IsoMap_1;
import de.gemo.game.tile.manager.HouseManager;

import static org.lwjgl.opengl.GL11.*;

public class Minetown extends Engine {

    private GameState gameState = GameState.STARTUP;
    private IsoMap isoMap = null;
    private int tickCounter = 0;

    public static float SCALE = 1f;

    public Minetown() {
        super("CityBuilder", 1280, 1024, false);
    }

    @Override
    protected void loadFonts() {
        drawLoadingText("Loading fonts...", "ANALOG, PLAIN, 20", 0);
        FontManager.loadFontFromJar("fonts\\analog.ttf", FontManager.ANALOG, Font.PLAIN, 20);
        drawLoadingText("Loading fonts...", "ANALOG, PLAIN, 24", 10);
        FontManager.loadFontFromJar("fonts\\analog.ttf", FontManager.ANALOG, Font.PLAIN, 26);
    }

    private final void drawLoadingText(String topic, String text, int percent) {
        glPushMatrix();
        {
            glClearColor(0, 0, 0, 0);
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            glDisable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            TrueTypeFont font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 26);

            // draw toptex
            int x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(topic) / 2f);
            int y = (int) (this.VIEW_HEIGHT / 2f - font.getHeight(topic) / 2f);
            font.drawString(x, y, topic, Color.red);
            y += font.getHeight(topic) + 10;

            // draw subtext
            font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 20);
            x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(text) / 2f);
            font.drawString(x, y, text, Color.gray);

            int width = 200;
            int height = 40;
            x = (int) (this.VIEW_WIDTH / 2f - width / 2f);
            y = this.VIEW_HEIGHT - 100;
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            float succeeded = percent * 2;

            // DRAW SUCCEEDED PERCENT
            glBegin(GL_QUADS);
            Color.red.bind();
            glVertex3f(x, y, 0);
            Color.green.bind();
            glVertex3f(x + succeeded, y, 0);
            Color.green.bind();
            glVertex3f(x + succeeded, y + height, 0);
            Color.red.bind();
            glVertex3f(x, y + height, 0);
            glEnd();

            // DRAW OUTLINE
            glLineWidth(3f);
            glBegin(GL_LINE_LOOP);
            Color.white.bind();
            glVertex3f(x, y, 0);
            glVertex3f(x + width, y, 0);
            glVertex3f(x + width, y + height, 0);
            glVertex3f(x, y + height, 0);
            glEnd();

            glEnable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);

            // draw line
            Display.update();
        }
        glPopMatrix();
    }

    @Override
    protected void loadTextures() {
        try {
            // LOAD MAINMENU TEXTURE
            drawLoadingText("Loading Textures...", "mainmenu.png", 20);
            SingleTexture mainMenuTexture = TextureManager.loadSingleTexture("textures\\ui\\mainmenu.png");
            TextureManager.addTexture("MAINMENU_BG", mainMenuTexture.toMultiTexture());

            // LOAD TEXTURES FOR MAINMENU-BUTTON
            drawLoadingText("Loading Textures...", "btn_mainmenu.png", 30);
            SingleTexture buttonCompleteTexture = TextureManager.loadSingleTexture("textures\\ui\\btn_mainmenu.png");
            SingleTexture buttonNormalTexture = buttonCompleteTexture.crop(0, 0 * 54, 276, 54);
            SingleTexture buttonHoverTexture = buttonCompleteTexture.crop(0, 1 * 54, 276, 54);
            SingleTexture buttonPressedTexture = buttonCompleteTexture.crop(0, 2 * 54, 276, 54);
            MultiTexture buttonMultiTexture = new MultiTexture(276, 54, buttonNormalTexture, buttonHoverTexture, buttonPressedTexture);
            TextureManager.addTexture("MAINMENU_BTN", buttonMultiTexture);

            // LOAD GUI TEXTURE
            drawLoadingText("Loading Textures...", "game_main_right.png", 40);
            SingleTexture guiTexture = TextureManager.loadSingleTexture("textures\\ui\\game_main_right.png");
            TextureManager.addTexture("GUI", guiTexture.toMultiTexture());

            // LOAD TILES
            this.loadTiles();

            // LOAD TEXTURES FOR BUTTON 1
            drawLoadingText("Loading Textures...", "btn_main.png", 50);
            buttonCompleteTexture = TextureManager.loadSingleTexture("textures\\ui\\btn_main.png");
            buttonNormalTexture = buttonCompleteTexture.crop(0, 0, 66, 66);
            buttonHoverTexture = buttonCompleteTexture.crop(0, 2 * 66, 66, 66);
            buttonPressedTexture = buttonCompleteTexture.crop(0, 2 * 66, 66, 66);
            buttonMultiTexture = new MultiTexture(66, 66, buttonNormalTexture, buttonHoverTexture, buttonPressedTexture);
            TextureManager.addTexture("BTN_GAME_MAIN", buttonMultiTexture);

            // LOAD TEXTURES FOR ICONS
            drawLoadingText("Loading Textures...", "btn_main_icons.png", 60);
            SingleTexture buttonIconsCompleteTexture = TextureManager.loadSingleTexture("textures\\ui\\btn_main_icons.png");
            SingleTexture iconPowerPlant = buttonIconsCompleteTexture.crop(0, 0, 64, 64);
            SingleTexture iconPoliceStation = buttonIconsCompleteTexture.crop(0, 1 * 64, 64, 64);
            SingleTexture iconHouse = buttonIconsCompleteTexture.crop(0, 2 * 64, 64, 64);
            SingleTexture iconStreets = buttonIconsCompleteTexture.crop(0, 3 * 64, 64, 64);
            SingleTexture iconBulldozer = buttonIconsCompleteTexture.crop(0, 4 * 64, 64, 64);
            SingleTexture iconPowerline = buttonIconsCompleteTexture.crop(0, 5 * 64, 64, 64);
            MultiTexture buttonIconsMultiTexture = new MultiTexture(64, 64, iconPowerPlant, iconPoliceStation, iconHouse, iconStreets, iconBulldozer, iconPowerline);
            TextureManager.addTexture("BTN_GAME_MAIN_ICONS", buttonIconsMultiTexture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTiles() {
        try {
            SingleTexture powerTexture = TextureManager.loadSingleTexture("textures\\ui\\icon_nopower.png");
            TextureManager.addTexture("icon_nopower", powerTexture.toMultiTexture());

            // /////////// BEGIN TILESHEET

            SingleTexture tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tilesheet_01.png");
            TextureManager.addTexture("tilesheet_01", tileTexture.toMultiTexture());

            TextureManager.addTexture("tile_mouse", tileTexture.crop(0 * 64, 0 * 32, 64, 32).toMultiTexture());
            TextureManager.addTexture("tile_path", tileTexture.crop(1 * 64, 0 * 32, 64, 32).toMultiTexture());
            TextureManager.addTexture("tile_white", tileTexture.crop(2 * 64, 0 * 32, 64, 32).toMultiTexture());
            TextureManager.addTexture("tile_grass", tileTexture.crop(3 * 64, 0 * 32, 64, 32).toMultiTexture());
            TextureManager.addTexture("tile_water", tileTexture.crop(7 * 64, 1 * 32, 64, 32).toMultiTexture());

            // streets
            MultiTexture streetTextures = new MultiTexture(64, 32);
            streetTextures.addTextures(tileTexture.crop(4 * 64, 0 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(5 * 64, 0 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(6 * 64, 0 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(7 * 64, 0 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(0 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(1 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(2 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(3 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(4 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(5 * 64, 1 * 32, 64, 32));
            streetTextures.addTextures(tileTexture.crop(6 * 64, 1 * 32, 64, 32));
            TextureManager.addTexture("tile_street", streetTextures);

            // power
            MultiTexture powerlineTextures = new MultiTexture(64, 64);
            powerlineTextures.addTextures(tileTexture.crop(0 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(1 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(2 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(3 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(4 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(5 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(6 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(7 * 64, 2 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(0 * 64, 4 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(1 * 64, 4 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(2 * 64, 4 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(3 * 64, 4 * 32, 64, 64));
            powerlineTextures.addTextures(tileTexture.crop(4 * 64, 4 * 32, 64, 64));
            TextureManager.addTexture("tile_powerline", powerlineTextures);

            // watertubes
            MultiTexture watertubeTextures = new MultiTexture(64, 64);
            watertubeTextures.addTextures(tileTexture.crop(5 * 64, 4 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(6 * 64, 4 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(7 * 64, 4 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(5 * 64, 5 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(6 * 64, 5 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(7 * 64, 5 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(0 * 64, 6 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(1 * 64, 6 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(2 * 64, 6 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(3 * 64, 6 * 32, 64, 32));
            watertubeTextures.addTextures(tileTexture.crop(4 * 64, 6 * 32, 64, 32));
            TextureManager.addTexture("tile_watertube", watertubeTextures);

            // /////////// END TILESHEET

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_house_small_01.png");
            TextureManager.addTexture("tile_house_small_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_house_mid_01.png");
            TextureManager.addTexture("tile_house_mid_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_police_01.png");
            TextureManager.addTexture("tile_police_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\powerplant_01.png");
            TextureManager.addTexture("powerplant_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\bulldozer.png");
            TextureManager.addTexture("bulldozer", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_tree_01.png");
            TextureManager.addTexture("tile_tree_01", tileTexture.toMultiTexture());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createManager() {
        this.setDebugMonitor(new ExtendedDebugMonitor());
    }

    @Override
    protected final void createGUI() {
        this.setState(GameState.MAIN_MENU);
    }

    public static void setGameState(GameState gameState) {
        Minetown instance = (Minetown) Engine.INSTANCE;
        instance.setState(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setState(GameState gameState) {
        if (gameState.equals(this.gameState)) {
            return;
        }

        this.gameState = gameState;
        if (gameState.equals(GameState.MAIN_MENU)) {
            this.unregisterAllGUIManagers();
            final int width = getWindowWidth() / 2;
            final int height = getWindowHeight() / 2;
            Hitbox hitbox = new Hitbox(width, height);
            hitbox.addPoint(-width, -height);
            hitbox.addPoint(width, -height);
            hitbox.addPoint(width, height);
            hitbox.addPoint(-width, height);
            this.registerGUIManager(new Mainmenu("MAINMENU", hitbox, MouseManager.INSTANCE.getMouseVector(), 0));
            this.initGUIManager(this.getGUIManager("MAINMENU"));
            this.isoMap = null;
        } else if (gameState.equals(GameState.GAME)) {
            this.unregisterGUIManager(this.getGUIManager("GAME_PAUSED"));
            if (this.isoMap == null) {
                this.unregisterAllGUIManagers();
                final int width = getWindowWidth() / 2;
                final int height = getWindowHeight() / 2;
                this.isoMap = new IsoMap_1(100, 100, 64, 32, 0, 0, getWindowWidth() + 200, getWindowHeight() + 200);
                Hitbox hitbox = new Hitbox(width, height);
                hitbox.addPoint(-width, -height);
                hitbox.addPoint(width, -height);
                hitbox.addPoint(width, height);
                hitbox.addPoint(-width, height);
                this.registerGUIManager(new MaingameGUIManager("GUI", hitbox, MouseManager.INSTANCE.getMouseVector(), 0, this.isoMap));
                this.initGUIManager(this.getGUIManager("GUI"));
            }
        } else if (gameState.equals(GameState.GAME_PAUSED)) {
            final int width = getWindowWidth() / 2;
            final int height = getWindowHeight() / 2;
            Hitbox hitbox = new Hitbox(width, height);
            hitbox.addPoint(-width, -height);
            hitbox.addPoint(width, -height);
            hitbox.addPoint(width, height);
            hitbox.addPoint(-width, height);
            this.registerGUIManager(new GamePausedMenu("GAME_PAUSED", hitbox, MouseManager.INSTANCE.getMouseVector(), -1));
            this.initGUIManager(this.getGUIManager("GAME_PAUSED"));
        }
    }

    @Override
    protected void renderGame2D() {
        switch (this.gameState) {
            case GAME : {
                glPushMatrix();
                glScalef(SCALE, SCALE, 1);
                this.isoMap.render();
                glPopMatrix();
                break;
            }
            case GAME_PAUSED : {
                glPushMatrix();
                glScalef(SCALE, SCALE, 1);
                this.isoMap.render();
                glPopMatrix();
                break;
            }
            default :
                break;
        }
    }

    @Override
    protected void tickGame() {
        switch (this.gameState) {
            case GAME : {
                tickCounter++;
                if (tickCounter % 20 == 0) {
                    HouseManager.doRandomTicks(this.isoMap, 10);
                    tickCounter = 0;
                }
                break;
            }
            default :
                break;
        }

    }
}
