package de.gemo.game.entity;

import org.newdawn.slick.opengl.Texture;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.collision.ComplexVector;
import de.gemo.game.collision.Vector;

public class GUIElement extends AbstractEntity2D {

    private ComplexHitbox clickBox;

    public GUIElement(float x, float y, Texture texture) {
        super(x, y, texture);

        // create texture-clickbox
        this.createClickBoxFromTexture();
    }

    public GUIElement(float x, float y, float width, float height, Texture texture) {
        super(x, y, texture);

        this.halfWidth = width / 2;
        this.halfHeight = height / 2;

        // create texture-clickbox
        this.clickBox = new ComplexHitbox(this.center);
        this.clickBox.addPoint(-this.halfWidth, -this.halfHeight);
        this.clickBox.addPoint(+this.halfWidth, -this.halfHeight);
        this.clickBox.addPoint(+this.halfWidth, +this.halfHeight);
        this.clickBox.addPoint(-this.halfWidth, +this.halfHeight);
    }

    protected void createClickBoxFromTexture() {
        this.clickBox = new ComplexHitbox(this.center);
        this.clickBox.addPoint(-this.halfWidth, -this.halfHeight);
        this.clickBox.addPoint(+this.halfWidth, -this.halfHeight);
        this.clickBox.addPoint(+this.halfWidth, +this.halfHeight);
        this.clickBox.addPoint(-this.halfWidth, +this.halfHeight);
    }

    public ComplexHitbox getClickBox() {
        return clickBox;
    }

    protected void setClickBox(ComplexHitbox clickBox) {
        this.clickBox = clickBox;
        this.clickBox.moveHitbox(this.center);
    }

    public boolean isVectorInHitbox(Vector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.clickBox);
    }

    public boolean isVectorInHitbox(ComplexVector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.clickBox);
    }

    public boolean isColliding(ComplexHitbox otherHitbox) {
        return CollisionHelper.isColliding(this.clickBox, otherHitbox);
    }

    @Override
    public void move(float x, float y) {
        super.move(x, y);
        this.clickBox.move(x, y);
    }

    public void rotate(float angle) {
        super.rotate(angle);
        this.clickBox.rotate(angle);
    }
}
