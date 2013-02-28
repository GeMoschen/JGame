package de.gemo.engine.particles;

import org.newdawn.slick.Color;

import de.gemo.engine.manager.TextureManager;
import de.gemo.engine.textures.SingleTexture;

public class SmokeEmitter extends Emitter {

    private SingleTexture particleTexture = null;

    public SmokeEmitter(ParticleSystem particleSystem, float x, float y) {
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
        int tX = 1;
        int tY = 1;
        if (generator.nextBoolean()) {
            tX = -1;
        }

        float mx = (float) generator.nextGaussian() * 2 * tX;
        float scale = 1f;
        dx = (float) generator.nextGaussian() * 0.03f * tX * scale;
        dy = -Math.abs((float) generator.nextGaussian() * 0.15f * tY) - (0.2f) * scale;
        float size = (float) generator.nextGaussian() * 0.1f * 0.5f * scale;
        int life = (int) ((generator.nextFloat() * 60f));
        this.registerParticle(new SmokeParticle(this.particleTexture, x + mx, -4 + y, dx, dy, size, life, Color.gray));
    }
}
