package de.gemo.game.terrain.core;

import de.gemo.gameengine.units.*;

public interface IPhysicsObject {

    public abstract Vector2f getPosition();

    public abstract Vector2f getVelocity();

    public abstract void setPosition(Vector2f position);

    public abstract void setVelocity(Vector2f velocity);

    public abstract void updatePhysics(int delta);
}
