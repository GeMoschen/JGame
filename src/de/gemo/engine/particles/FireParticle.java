package de.gemo.engine.particles;

import org.newdawn.slick.Color;

import de.gemo.engine.textures.SingleTexture;

import static org.lwjgl.opengl.GL11.*;

public class FireParticle extends Particle {

    private int maxlife;
    private float alpha;

    public FireParticle(SingleTexture texture, float x, float y, float dx, float dy, float size, int life, Color color) {
        super(texture, x, y, dx, dy, size, life, color);
        this.maxlife = life;
        this.alpha = 1f;
        this.color = new Color(0.6f + Math.min(generator.nextFloat(), 0.4f), 0.3f + Math.min(generator.nextFloat(), 0.6f), 0);
    }

    public void update() {
        super.update();
        alpha -= 0.015;
    }

    public void render() {
        glEnable(GL_BLEND);
        glPushMatrix();
        {
            glTranslatef(x, y, 0);
            glScalef(size * 0.5f, size, 1);
            float alpha2 = ((float) life / (float) maxlife);
            float c = alpha2 / 2f;
            // c = Math.min(c, 0.3f);
            glColor4f(color.r, color.g, color.b, c);
            this.texture.renderEmbedded();
        }
        glPopMatrix();
    }
}