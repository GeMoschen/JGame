package de.gemo.engine.entity;

import de.gemo.engine.interfaces.rendering.IRenderable;
import de.gemo.engine.textures.Animation;
import de.gemo.engine.textures.MultiTexture;
import de.gemo.engine.textures.SingleTexture;
import de.gemo.engine.units.Vector;

public class Entity2D extends Entity implements IRenderable, Comparable<Entity2D> {

    protected Animation animation = null;
    protected float alpha = 1f;
    protected boolean visible = true;
    protected boolean valid = false;
    protected boolean dead = false;
    protected float scaleX = 1.0f;
    protected float scaleY = 1.0f;

    public Entity2D(Vector center, SingleTexture singleTexture) {
        super(center);
        this.setTexture(singleTexture);
    }

    public Entity2D(Vector center, MultiTexture multiTexture) {
        super(center);

        // set texture
        this.setTexture(multiTexture);
    }

    public Entity2D(float x, float y, SingleTexture singleTexture) {
        this(new Vector(x, y), singleTexture);
    }

    public Entity2D(float x, float y, MultiTexture multiTexture) {
        this(new Vector(x, y), multiTexture);
    }

    public int getXOnScreen() {
        return (int) (this.getX() - this.getWidth() / 2f);
    }

    public int getYOnScreen() {
        return (int) (this.getY() - this.getHeight() / 2f);
    }

    public void setXOnScreen(int xOnScreen) {
        this.setPositionOnScreen(xOnScreen, (int) this.getY());
    }

    public void setYOnScreen(int yOnScreen) {
        this.setPositionOnScreen((int) this.getXOnScreen(), yOnScreen);
    }

    public void setPositionOnScreen(float xOnScreen, float yOnScreen) {
        float halfWidth = (this.getWidth() / 2f);
        float halfHeight = (this.getHeight() / 2f);
        this.setCenter(xOnScreen + halfWidth, yOnScreen + halfHeight);
    }

    public float getWidth() {
        return this.animation.getWidth() * this.scaleX;
    }

    public float getHeight() {
        return this.animation.getHeight() * this.scaleY;
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

    public final void setTexture(MultiTexture multiTexture) {
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

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void scale(float scaleX, float scaleY) {
        this.scaleX *= scaleX;
        this.scaleY *= scaleY;
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
        this.animation.render(r, g, b, getAlpha());
    }

    @Override
    public int compareTo(Entity2D o) {
        return (int) (o.getZ() - this.getZ());
    }

    public Animation getAnimation() {
        return animation;
    }
}
