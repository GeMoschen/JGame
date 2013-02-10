package de.gemo.game.interfaces;

import de.gemo.game.collision.Hitbox;

public interface IClickable {
    public Hitbox getClickbox();

    public void setClickbox(Hitbox hitbox);

    public void recalculateClickbox();
}
