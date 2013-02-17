package de.gemo.engine.core;

import org.newdawn.slick.Color;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.entity.Entity;
import de.gemo.engine.entity.Entity2D;
import de.gemo.engine.manager.FontManager;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    public static void render(Entity2D renderable) {
        glPushMatrix();
        {
            glTranslatef((int) renderable.getX(), (int) renderable.getY(), renderable.getZ());
            glRotatef(renderable.getAngle(), 0, 0, 1);
            glScalef(renderable.getScaleX(), renderable.getScaleY(), 0f);
            renderable.render();
        }
        glPopMatrix();
    }

    public static void debugRender(Entity renderable) {
        renderable.debugRender();
    }

    public static void renderHitbox(String name, Hitbox hitbox) {
        glPushMatrix();
        {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            hitbox.render();
            glEnable(GL_BLEND);
            FontManager.getStandardFont().drawString(hitbox.getCenter().getX() - ((int) (FontManager.getStandardFont().getWidth(name) / 2 + 3)), hitbox.getCenter().getY(), name, Color.white);
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }
}
