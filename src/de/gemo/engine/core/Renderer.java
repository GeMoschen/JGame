package de.gemo.engine.core;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.entity.Entity;
import de.gemo.engine.entity.Entity2D;

public class Renderer {

    public static void render(Entity2D renderable) {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef((int) renderable.getX(), (int) renderable.getY(), renderable.getZ());
            GL11.glRotatef(renderable.getAngle(), 0, 0, 1);
            GL11.glScalef(renderable.getScaleX(), renderable.getScaleY(), 0f);
            renderable.render();
        }
        GL11.glPopMatrix();
    }
    public static void debugRender(Entity renderable) {
        renderable.debugRender();
    }

    public static void renderHitbox(String name, Hitbox hitbox) {
        GL11.glPushMatrix();
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            hitbox.render();
            GL11.glEnable(GL11.GL_BLEND);
            FontManager.getStandardFont().drawString(hitbox.getCenter().getX() - ((int) (FontManager.getStandardFont().getWidth(name) / 2 + 3)), hitbox.getCenter().getY(), name, Color.white);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();
    }
}
