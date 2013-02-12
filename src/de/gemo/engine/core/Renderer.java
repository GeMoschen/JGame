package de.gemo.engine.core;

import org.lwjgl.opengl.GL11;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.entity.Entity;
import de.gemo.engine.entity.Entity2D;

public class Renderer {

    public static boolean SHOW_HITBOXES = false;
    public static boolean SHOW_GRAPHICS = true;

    public static void render(Entity2D renderable) {
        GL11.glTranslatef(renderable.getX(), renderable.getY(), renderable.getZ());
        GL11.glRotatef(renderable.getAngle(), 0, 0, 1);
        renderable.render();
        GL11.glRotatef(-renderable.getAngle(), 0, 0, 1);
        GL11.glTranslatef(-renderable.getX(), -renderable.getY(), -renderable.getZ());
    }

    public static void debugRender(Entity renderable) {
        renderable.debugRender();
    }

    public static void renderHitbox(String name, Hitbox hitbox) {
        GL11.glDisable(GL11.GL_BLEND);
        hitbox.render();
        GL11.glEnable(GL11.GL_BLEND);
        FontManager.getStandardFont().drawString(hitbox.getCenter().getX() - ((int) (FontManager.getStandardFont().getWidth(name) / 2)), hitbox.getCenter().getY(), name);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
