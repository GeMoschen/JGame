package de.gemo.game.core;

import java.awt.Font;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.GradientEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.MultiTexture;
import de.gemo.engine.textures.SingleTexture;
import de.gemo.game.manager.gui.MyGUIManager1;

import static org.lwjgl.opengl.GL11.*;

public class MyEngine extends Engine {

    public MyEngine() {
        super("My Enginetest", 800, 600, false);
    }

    @Override
    protected void loadFonts() {
        drawLoadingText("Loading fonts...", "ANALOG, PLAIN, 20", 0);
        FontManager.loadFontFromJar("fonts\\analog.ttf", FontManager.ANALOG, Font.PLAIN, 20, new OutlineEffect(2, java.awt.Color.black), new ShadowEffect(java.awt.Color.black, 2, 2, 0.5f), new GradientEffect(new java.awt.Color(255, 255, 255), new java.awt.Color(150, 150, 150), 1f));
        drawLoadingText("Loading fonts...", "ANALOG, PLAIN, 24", 30);
        FontManager.loadFontFromJar("fonts\\analog.ttf", FontManager.ANALOG, Font.PLAIN, 24);
    }

    private final void drawLoadingText(String topic, String text, int percent) {
        glPushMatrix();
        glClearColor(0, 0, 0, 0);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glDisable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        UnicodeFont font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 26);

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
        glPopMatrix();
    }

    @Override
    protected void loadTextures() {
        try {
            // LOAD GUI TEXTURE
            drawLoadingText("Loading Textures...", "game_main_right.png", 20);
            SingleTexture guiTexture = TextureManager.loadSingleTexture("textures\\ui\\game_main_right.png");
            TextureManager.addTexture("GUI", guiTexture.toMultiTexture());

            // LOAD TILES
            this.loadTiles();

            // LOAD TEXTURES FOR BUTTON 1
            drawLoadingText("Loading Textures...", "btn_main.png", 60);
            SingleTexture buttonCompleteTexture = TextureManager.loadSingleTexture("textures\\ui\\btn_main.png");
            SingleTexture buttonNormalTexture = buttonCompleteTexture.crop(0, 0, 66, 66);
            SingleTexture buttonHoverTexture = buttonCompleteTexture.crop(0, 2 * 66, 66, 66);
            SingleTexture buttonPressedTexture = buttonCompleteTexture.crop(0, 2 * 66, 66, 66);
            MultiTexture buttonMultiTexture = new MultiTexture(66, 66, buttonNormalTexture, buttonHoverTexture, buttonPressedTexture);
            TextureManager.addTexture("BTN_GAME_MAIN", buttonMultiTexture);

            // LOAD TEXTURES FOR BUTTON 2
            drawLoadingText("Loading Textures...", "btn_main_icons.png", 60);
            SingleTexture buttonIconsCompleteTexture = TextureManager.loadSingleTexture("textures\\ui\\btn_main_icons.png");
            SingleTexture iconPowerPlant = buttonIconsCompleteTexture.crop(0, 0, 64, 64);
            SingleTexture iconPoliceStation = buttonIconsCompleteTexture.crop(0, 1 * 64, 64, 64);
            SingleTexture iconHouse = buttonIconsCompleteTexture.crop(0, 2 * 64, 64, 64);
            SingleTexture iconStreets = buttonIconsCompleteTexture.crop(0, 3 * 64, 64, 64);
            SingleTexture iconBulldozer = buttonIconsCompleteTexture.crop(0, 4 * 64, 64, 64);
            MultiTexture buttonIconsMultiTexture = new MultiTexture(64, 64, iconPowerPlant, iconPoliceStation, iconHouse, iconStreets, iconBulldozer);
            TextureManager.addTexture("BTN_GAME_MAIN_ICONS", buttonIconsMultiTexture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTiles() {
        try {
            SingleTexture tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_grass.png");
            TextureManager.addTexture("tile_grass", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_white.png");
            TextureManager.addTexture("tile_white", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_mouse.png");
            TextureManager.addTexture("tile_mouse", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_path.png");
            TextureManager.addTexture("tile_path", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_house_small_01.png");
            TextureManager.addTexture("tile_house_small_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_police_01.png");
            TextureManager.addTexture("tile_police_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\powerplant_01.png");
            TextureManager.addTexture("powerplant_01", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\bulldozer.png");
            TextureManager.addTexture("bulldozer", tileTexture.toMultiTexture());

            tileTexture = TextureManager.loadSingleTexture("textures\\tiles\\tile_streets.png");
            MultiTexture streetTextures = new MultiTexture(64, 32);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 4; x++) {
                    streetTextures.addTextures(tileTexture.crop(x * 64, y * 32, 64, 32));
                }
            }
            TextureManager.addTexture("tile_street", streetTextures);
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
        Hitbox hitbox = new Hitbox(400, 300);
        hitbox.addPoint(-400, -300);
        hitbox.addPoint(400, -300);
        hitbox.addPoint(400, 300);
        hitbox.addPoint(-400, 300);
        this.registerGUIManager(new MyGUIManager1("GUI", hitbox, MouseManager.INSTANCE.getMouseVector(), 0));
        this.initGUIManager(this.getGUIManager("GUI"));
    }

    @Override
    protected void renderGame() {
    }
}
