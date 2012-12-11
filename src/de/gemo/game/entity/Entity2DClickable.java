package de.gemo.game.entity;

import org.newdawn.slick.opengl.Texture;

import de.gemo.game.collision.Hitbox;
import de.gemo.game.collision.IClickable;
import de.gemo.game.interfaces.Vector;

public class Entity2DClickable extends Entity2D implements IClickable {

    private Hitbox clickbox = null;

    public Entity2DClickable(float x, float y, Texture texture) {
        super(x, y, texture);
        this.autoGenerateClickbox();
    }

    public Entity2DClickable(Vector center, Texture texture) {
        super(center, texture);
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
    public void scale(float scale) {
        super.scale(scale);
        this.clickbox.scale(scale);
    }

    @Override
    public void scaleX(float scaleX) {
        super.scaleX(scaleX);
        this.clickbox.scaleX(scaleX);
    }

    @Override
    public void scaleY(float scaleY) {
        super.scaleY(scaleY);
        this.clickbox.scaleY(scaleY);
    }

    @Override
    public void move(float x, float y) {
        super.move(x, y);
        this.clickbox.recalculatePositions();
    }

    protected void autoGenerateClickbox() {
        this.clickbox = new Hitbox(this.center);
        this.clickbox.addPoint(-this.animation.getSingleTileWidth(), -this.animation.getSingleTileHeight());
        this.clickbox.addPoint(+this.animation.getSingleTileWidth(), -this.animation.getSingleTileHeight());
        this.clickbox.addPoint(+this.animation.getSingleTileWidth(), +this.animation.getSingleTileHeight());
        this.clickbox.addPoint(-this.animation.getSingleTileWidth(), +this.animation.getSingleTileHeight());
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
