package de.gemo.game.core;

import java.awt.Font;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.GradientEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import static org.lwjgl.opengl.GL11.*;

import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.manager.TextureManager;
import de.gemo.game.manager.gui.MyGUIManager1;
import de.gemo.game.manager.gui.MyGUIManager2;

public class MyEngine extends Engine {

    public MyEngine() {
        super("My Enginetest", 1280, 1024, false);
    }

    @Override
    protected void loadFonts() {
        FontManager.loadFont(FontManager.ANALOG, Font.PLAIN, 20, new OutlineEffect(2, java.awt.Color.black), new ShadowEffect(java.awt.Color.black, 2, 2, 0.5f), new GradientEffect(new java.awt.Color(255, 255, 255), new java.awt.Color(150, 150, 150), 1f));
        FontManager.loadFont(FontManager.ANALOG, Font.PLAIN, 24);
    }

    private final void drawLoadingText(String topic, String text) {
        glPushMatrix();
        glClearColor(0, 0, 0, 0);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        UnicodeFont font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 26);
        int x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(topic) / 2f);
        int y = (int) (this.VIEW_HEIGHT / 2f - font.getHeight(topic) / 2f);
        font.drawString(x, y, topic, Color.red);

        x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(text) / 2f);
        font.drawString(x, y + font.getHeight(topic) + 10, text, Color.gray);
        Display.update();
        glPopMatrix();
    }

    @Override
    protected void loadTextures() {
        try {
            // LOAD GUI TEXTURE
            SingleTexture guiTexture = TextureManager.loadSingleTexture("GUI_INGAME.png");
            TextureManager.addTexture("GUI_1", TextureManager.SingleToMultiTexture(guiTexture.crop(0, 0, 1280, 1024)));

            // LOAD COUNTDOWN TEXTURE
            MultiTexture countdownMultiTexture = new MultiTexture(72, 104);
            int y = 0;
            int x = 0;
            for (int i = 9; i >= 0; i--) {
                if (i == 4) {
                    y += countdownMultiTexture.getHeight();
                    x = 0;
                }
                countdownMultiTexture.addTextures(guiTexture.crop(1280 + x, y, countdownMultiTexture.getWidth(), countdownMultiTexture.getHeight()));
                x += countdownMultiTexture.getWidth();
            }
            TextureManager.addTexture("countdown", countdownMultiTexture);

            // LOAD TEXTURES FOR BUTTON
            drawLoadingText("Loading Textures...", "test.jpg");
            SingleTexture buttonCompleteTexture = TextureManager.loadSingleTexture("test.jpg");
            SingleTexture buttonNormalTexture = buttonCompleteTexture.crop(0, 0, 175, 34);
            SingleTexture buttonHoverTexture = buttonCompleteTexture.crop(0, 0, 175, 34);
            SingleTexture buttonPressedTexture = buttonCompleteTexture.crop(0, 2 * 34, 175, 34);
            MultiTexture buttonMultiTexture = new MultiTexture(buttonNormalTexture.getWidth(), buttonNormalTexture.getHeight(), buttonNormalTexture, buttonHoverTexture, buttonPressedTexture);
            TextureManager.addTexture("BTN_1", buttonMultiTexture);

            // LOAD TEXTFIELD TEXTURE
            drawLoadingText("Loading Textures...", "edit_normal.jpg");
            SingleTexture editTexture = TextureManager.loadSingleTexture("edit_normal.jpg", 0, 0, 175, 34);
            TextureManager.addTexture("EDIT_1", TextureManager.SingleToMultiTexture(editTexture));

            // LOAD CHECKBOX TEXTURE
            drawLoadingText("Loading Textures...", "gui_checkboxradio.png");
            SingleTexture checkBoxRadioTexture = TextureManager.loadSingleTexture("gui_checkboxradio.png");
            SingleTexture checkBoxTextureOff = checkBoxRadioTexture.crop(0, 0, 21, 21);
            SingleTexture checkBoxTextureOn = checkBoxRadioTexture.crop(21, 0, 21, 21);
            MultiTexture checkBoxMultiTexture = new MultiTexture(21, 21, checkBoxTextureOff, checkBoxTextureOn);
            TextureManager.addTexture("CB_1", checkBoxMultiTexture);

            // LOAD RADIOBUTTON TEXTURE
            SingleTexture radioButtonTextureOff = checkBoxRadioTexture.crop(0, 21, 20, 20);
            SingleTexture radioButtonTextureOn = checkBoxRadioTexture.crop(20, 21, 20, 20);
            MultiTexture radioButtonMultiTexture = new MultiTexture(21, 21, radioButtonTextureOff, radioButtonTextureOn);
            TextureManager.addTexture("RADIO_1", radioButtonMultiTexture);
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
        Hitbox hitbox = new Hitbox(550, 535);
        hitbox.addPoint(-530, -470);
        hitbox.addPoint(530, -470);
        hitbox.addPoint(530, 470);
        hitbox.addPoint(-530, 470);
        MyGUIManager2 manager = new MyGUIManager2("GUI2", hitbox, MouseManager.INSTANCE.getMouseVector(), -1);
        this.registerGUIManager(manager);

        hitbox = new Hitbox(1185, 512);
        hitbox.addPoint(-95, -447);
        hitbox.addPoint(95, -447);
        hitbox.addPoint(95, 512);
        hitbox.addPoint(-95, 512);
        this.registerGUIManager(new MyGUIManager1("GUI", hitbox, MouseManager.INSTANCE.getMouseVector(), 0));

        this.initGUIManager(this.getGUIManager("GUI2"));
        this.initGUIManager(this.getGUIManager("GUI"));
    }
}
