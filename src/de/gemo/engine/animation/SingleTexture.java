package de.gemo.engine.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class SingleTexture {
    private final Texture texture;
    private float width, height;
    private float x;
    private float y;
    private final float u, v, u2, v2;

    private boolean newList = true;
    // private int displayList;

    private FloatBuffer verts;
    private FloatBuffer tex;

    public SingleTexture(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.x = x;
        this.y = y;

        u = x / this.texture.getImageWidth();
        v = y / this.texture.getImageHeight();

        u2 = (x + width) / this.texture.getImageWidth();
        v2 = (y + height) / this.texture.getImageHeight();

        this.width = width;
        this.height = height;
    }

    private void createVertices(float z) {
        float halfWidth = this.width / 2f;
        float halfHeight = this.height / 2f;
        verts = BufferUtils.createFloatBuffer(3 * 4);
        tex = BufferUtils.createFloatBuffer(2 * 4);
        verts.put(new float[]{-halfWidth, -halfHeight, z, halfWidth, -halfHeight, z, halfWidth, halfHeight, z, -halfWidth, halfHeight, z});
        tex.put(new float[]{u, v, u2, v, u2, v2, u, v2});
        newList = false;
    }

    // private void createDisplayList(float z) {
    // GL11.glPushMatrix();
    // displayList = GL11.glGenLists(1);
    //
    // float halfWidth = this.width / 2f;
    // float halfHeight = this.height / 2f;
    // GL11.glNewList(displayList, GL11.GL_COMPILE);
    //
    // this.texture.bind();
    // // begin quads
    // GL11.glBegin(GL11.GL_QUADS);
    //
    // // up-left
    // GL11.glTexCoord2f(u, v);
    // GL11.glVertex3f(-halfWidth, -halfHeight, z);
    //
    // // up-right
    // GL11.glTexCoord2f(u2, v);
    // GL11.glVertex3f(halfWidth, -halfHeight, z);
    //
    // // down-right
    // GL11.glTexCoord2f(u2, v2);
    // GL11.glVertex3f(halfWidth, halfHeight, z);
    //
    // // down-left
    // GL11.glTexCoord2f(u, v2);
    // GL11.glVertex3f(-halfWidth, halfHeight, z);
    //
    // // end quads
    // GL11.glEnd();
    // GL11.glEndList();
    // GL11.glPopMatrix();
    // newList = false;
    // }

    public void scale(float scaleX, float scaleY) {
        this.width *= scaleX;
        this.height *= scaleY;
        newList = true;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void render(float x, float y, float z, float alpha) {
        this.render(x, y, z, 1, 1, 1, alpha);
    }

    public void render(float x, float y, float z, float r, float g, float b, float alpha) {
        // bind texture
        this.texture.bind();
        GL11.glColor4f(r, g, b, alpha);

        if (newList) {
            this.createVertices(z);
            // this.createDisplayList(z);
        }

        // GL11.glCallList(displayList);

        verts.rewind();
        tex.rewind();

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        GL11.glVertexPointer(3, 0, verts);
        GL11.glTexCoordPointer(2, 0, tex);

        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    }

    public SingleTexture clone() {
        return new SingleTexture(this.texture, x, y, width, height);
    }
}