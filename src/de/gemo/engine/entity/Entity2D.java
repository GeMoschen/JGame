package de.gemo.engine.entity;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.interfaces.rendering.IRenderable;
import de.gemo.engine.units.Vector;

public class Entity2D extends Entity implements IRenderable, Comparable<Entity2D> {

    protected Animation animation = null;
    protected float alpha = 1f;
    protected boolean visible = true;
    protected boolean valid = false;
    protected boolean dead = false;

    public Entity2D(Vector center, SingleTexture singleTexture) {
        super(center);

        // set texture
        this.setTexture(singleTexture);
    }

    public Entity2D(float x, float y, SingleTexture singleTexture) {
        this(new Vector(x, y), singleTexture);
    }

    public float getXOnScreen() {
        return this.getX() - this.getWidth() / 2f;
    }

    public float getYOnScreen() {
        return this.getY() - this.getHeight() / 2f;
    }

    public float getWidth() {
        return this.animation.getWidth();
    }

    public float getHeight() {
        return this.animation.getHeight();
    }

    public Entity2D(Vector center, Animation animation) {
        super(center);

        // set animation
        this.setAnimation(animation);
    }

    public Entity2D(float x, float y, Animation animation) {
        this(new Vector((int) (x + animation.getWidth() / 2), (int) (y + animation.getHeight() / 2)), animation.clone());
    }

    public final void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public final void setTexture(SingleTexture singleTexture) {
        MultiTexture multiTexture = new MultiTexture(singleTexture.getWidth(), singleTexture.getHeight(), singleTexture);
        this.animation = new Animation(multiTexture);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        if (alpha <= 0) {
            alpha = 0f;
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
        if (alpha > 1) {
            alpha = 1f;
        }
        this.alpha = alpha;
    }

    public void scale(float scaleX, float scaleY) {
        this.animation.scale(scaleX, scaleY);
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isValid() {
        return valid;
    }

    public void render() {
        this.render(1, 1, 1);
    }

    public void render(float r, float g, float b) {
        float x = -this.animation.getWidth() / 2;
        float y = -this.animation.getHeight() / 2;
        this.animation.render(x, y, this.getZ(), r, g, b, this.alpha);
    }

    @Override
    public int compareTo(Entity2D o) {
        return (int) (o.getZ() - this.getZ());
    }
}
