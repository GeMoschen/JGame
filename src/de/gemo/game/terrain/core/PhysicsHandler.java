package de.gemo.game.terrain.core;

import java.util.*;

public class PhysicsHandler {
    private List<IPhysicsObject> objects = new ArrayList<IPhysicsObject>();

    public void add(IPhysicsObject object) {
        this.objects.add(object);
    }

    public void remove(IPhysicsObject object) {
        for (int i = 0; i < this.objects.size(); i++) {
            if (this.objects.get(i) == object) {
                this.objects.remove(i);
                return;
            }
        }
    }

    public void update(int delta) {
        for (int i = 0; i < this.objects.size(); i++) {
            this.objects.get(i).updatePhysics(delta);
        }
    }
}
