package de.gemo.engine.textures;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

import de.gemo.engine.manager.TextureManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

public class SingleTextureVertexArray extends SingleTexture {
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

    public SingleTextureVertexArray(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
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

    public void startUse() {
        this.startUse(1, 1, 1, 1);
    }

    public void startUse(Color color) {
        this.startUse(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha());
    }

    public void startUse(float r, float g, float b, float alpha) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_TEXTURE_RECTANGLE_ARB);
        this.texture.bind();
        glEnable(GL_BLEND);
        glColor4f(r, g, b, alpha);
    }

    public void endUse() {
        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);
    }

    public void render(float width, float height, float r, float g, float b, float alpha) {
        glPushMatrix();
        {
            // start use
            this.startUse(r, g, b, alpha);

            // scale matrix
            glScalef(width / this.getWidth(), height / this.getHeight(), 1f);

            // render
            this.renderEmbedded();

            // end use
            this.endUse();
        }
        glPopMatrix();
    }

    public void render(float r, float g, float b, float alpha) {
        // start use
        this.startUse(r, g, b, alpha);

        // render
        this.renderEmbedded();

        // end use
        this.endUse();
    }

    public void renderEmbedded() {
        glPushMatrix();
        {
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
