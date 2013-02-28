package de.gemo.game.core;

import java.util.Random;

import de.gemo.engine.core.Engine;
import de.gemo.engine.manager.MouseManager;
import de.gemo.engine.particles.Emitter;
import de.gemo.engine.particles.FireEmitter;
import de.gemo.engine.particles.ParticleSystem;
import de.gemo.engine.particles.SmokeEmitter;

public class MyEngine2 extends Engine {

    private Random random = new Random();
    private ParticleSystem particleSystem;
    private Emitter smokeEmitter, fireEmitter;

    public MyEngine2() {
        super("My Enginetest", 800, 600, false);
    }

    @Override
    protected void createManager() {
        this.setDebugMonitor(new ExtendedDebugMonitor());
    }

    @Override
    protected final void createGUI() {
        particleSystem = new ParticleSystem();
        smokeEmitter = new SmokeEmitter(particleSystem, 400, 300);
        fireEmitter = new FireEmitter(particleSystem, 400, 300);
    }

    @Override
    protected void renderGame() {
        if (MouseManager.INSTANCE.isButtonDown(0)) {
            smokeEmitter.createParticles(8, 20, 0);
            fireEmitter.createParticles(3, 20, 0);
            // fireEmitter.setPosition(402, 300);
        }
        this.particleSystem.update();
        this.particleSystem.render();
    }
}
