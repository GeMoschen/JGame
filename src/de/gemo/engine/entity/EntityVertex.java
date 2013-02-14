package de.gemo.engine.entity;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;

import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.gui.GUIElement;

public class EntityVertex extends GUIElement {

    private static int boxSize = 10;
    private static int pointSize = 3;

    public EntityVertex(float x, float y) {
        super(x, y, new MultiTexture(boxSize, boxSize));
        this.setAutoLooseFocus(false);
    }

    @Override
    public void render() {
        glPushMatrix();
        {
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            // render center
            if (this.isHovered()) {
                Color.red.bind();
            } else if (this.isFocused()) {
                Color.pink.bind();
            } else {
                Color.orange.bind();
            }
            glBegin(GL_LINE_LOOP);
            glVertex3f(-pointSize, -pointSize, this.getZ());
            glVertex3f(pointSize, -pointSize, this.getZ());
            glVertex3f(+pointSize, +pointSize, this.getZ());
            glVertex3f(-pointSize, +pointSize, this.getZ());
            glEnd();

            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }
}
