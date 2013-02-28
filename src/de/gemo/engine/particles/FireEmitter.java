package de.gemo.engine.particles;

import org.newdawn.slick.Color;

import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.SingleTexture;

public class FireEmitter extends Emitter {

    private SingleTexture particleTexture = null;

    public FireEmitter(ParticleSystem particleSystem, float x, float y) {
        super(particleSystem, x, y);
        try {
            this.particleTexture = TextureManager.loadSingleTexture("textures\\ui\\particle.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createParticle() {
        this.createParticle(0, 0);
    }

    @Override
    public void createParticle(int x, int y) {
        float dx, dy;
        int tY = 1;
        float scale = 1f;
        dx = (float) generator.nextGaussian() * 0.005f * scale;
        dx = 0;
        dy = -Math.abs((float) generator.nextGaussian() * 0.01f * tY) - (0.2f) * scale;
        float size = (float) generator.nextGaussian() * 0.1f * scale;
        int life = (int) ((generator.nextFloat() * 100f));
        this.registerParticle(new FireParticle(this.particleTexture, x, y, dx, dy, size, life, Color.gray));
    }
}
