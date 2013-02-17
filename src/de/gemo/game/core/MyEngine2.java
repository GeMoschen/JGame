package de.gemo.game.core;

import java.awt.Font;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.core.Engine;
import de.gemo.engine.core.Renderer;
import de.gemo.engine.entity.Entity2D;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.TextureManager;

import static org.lwjgl.opengl.GL11.*;

public class MyEngine2 extends Engine {

    public MyEngine2() {
        super("My Enginetest 2", 1024, 768, false);
    }

    private final void drawLoadingText(String topic, String subText, int percent) {
        glPushMatrix();
        glClearColor(0, 0, 0, 0);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        UnicodeFont font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 26);

        // draw toptex
        int x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(topic) / 2f);
        int y = (int) (this.VIEW_HEIGHT / 2f - font.getHeight(topic) / 2f);
        font.drawString(x, y, topic, Color.red);
        y += font.getHeight(topic) + 10;

        // draw subtext
        font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 20);
        x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(subText) / 2f);
        font.drawString(x, y, subText, Color.gray);

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

        // draw line
        Display.update();
        glPopMatrix();
    }

    @Override
    protected void loadTextures() {
        try {
            // LOAD GUI TEXTURE
            drawLoadingText("Loading Textures...", "GUI_INGAME.png", 40);
            SingleTexture guiTexture = TextureManager.loadSingleTexture("GUI_INGAME.png");

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void createManager() {
        this.setDebugMonitor(new ExtendedDebugMonitor());
    }

    Element element;
    @Override
    protected final void createGUI() {
        element = new Element(200, 200, 0, new Animation(TextureManager.getTexture("countdown")), new Animation(TextureManager.getTexture("countdown")));
    }

    @Override
    protected void renderGame() {
        Renderer.render(element);
    }

    private class Element extends Entity2D {
        private Animation ani_2;

        public Element(int x, int y, int z, Animation ani_1, Animation ani_2) {
            super(x, y, ani_1);
            this.setZ(z);
            this.animation.goToFrame(4);
            this.ani_2 = ani_2;
            this.ani_2.goToFrame(6);
        }

        public void render() {
            super.render();
            glPushMatrix();
            {
                glTranslatef(0, 40, 1);
                glPushMatrix();
                {
                    glTranslatef(0, 0, -2);
                    ani_2.render(0, 0, 0, 0.95f);
                    glTranslatef(-30, -100, +2);
                    glRotatef(45f, 0, 0, 1);
                    FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 24).drawString(0, 0, "abcdefghijklmnopqrst", Color.red);
                }
                glPopMatrix();
            }
            glPopMatrix();
        }
    }
}
