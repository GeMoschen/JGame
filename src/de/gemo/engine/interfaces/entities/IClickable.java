package de.gemo.engine.interfaces.entities;

import de.gemo.engine.collision.Hitbox;

public interface IClickable {
    public Hitbox getClickbox();

    public void setClickbox(Hitbox hitbox);

    public void recalculateClickbox();
}
