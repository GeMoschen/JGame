package de.gemo.game.entity;

import de.gemo.game.animation.Animation;
import de.gemo.game.animation.SingleTexture;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.interfaces.IClickable;
import de.gemo.game.interfaces.Vector;

public class Entity2DClickable extends Entity2D implements IClickable {

    private Hitbox clickbox = null;

    public Entity2DClickable(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
        this.autoGenerateClickbox();
    }

    public Entity2DClickable(Vector center, SingleTexture singleTexture) {
        super(center, singleTexture);
        this.autoGenerateClickbox();
    }

    public Entity2DClickable(float x, float y, Animation animation) {
        super(x, y, animation);
        this.autoGenerateClickbox();
    }

    public Entity2DClickable(Vector center, Animation animation) {
        super(center, animation);
        this.autoGenerateClickbox();
    }

    @Override
    public void debugRender() {
        super.debugRender();
        this.clickbox.render();
    }

    @Override
    public void rotate(float angle) {
        super.rotate(angle);
        this.clickbox.rotate(angle);
    }

    @Override
    public void setAngle(float angle) {
        super.setAngle(angle);
        this.clickbox.setAngle(angle);
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        super.scale(scaleX, scaleY);
        this.clickbox.scaleX(scaleX);
        this.clickbox.scaleY(scaleY);
    }

    @Override
    public void move(float x, float y) {
        super.move(x, y);
        this.clickbox.recalculatePositions();
    }

    protected void autoGenerateClickbox() {
        this.clickbox = new Hitbox(this.center);
        float x = this.animation.getWidth() / 2;
        float y = this.animation.getHeight() / 2;
        this.clickbox.addPoint(-x, -y);
        this.clickbox.addPoint(+x, -y);
        this.clickbox.addPoint(+x, +y);
        this.clickbox.addPoint(-x, +y);
        this.clickbox.recalculatePositions();
    }

    @Override
    public void recalculateClickbox() {
        this.clickbox.recalculatePositions();
    }

    @Override
    public Hitbox getClickbox() {
        return this.clickbox;
    }

    @Override
    public void setClickbox(Hitbox hitbox) {
        this.clickbox = hitbox.clone();
        this.clickbox.recalculatePositions();
    }

}
