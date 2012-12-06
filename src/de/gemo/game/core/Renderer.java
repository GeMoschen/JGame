package de.gemo.game.core;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.entity.AbstractEntity2D;

public class Renderer {

    public static boolean SHOW_HITBOXES = true;
    public static boolean SHOW_GRAPHICS = true;

    public static void render(AbstractEntity2D entity) {
        GL11.glTranslated(entity.getX(), entity.getY(), entity.getZ());
        GL11.glRotated(entity.getAngle(), 0d, 0d, 1d);

        if (SHOW_HITBOXES) {
            GL11.glDisable(GL11.GL_BLEND);
            entity.debugRender();
            GL11.glEnable(GL11.GL_BLEND);
        }
        if (SHOW_GRAPHICS) {
            GL11.glEnable(GL11.GL_BLEND);
            entity.render();
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glRotated(-entity.getAngle(), 0d, 0d, 1d);
        GL11.glTranslated(-entity.getX(), -entity.getY(), -entity.getZ());
    }

    public static void render(String text, ComplexHitbox hitbox) {
        if (SHOW_HITBOXES) {
            GL11.glDisable(GL11.GL_BLEND);
            hitbox.render();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glTranslatef(hitbox.getCenter().getX(), hitbox.getCenter().getY(), (hitbox.getCenter().getZ() - 1));
            FontManager.getStandardFont().drawString((int) (FontManager.getStandardFont().getWidth(text) / -2f), 3, text, Color.white);
            GL11.glTranslatef(-hitbox.getCenter().getX(), -hitbox.getCenter().getY(), -(hitbox.getCenter().getZ() - 1));
        }
    }
}
