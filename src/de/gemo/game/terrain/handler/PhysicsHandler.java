package de.gemo.game.terrain.handler;

import de.gemo.game.terrain.entities.IPhysicsObject;

import java.util.ArrayList;
import java.util.List;

public class PhysicsHandler {

    private static PhysicsHandler HANDLER;

    private List<IPhysicsObject> _objects = new ArrayList<IPhysicsObject>();

    public PhysicsHandler() {
        HANDLER = this;
    }

    public static void addObject(IPhysicsObject object) {
        HANDLER.add(object);
    }

    public static void removeObject(IPhysicsObject object) {
        HANDLER.remove(object);
    }

    public void add(IPhysicsObject object) {
        _objects.add(object);
    }

    public void remove(IPhysicsObject object) {
        for (int i = 0; i < _objects.size(); i++) {
            if (_objects.get(i) == object) {
                _objects.remove(i);
                return;
            }
        }
    }

    public void updateAll(int delta) {
        for (int i = 0; i < _objects.size(); i++) {
            _objects.get(i).updatePhysics(delta);
        }
    }
}
