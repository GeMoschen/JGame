package de.gemo.game.animation;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Animation {

    protected Texture texture;
    protected final double tileWidth, tileHeight;
    protected final int tilesX, tilesY;
    private final int singleTileWidth, singleTileHeight;
    private final double halfTileWidth, halfTileHeight;
    protected int currentFrame = -1;
    protected final int maxFrames;

    protected double u = 0d, v = 0d;
    protected double u2 = 1d, v2 = 1d;

    protected double currentStep = 0d;

    private double wantedFPS;

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
        this.tileWidth = 1.0d / (double) tilesX;
        this.tileHeight = 1.0d / (double) tilesY;

        this.singleTileWidth = this.texture.getImageWidth() / tilesX;
        this.singleTileHeight = this.texture.getImageHeight() / tilesY;

        this.halfTileWidth = this.singleTileWidth / 2d;
        this.halfTileHeight = this.singleTileHeight / 2d;

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
        this.wantedFPS = (double) wantedFPS / 1000d;
    }

    public void step(double delta) {
        double toGo = this.wantedFPS * delta;
        this.currentStep += toGo;
        if (this.currentStep >= this.maxFrames) {
            this.currentStep -= this.maxFrames;
        }
        this.goToFrame((int) this.currentStep);
    }

    public double getU() {
        return u;
    }

    public double getU2() {
        return u2;
    }

    public double getV() {
        return v;
    }

    public double getV2() {
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

    public double getHalfTileWidth() {
        return halfTileWidth;
    }

    public double getHalfTileHeight() {
        return halfTileHeight;
    }

    public void render(double x, double y, double z) {
        // bind texture
        Color.white.bind();
        this.texture.bind();

        // begin quads
        GL11.glBegin(GL11.GL_QUADS);

        // up-left
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
        // up-right
        GL11.glTexCoord2d(u2, v);
        GL11.glVertex3d(x + this.singleTileWidth, y, z);

        // down-right
        GL11.glTexCoord2d(u2, v2);
        GL11.glVertex3d(x + this.singleTileWidth, y + this.singleTileHeight, z);

        // down-left
        GL11.glTexCoord2d(u, v2);
        GL11.glVertex3d(x, y + this.singleTileHeight, z);

        // end quads
        GL11.glEnd();
    }

    public void render(double x, double y, double z, double angle, double alpha, double width, double height) {
        // bind texture

        GL11.glColor4d(1, 1, 1, alpha);
        this.texture.bind();

        double halfW = width / 2;
        double halfH = height / 2;

        // begin quads
        GL11.glBegin(GL11.GL_QUADS);
        // up-left
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(-halfW, -halfH, 0);
        // up-right
        GL11.glTexCoord2d(u2, v);
        GL11.glVertex3d(+halfW, -halfH, 0);

        // down-right
        GL11.glTexCoord2d(u2, v2);
        GL11.glVertex3d(+halfW, +halfH, 0);

        // down-left
        GL11.glTexCoord2d(u, v2);
        GL11.glVertex3d(-halfW, +halfH, 0);

        // end quads
        GL11.glEnd();
    }
}
