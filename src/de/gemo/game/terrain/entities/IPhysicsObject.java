package de.gemo.game.terrain.entities;

import de.gemo.gameengine.units.*;

public interface IPhysicsObject {

    Vector2f getPosition();

    Vector2f getVelocity();

    void setPosition(Vector2f position);

    void setVelocity(Vector2f velocity);

    void updatePhysics(int delta);
}
