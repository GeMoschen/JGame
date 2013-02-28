package de.gemo.engine.particles;

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public abstract class Emitter {
    protected static Random generator = new Random();

    private final ParticleSystem particleSystem;
    private final ArrayList<Particle> particleList;

    protected final int ID;
    protected float x, y;

    public Emitter(ParticleSystem particleSystem, float x, float y) {
        this.ID = particleSystem.getNewEmitterID();
        this.particleSystem = particleSystem;
        this.particleList = new ArrayList<Particle>();
        this.setPosition(x, y);
        this.register();
    }

    private final void register() {
        this.particleSystem.addEmitter(this);
    }

    public final void unregister() {
        this.particleList.clear();
        this.particleSystem.removeEmitter(this);
    }

    public final int getID() {
        return ID;
    }

    public final void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final void updateParticles() {
        for (int i = 0; i <= this.particleList.size() - 1; i++) {
            this.particleList.get(i).update();
            if (this.particleList.get(i).isDead()) {
                this.particleList.remove(i);
            }
        }
    }

    public final void render() {
        glPushMatrix();
        {
            glTranslatef(x, y, 0);
            for (int i = 0; i <= this.particleList.size() - 1; i++) {
                if (i == 0) {
                    this.particleList.get(i).startUse();
                }
                this.particleList.get(i).render();
                if (i == this.particleList.size() - 1) {
                    this.particleList.get(i).endUse();
                }
            }
        }
        glPopMatrix();
    }

    public final void createParticles(int amount, int x, int y) {
        for (int i = 0; i < amount; i++) {
            this.createParticle(x, y);
        }
    }

    public final void createParticles(int amount) {
        this.createParticles(amount, 0, 0);
    }

    protected final void registerParticle(Particle particle) {
        this.particleList.add(particle);
    }

    public abstract void createParticle();

    public abstract void createParticle(int x, int y);
}
