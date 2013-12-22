package de.gemo.gameengine.core.debug;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.gameengine.manager.FontManager;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

import static org.lwjgl.opengl.GL11.*;

public class StandardDebugMonitor extends AbstractDebugMonitor {

    @Override
    public void render() {
        glPushMatrix();
        {
            TrueTypeFont font = FontManager.getStandardFont(Font.BOLD);

            glDisable(GL_CULL_FACE);
            glDisable(GL_TEXTURE_RECTANGLE_ARB);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_LIGHTING);
            glDisable(GL_LIGHT0);
            int fontX = 125;
            int fontY = 5;
            glTranslatef(20, 20, 0);

            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            int height = 200;
            if (!this.isShowExtended()) {
                height = 90;
            }
            // // DRAW BACKGROUND
            Color.black.bind();
            glBegin(GL_QUADS);
            {
                glVertex2f(0, 0);
                glVertex2f(250, 0);
                glVertex2f(250, height);
                glVertex2f(0, height);
            }
            glEnd();

            // // DRAW OUTLINE
            Color.white.bind();
            glLineWidth(1.5f);
            glBegin(GL_LINE_LOOP);
            {
                glVertex3f(0, 0, 0);
                glVertex3f(250, 0, 0);
                glVertex3f(250, height, 0);
                glVertex3f(0, height, 0);
            }
            glEnd();
            // DRAW STIPPLED LINE
            Color.white.bind();
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glLineWidth(1f);
            glEnable(GL_LINE_STIPPLE);
            glLineStipple(3, (short) 0x0101);
            glBegin(GL_LINE);
            {
                glVertex3f(20, fontY + 20, 0);
                glVertex3f(230, fontY + 20, 0);
            }
            glEnd();
            glDisable(GL_LINE_STIPPLE);

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            font.drawString((int) (fontX - font.getWidth("DebugMonitor") / 2f), fontY, "DebugMonitor", Color.red);

            fontX = 20;
            fontY = 25;

            font.drawString(fontX, fontY + 10, "FPS: " + this.getFPS() + (this.isUseVSync() ? " (vsync)" : ""), Color.red);
            font.drawString(fontX, fontY + 25, "Delta: " + this.getDelta(), Color.red);

            glDisable(GL_BLEND);

            if (this.isShowExtended()) {
                // DRAW STIPPLED LINE
                Color.white.bind();

                glLineWidth(1f);
                glEnable(GL_LINE_STIPPLE);
                glLineStipple(3, (short) 0x0101);
                glBegin(GL_LINE);
                {
                    glVertex3f(20, fontY + 65, 0);
                    glVertex3f(230, fontY + 65, 0);
                }
                glEnd();
                glDisable(GL_LINE_STIPPLE);
                glEnable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                font.drawString(fontX, fontY + 75, "F1: toggle vysnc", Color.orange);
                font.drawString(fontX, fontY + 90, "F2: toggle DebugMonitor", Color.orange);
                font.drawString(fontX, fontY + 105, "F3: toggle extended information", Color.orange);
                font.drawString(fontX, fontY + 135, "F11: toggle graphics", Color.orange);
                font.drawString(fontX, fontY + 150, "F12: toggle hitboxes", Color.orange);

                glDisable(GL_BLEND);
            }
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_ALPHA_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            glDepthFunc(GL_LEQUAL);
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
        }
        glPopMatrix();
    }
}
