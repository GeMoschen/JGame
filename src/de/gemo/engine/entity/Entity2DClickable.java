package de.gemo.engine.entity;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.interfaces.entities.IClickable;
import de.gemo.engine.units.Vector;

public class Entity2DClickable extends Entity2D implements IClickable {

    private Hitbox clickbox = null;

    public Entity2DClickable(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
        this.autoGenerateClickbox();
        this.setCenter(x, y);
        this.setPositionOnScreen(x, y);
    }

    public Entity2DClickable(Vector center, SingleTexture singleTexture) {
        super(center, singleTexture);
        this.autoGenerateClickbox();
        this.setCenter(center);
        this.setPositionOnScreen(center.getX(), center.getY());
    }

    public Entity2DClickable(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
        this.autoGenerateClickbox();
        this.setCenter(x, y);
        this.setPositionOnScreen(x, y);
    }

    public Entity2DClickable(Vector center, MultiTexture multiTexture) {
        super(center, multiTexture);
        this.autoGenerateClickbox();
        this.setCenter(center);
        this.setPositionOnScreen(center.getX(), center.getY());
    }

    public Entity2DClickable(float x, float y, Animation animation) {
        super(x, y, animation);
        this.autoGenerateClickbox();
        this.setCenter(x, y);
        this.setPositionOnScreen(x, y);
    }

    public Entity2DClickable(Vector center, Animation animation) {
        super(center, animation);
        this.autoGenerateClickbox();
        this.setCenter(center);
        this.setPositionOnScreen(center.getX(), center.getY());
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

    // protected void autoGenerateClickbox(float offsetX, float offsetY) {
    // this.clickbox = new Hitbox(this.center.getX() + offsetX, this.center.getY() + offsetY);
    // float x = this.animation.getWidth() / 2;
    // float y = this.animation.getHeight() / 2;
    // this.clickbox.addPoint(-x, -y);
    // this.clickbox.addPoint(+x, -y);
    // this.clickbox.addPoint(+x, +y);
    // this.clickbox.addPoint(-x, +y);
    // this.clickbox.recalculatePositions();
    // }

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
