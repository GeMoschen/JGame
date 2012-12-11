package de.gemo.game.collision;

public interface IClickable {
    public Hitbox getClickbox();

    public void setClickbox(Hitbox hitbox);

    public void recalculateClickbox();
}
