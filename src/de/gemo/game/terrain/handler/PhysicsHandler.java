package de.gemo.game.terrain.handler;

import java.util.*;

import de.gemo.game.terrain.entities.*;

public class PhysicsHandler {

    private static PhysicsHandler handler;

    private List<IPhysicsObject> objects = new ArrayList<IPhysicsObject>();

    public PhysicsHandler() {
        handler = this;
    }

    public static void addObject(IPhysicsObject object) {
        handler.add(object);
    }

    public static void removeObject(IPhysicsObject object) {
        handler.remove(object);
    }

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

    public void updateAll(int delta) {
        for (int i = 0; i < this.objects.size(); i++) {
            this.objects.get(i).updatePhysics(delta);
        }
    }
}
