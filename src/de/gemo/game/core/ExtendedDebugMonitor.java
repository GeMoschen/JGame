package de.gemo.game.core;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.core.Engine;
import de.gemo.engine.core.debug.StandardDebugMonitor;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.GUIManager;

import static org.lwjgl.opengl.GL11.*;

public class ExtendedDebugMonitor extends StandardDebugMonitor {

    @Override
    public void render() {
        glPushMatrix();
        {
            int fontX = 125;
            int fontY = 5;
            glTranslatef(20, 20, 0);

            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            GUIManager manager = Engine.INSTANCE.getActiveGUIManager();
            int height = 200;
            if (manager != null) {
                if (manager.hasFocusedElement()) {
                    height += 15;
                }
                if (manager.hasHoveredElement()) {
                    height += 15;
                }
            }
            if (!this.isShowExtended()) {
                height = 90;
            }
            // DRAW BACKGROUND
            Color.black.bind();
            glBegin(GL_QUADS);
            glVertex3f(0, 0, 0);
            glVertex3f(250, 0, 0);
            glVertex3f(250, height, 0);
            glVertex3f(0, height, 0);
            glEnd();

            // DRAW OUTLINE
            Color.white.bind();
            glLineWidth(1.5f);
            glBegin(GL_LINE_LOOP);
            glVertex3f(0, 0, 0);
            glVertex3f(250, 0, 0);
            glVertex3f(250, height, 0);
            glVertex3f(0, height, 0);
            glEnd();

            // DRAW STIPPLED LINE
            Color.white.bind();
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);
            glEnable(GL_LINE_STIPPLE);
            glLineStipple(3, (short) 0x0101);
            glBegin(GL_LINE_LOOP);
            glVertex3f(20, fontY + 20, 0);
            glVertex3f(230, fontY + 20, 0);
            glEnd();
            glDisable(GL_LINE_STIPPLE);

            glEnable(GL_BLEND);

            UnicodeFont font = FontManager.getStandardFont(Font.BOLD);

            font.drawString((int) (fontX - font.getWidth("DebugMonitor") / 2f), fontY, "DebugMonitor", Color.red);

            fontX = 20;
            fontY = 25;
            font = FontManager.getStandardFont();
            font.drawString(fontX, fontY + 10, "FPS: " + this.getFPS() + (this.isUseVSync() ? " (vsync)" : ""), Color.red);
            font.drawString(fontX, fontY + 25, "Delta: " + this.getDelta(), Color.red);

            String text = "NONE";
            if (this.getActiveGUIManager() != null) {
                text = this.getActiveGUIManager().getName();
            }
            font.drawString(fontX, fontY + 40, "Active UI: " + text, Color.red);

            if (this.isShowExtended()) {
                // DRAW STIPPLED LINE
                Color.white.bind();
                glDisable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glLineWidth(1f);
                glEnable(GL_LINE_STIPPLE);
                glLineStipple(3, (short) 0x0101);
                glBegin(GL_LINE_LOOP);
                glVertex3f(20, fontY + 65, 0);
                glVertex3f(230, fontY + 65, 0);
                glEnd();
                glDisable(GL_LINE_STIPPLE);

                glEnable(GL_BLEND);

                font.drawString(fontX, fontY + 75, "F1: toggle vysnc", Color.orange);
                font.drawString(fontX, fontY + 90, "F2: toggle DebugMonitor", Color.orange);
                font.drawString(fontX, fontY + 105, "F3: toggle extended information", Color.orange);
                font.drawString(fontX, fontY + 120, "F5 / F6: change volume ( " + Engine.INSTANCE.getSoundManager().getVolume() + " )", Color.orange);
                font.drawString(fontX, fontY + 135, "F11: toggle graphics", Color.orange);
                font.drawString(fontX, fontY + 150, "F12: toggle hitboxes", Color.orange);

                if (manager != null) {
                    if (manager.hasFocusedElement()) {
                        font.drawString(fontX, fontY + 165, "Focus: " + manager.getFocusedElement().getEntityID(), Color.magenta);
                    }
                    if (manager.hasHoveredElement()) {
                        if (manager.hasFocusedElement()) {
                            font.drawString(fontX, fontY + 180, "Hover: " + manager.getHoveredElement().getEntityID(), Color.magenta);
                        } else {
                            font.drawString(fontX, fontY + 165, "Hover: " + manager.getHoveredElement().getEntityID(), Color.magenta);
                        }
                    }
                }
            }

            glDisable(GL_BLEND);
        }
        glPopMatrix();
    }
}
