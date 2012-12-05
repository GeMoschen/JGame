package de.gemo.game.entity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.game.collision.Vector;

public abstract class AbstractEntity2D extends AbstractEntity {

    protected Texture texture = null;
    protected double textureWidth, textureHeight;
    protected double halfWidth, halfHeight;
    protected double angle = 0;
    protected double alpha = 1d;

    public AbstractEntity2D(Vector center, String fileName) throws FileNotFoundException, IOException {
        this(center.getX(), center.getY(), fileName);
    }

    public AbstractEntity2D(double x, double y, String fileName) throws FileNotFoundException, IOException {
        this(x, y, TextureLoader.getTexture("PNG", new FileInputStream(fileName)));
    }

    public AbstractEntity2D(double x, double y, Texture texture) {
        super(x, y);
        // set texture
        this.setTexture(texture);
    }

    public void setTexture(Texture texture) {
        // set texture
        this.texture = texture;
        this.textureWidth = this.texture.getImageWidth();
        this.textureHeight = this.texture.getImageHeight();

        // pre-calculate width/height
        this.halfWidth = this.textureWidth / 2;
        this.halfHeight = this.textureHeight / 2;
    }

    @Override
    public void render() {
        // bind texture
////        this.texture.bind();
//
//        GL11.glTranslated((int) this.center.getX(), (int) this.center.getY(), this.center.getZ());
//        GL11.glRotated(this.angle, 0d, 0d, 1d);
//
//        // begin quads
//        GL11.glBegin(GL11.GL_QUADS);
//
//        // draw bitmap
//
//        // up-left
//        GL11.glTexCoord2d(0.0f, 0.0f);
//        GL11.glVertex3d(-this.halfWidth, -this.halfHeight, 0);
//
//        // up-right
//        GL11.glTexCoord2d(this.textureWidth, 0.0f);
//        GL11.glVertex3d(+this.halfWidth, -this.halfHeight, 0);
//
//        // down-right
//        GL11.glTexCoord2d(this.textureWidth, this.textureHeight);
//        GL11.glVertex3d(+this.halfWidth, +this.halfHeight, 0);
//
//        // down-left
//        GL11.glTexCoord2d(0.0f, this.textureHeight);
//        GL11.glVertex3d(-this.halfWidth, +this.halfHeight, 0);
//
//        // end quads
//        GL11.glEnd();
//
//        GL11.glRotated(-this.angle, 0d, 0d, 1d);
//        GL11.glTranslated((int) -this.center.getX(), (int) -this.center.getY(), -this.center.getZ());
    }

    public void debugRender() {
        // render center
        super.render();

        // begin quads
        GL11.glBegin(GL11.GL_LINE_LOOP);

        // draw bitmap
        // up-left
        GL11.glVertex3d(-this.halfWidth, -this.halfHeight, -1);

        // up-right
        GL11.glVertex3d(+this.halfWidth, -this.halfHeight, -1);

        // down-right
        GL11.glVertex3d(+this.halfWidth, +this.halfHeight, -1);

        // down-left
        GL11.glVertex3d(-this.halfWidth, +this.halfHeight, -1);

        // end quads
        GL11.glEnd();
    }

    public double getX() {
        return this.center.getX();
    }

    public double getY() {
        return this.center.getY();
    }

    public double getZ() {
        return this.center.getZ();
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.rotate(angle - this.angle);
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void rotate(double angle) {
        this.angle += angle;
        if (this.angle > 360d) {
            this.angle -= 360d;
        }
        if (this.angle < 0d) {
            this.angle += 360d;
        }
    }
}
