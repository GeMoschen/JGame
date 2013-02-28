package de.gemo.engine.particles;

import java.util.HashMap;

public class ParticleSystem {

    private int IDCOUNTER = 0;
    private HashMap<Integer, Emitter> emitterMap = new HashMap<Integer, Emitter>();

    public int getNewEmitterID() {
        return IDCOUNTER++;
    }

    public final void addEmitter(Emitter emitter) {
        emitterMap.put(emitter.getID(), emitter);
    }

    public final void removeEmitter(Emitter emitter) {
        emitterMap.remove(emitter.getID());
    }

    public final void removeAllEmitter() {
        this.emitterMap.clear();
    }

    public final void update() {
        for (Emitter emitter : this.emitterMap.values()) {
            emitter.updateParticles();
        }
    }

    public final void render() {
        for (Emitter emitter : this.emitterMap.values()) {
            emitter.render();
        }
    }
}
