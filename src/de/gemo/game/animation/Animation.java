package de.gemo.game.animation;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Animation {

    protected Texture texture;
    protected final float tileWidth, tileHeight;
    protected final int tilesX, tilesY;
    private final int singleTileWidth, singleTileHeight;
    private final float halfTileWidth, halfTileHeight;
    protected int currentFrame = -1;
    protected final int maxFrames;

    protected float u = 0f, v = 0f;
    protected float u2 = 1f, v2 = 1f;

    protected float currentStep = 0f;

    private float wantedFPS;

    public Animation(Texture texture, int tilesX, int tilesY) {
        this(texture, tilesX, tilesY, 30);
    }

    public Animation(Texture texture, int tilesX, int tilesY, int wantedFPS) {
        // set texture
        this.texture = texture;

        // save tiledimensions
        this.tilesX = tilesX;
        this.tilesY = tilesY;

        // pre-calculate width/height
        this.tileWidth = 1.0f / (float) tilesX;
        this.tileHeight = 1.0f / (float) tilesY;

        this.singleTileWidth = this.texture.getImageWidth() / tilesX;
        this.singleTileHeight = this.texture.getImageHeight() / tilesY;

        this.halfTileWidth = this.singleTileWidth / 2f;
        this.halfTileHeight = this.singleTileHeight / 2f;

        // go to frame 0
        this.goToFrame(0);

        // update fps
        this.setWantedFPS(wantedFPS);

        // calculate maxframes
        this.maxFrames = tilesX * tilesY;
    }

    public void nextFrame() {
        this.goToFrame(this.currentFrame + 1);
    }

    public void lastFrame() {
        this.goToFrame(this.currentFrame - 1);
    }

    public void goToFrame(int frame) {
        // we need different frames
        if (this.currentFrame == frame) {
            return;
        }

        // check framebounds
        if (frame < 0) {
            frame = maxFrames - 1;
        } else if (frame >= maxFrames) {
            frame = 0;
        }

        // update the frame
        this.currentFrame = frame;

        // calculate x & y-tile
        int posX = this.currentFrame % this.tilesX;
        int posY = this.currentFrame;
        if (this.currentFrame > this.tilesY) {
            posY = this.currentFrame / this.tilesY;
        }

        // calculate new u & v-coordinates
        u = posX * this.tileWidth;
        v = posY * this.tileHeight;
        u2 = u + this.tileWidth;
        v2 = v + this.tileHeight;
    }

    public void setWantedFPS(int wantedFPS) {
        this.wantedFPS = (float) wantedFPS / 1000f;
    }

    public void step(float delta) {
        float toGo = this.wantedFPS * delta;
        this.currentStep += toGo;
        if (this.currentStep >= this.maxFrames) {
            this.currentStep -= this.maxFrames;
        }
        this.goToFrame((int) this.currentStep);
    }

    public float getU() {
        return u;
    }

    public float getU2() {
        return u2;
    }

    public float getV() {
        return v;
    }

    public float getV2() {
        return v2;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getSingleTileWidth() {
        return singleTileWidth;
    }

    public int getSingleTileHeight() {
        return singleTileHeight;
    }

    public float getHalfTileWidth() {
        return halfTileWidth;
    }

    public float getHalfTileHeight() {
        return halfTileHeight;
    }

    public void render(float x, float y, float z) {
        this.render(x, y, z, 0f, 1f, this.texture.getImageWidth(), this.texture.getImageHeight());
    }

    public void render(float x, float y, float z, float angle, float alpha, float width, float height) {
        // bind texture
        GL11.glColor4f(1, 1, 1, alpha);
        this.texture.bind();

        float halfW = width / 2;
        float halfH = height / 2;

        // begin quads
        GL11.glBegin(GL11.GL_QUADS);

        // up-left
        GL11.glTexCoord2f(u, v);
        GL11.glVertex3f(-halfW, -halfH, 0);

        // up-right
        GL11.glTexCoord2f(u2, v);
        GL11.glVertex3f(+halfW, -halfH, 0);

        // down-right
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex3f(+halfW, +halfH, 0);

        // down-left
        GL11.glTexCoord2f(u, v2);
        GL11.glVertex3f(-halfW, +halfH, 0);

        // end quads
        GL11.glEnd();
    }
}
