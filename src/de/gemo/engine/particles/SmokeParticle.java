package de.gemo.engine.particles;

import org.newdawn.slick.Color;

import de.gemo.engine.textures.SingleTexture;

import static org.lwjgl.opengl.GL11.*;

public class SmokeParticle extends Particle {

    private int maxlife;
    private float alpha;

    public SmokeParticle(SingleTexture texture, float x, float y, float dx, float dy, float size, int life, Color color) {
        super(texture, x, y, dx, dy, size, life, color);
        this.maxlife = life;
        this.alpha = 1f;
    }

    public void update() {
        super.update();
        dy += 0.0007f;
        dx *= 0.99f;
        alpha -= 0.01;
        size -= 0.007f;
        if (alpha < 0) {
            this.dead = true;
        }
    }

    public void render() {
        glEnable(GL_BLEND);
        glPushMatrix();
        {
            glTranslatef(x, y, 0);
            glScalef(size * 0.7f, size, 1);
            float alpha2 = ((float) life / (float) maxlife);
            float c = alpha2 / 2f;
            c = Math.min(c, 0.3f);
            glColor4f(c, c, c, alpha);
            this.texture.renderEmbedded();
        }
        glPopMatrix();
    }
}