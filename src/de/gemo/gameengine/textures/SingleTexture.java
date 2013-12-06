package de.gemo.gameengine.textures;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.renderer.Renderer;

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

    private int vboVertexHandle;
    private int vboTextureHandle;

    public SingleTexture(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.setDimensions(x, y, width, height);
    }

    public SingleTexture crop(float x, float y, float width, float height) {
        return new SingleTexture(texture, x, y, width, height);
    }

    public void setDimensions(float x, float y, float width, float height) {
        u = x / texture.getImageWidth();
        v = y / texture.getImageHeight();

        u2 = (x + width) / texture.getImageWidth();
        v2 = (y + height) / texture.getImageHeight();

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

        verts.flip();
        tex.flip();

        vboVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vboTextureHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, tex, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

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
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        Renderer.bindTexture(this.texture);
        glColor4f(r, g, b, alpha);
    }

    public void endUse() {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public void render(float width, float height, float r, float g, float b, float alpha) {
        glPushMatrix();
        {
            // start use
            this.startUse(r, g, b, alpha);

            // scale and render
            glScalef(width / this.getWidth(), height / this.getHeight(), 1f);
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

            glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
            glVertexPointer(2, GL_FLOAT, 0, 0L);

            glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glDrawArrays(GL_QUADS, 0, 4);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisableClientState(GL_VERTEX_ARRAY);
        }
        glPopMatrix();
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public Texture getTexture() {
        return texture;
    }

    public MultiTexture toMultiTexture() {
        return TextureManager.SingleToMultiTexture(this);
    }

    public SingleTexture clone() {
        return new SingleTexture(this.texture, x, y, width, height);
    }
}
