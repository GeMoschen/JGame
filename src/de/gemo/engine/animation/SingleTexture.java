package de.gemo.engine.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;

import de.gemo.engine.manager.TextureManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

public class SingleTexture {
    private final Texture texture;
    private float width, height;
    private float x;
    private float y;
    private float u, v, u2, v2;
    private float halfWidth = this.width / 2f;
    private float halfHeight = this.height / 2f;

    private boolean newList = true;

    private FloatBuffer verts;
    private FloatBuffer tex;

    public SingleTexture(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.setDimensions(x, y, width, height);
    }

    public SingleTexture crop(float x, float y, float width, float height) {
        return new SingleTexture(texture, x, y, width, height);
    }

    public void setDimensions(float x, float y, float width, float height) {
        u = x;
        v = y;

        u2 = (x + width);
        v2 = (y + height);

        this.width = width;
        this.height = height;

        this.halfWidth = this.width / 2f;
        this.halfHeight = this.height / 2f;
        this.newList = true;
        this.createVertices();
    }

    private void createVertices() {
        verts = BufferUtils.createFloatBuffer(2 * 4);
        tex = BufferUtils.createFloatBuffer(2 * 4);
        verts.put(new float[]{-halfWidth, -halfHeight, halfWidth, -halfHeight, halfWidth, halfHeight, -halfWidth, halfHeight});
        tex.put(new float[]{u, v, u2, v, u2, v2, u, v2});
        newList = false;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void render(float width, float height, float r, float g, float b, float alpha) {
        glPushMatrix();
        {
            this.texture.bind();
            if (alpha != 0) {
                glEnable(GL_BLEND);
                glColor4f(r, g, b, alpha);
            } else {
                glDisable(GL_BLEND);
                glColor3f(r, g, b);
            }

            width = width / 2f;
            height = height / 2f;

            glBegin(GL_QUADS);
            glTexCoord2f(u, v);
            glVertex2f(-(width), -(height));
            glTexCoord2f(u2, v);
            glVertex2f(+(width), -(height));
            glTexCoord2f(u2, v2);
            glVertex2f(+(width), +(height));
            glTexCoord2f(u, v2);
            glVertex2f(-(width), +(height));
            glEnd();
        }
        glPopMatrix();
    }

    public void render(float r, float g, float b, float alpha) {
        glPushMatrix();
        {
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_TEXTURE_RECTANGLE_ARB);
            this.texture.bind();
            if (alpha != 0) {
                glEnable(GL_BLEND);
                glColor4f(r, g, b, alpha);
            } else {
                glDisable(GL_BLEND);
                glColor3f(r, g, b);
            }

            if (newList) {
                this.createVertices();
            }

            verts.rewind();
            tex.rewind();

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            glVertexPointer(2, 0, verts);
            glTexCoordPointer(2, 0, tex);

            glDrawArrays(GL_QUADS, 0, 4);

            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisableClientState(GL_VERTEX_ARRAY);
            glDisable(GL_TEXTURE_RECTANGLE_ARB);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    public MultiTexture toMultiTexture() {
        return TextureManager.SingleToMultiTexture(this);
    }

    public SingleTexture clone() {
        return new SingleTexture(this.texture, x, y, width, height);
    }
}
