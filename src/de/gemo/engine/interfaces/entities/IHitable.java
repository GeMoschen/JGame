package de.gemo.engine.interfaces.entities;

import de.gemo.engine.collision.Hitbox;

public interface IHitable {
    public Hitbox getHitbox();

    public void setHitbox(Hitbox hitbox);
}
