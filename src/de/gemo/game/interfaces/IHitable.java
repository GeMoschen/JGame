package de.gemo.game.interfaces;

import de.gemo.game.collision.Hitbox;

public interface IHitable {
    public Hitbox getHitbox();

    public void setHitbox(Hitbox hitbox);
}
