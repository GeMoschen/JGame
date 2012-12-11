package de.gemo.game.entity;

import org.newdawn.slick.opengl.Texture;

import de.gemo.game.animation.Animation;
import de.gemo.game.interfaces.IRenderable;
import de.gemo.game.interfaces.Vector;

public class Entity2D extends Entity implements IRenderable {

    protected Animation animation = null;
    protected float alpha = 1f;
    protected boolean visible = true;

    public Entity2D(Vector center, Texture texture) {
        super(center);

        // set texture
        this.setTexture(texture);
    }

    public Entity2D(float x, float y, Texture texture) {
        this(new Vector(x, y), texture);
    }

    public final void setTexture(Texture texture) {
        this.setTexture(texture, 1, 1);
    }

    public final void setTexture(Texture texture, int tilesX, int tilesY) {
        this.animation = new Animation(texture, tilesX, tilesY);
    }

    public final void setTexture(Texture texture, int tilesX, int tilesY, int fps) {
        this.animation = new Animation(texture, tilesX, tilesY, fps);
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

    public void scale(float scale) {
        this.animation.scale(scale);
    }

    public void scaleX(float scaleX) {
        this.animation.scaleX(scaleX);
    }

    public void scaleY(float scaleY) {
        this.animation.scaleY(scaleY);
    }

    public void render() {
        this.animation.render(this.alpha);
    }
}
