package de.gemo.engine.particles;

import java.util.Random;

import org.newdawn.slick.Color;

import de.gemo.engine.textures.SingleTexture;

import static org.lwjgl.opengl.GL11.*;

public abstract class Particle {
    protected static Random generator = new Random();

    protected float x;
    protected float y;
    protected float dx;
    protected float dy;
    protected float size;
    protected int life;
    protected Color color;
    protected boolean dead = false;
    protected SingleTexture texture;

    public Particle(SingleTexture texture, float x, float y, float dx, float dy, float size, int life, Color color) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.size = size;
        this.life = life;
        this.color = color;
    }

    public void update() {
        x += dx;
        y += dy;
        life--;
        dead = life < 1;
    }

    public final boolean isDead() {
        return dead;
    }

    public final void startUse() {
        this.texture.startUse();
    }

    public final void endUse() {
        this.texture.endUse();
    }

    public void render() {
        glPushMatrix();
        {
            this.color.bind();
            glTranslatef(x, y, 0);
            glScalef(size, size, 1);
            color.bind();
            this.texture.renderEmbedded();
        }
        glPopMatrix();
    }
}