package de.gemo.engine.entity;

import org.newdawn.slick.Color;

import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.textures.MultiTexture;

import static org.lwjgl.opengl.GL11.*;

public class EntityVertex extends GUIElement {

    private static int boxSize = 10;
    private static int pointSize = 3;

    public EntityVertex(float x, float y) {
        super(x, y, new MultiTexture(boxSize, boxSize));
        this.setAutoLooseFocus(false);
    }

    @Override
    public void render() {
        // glPushMatrix();
        // {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        // render center
        if (this.isHovered() && !this.isFocused()) {
            Color.darkGray.bind();
        } else if (this.isFocused()) {
            Color.red.bind();
        } else {
            Color.lightGray.bind();
        }
        glBegin(GL_QUADS);
        glVertex3f(-pointSize, -pointSize, this.getZ());
        glVertex3f(pointSize, -pointSize, this.getZ());
        glVertex3f(+pointSize, +pointSize, this.getZ());
        glVertex3f(-pointSize, +pointSize, this.getZ());
        glEnd();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        if (this.isHovered() || this.isFocused()) {
            FontManager.getStandardFont().drawString(-FontManager.getStandardFont().getWidth("" + this.entityID) / 2f, pointSize + 2, "" + this.entityID);
        }
        // }
        // glPopMatrix();
    }
}
