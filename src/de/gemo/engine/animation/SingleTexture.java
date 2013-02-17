package de.gemo.engine.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;

import de.gemo.engine.manager.TextureManager;

import static org.lwjgl.opengl.GL11.*;

public class SingleTexture {
    private final Texture texture;
    private float width, height;
    private float x;
    private float y;
    private float u, v, u2, v2;

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
        u = x / this.texture.getImageWidth();
        v = y / this.texture.getImageHeight();

        u2 = (x + width) / this.texture.getImageWidth();
        v2 = (y + height) / this.texture.getImageHeight();

        this.width = width;
        this.height = height;
        this.newList = true;
    }

    private void createVertices() {
        float halfWidth = this.width / 2f;
        float halfHeight = this.height / 2f;
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

    public void render(float x, float y, float alpha) {
        this.render(x, y, 1, 1, 1, alpha);
    }

    public void render(float x, float y, float r, float g, float b, float alpha) {
        // bind texture and set color
        this.texture.bind();
        glColor4f(r, g, b, alpha);

        if (newList) {
            this.createVertices();
        }

        verts.rewind();
        tex.rewind();

        glTranslatef(x, y, 0);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glVertexPointer(2, 0, verts);
        glTexCoordPointer(2, 0, tex);

        glDrawArrays(GL_QUADS, 0, 4);

        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
        glTranslatef(-x, -y, 0);
    }

    public MultiTexture toMultiTexture() {
        return TextureManager.SingleToMultiTexture(this);
    }

    public SingleTexture clone() {
        return new SingleTexture(this.texture, x, y, width, height);
    }
}
