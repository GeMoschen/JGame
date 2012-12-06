package de.gemo.game.entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.collision.ComplexVector;
import de.gemo.game.collision.Vector;

public class GUIElement extends AbstractEntity2D {

    private ComplexHitbox clickBox;
    private GUIElementStatus status = GUIElementStatus.NONE;

    protected float width = 128, height = 32;
    private ActionListener listener = null;

    public GUIElement(float x, float y, Texture texture) {
        super(x, y, texture);
        // create texture-clickbox
        this.createClickBoxFromTexture();
    }

    public GUIElement(float x, float y, float width, float height, Texture texture) {
        super(x, y, texture);

        this.width = width;
        this.height = height;

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

    @Override
    public void debugRender() {
        // render center
        // super.render();

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        this.clickBox.render();
        this.clickBox.renderCenter();
        GL11.glPopMatrix();
    }

    public ComplexHitbox getClickBox() {
        return clickBox;
    }

    protected void setClickBox(ComplexHitbox clickBox) {
        this.clickBox = clickBox;
        this.clickBox.moveHitbox(this.center);
    }

    public void setActionListener(ActionListener actionListener) {
        this.listener = actionListener;
    }

    public void fireEvent(ActionEvent event) {
        if (this.listener != null) {
            this.listener.actionPerformed(event);
        }
    }

    public GUIElementStatus getStatus() {
        return status;
    }

    public void setStatus(GUIElementStatus status) {
        this.status = status;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void scale(float scale) {
        this.height = height * scale;
        this.width = width * scale;
        this.clickBox.scale(scale);
    }

    public boolean isVectorInHitbox(Vector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.clickBox);
    }

    public boolean isVectorInHitbox(ComplexVector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.clickBox);
    }

    public boolean isColliding(ComplexHitbox otherHitbox) {
        return CollisionHelper.isColliding(this.clickBox, otherHitbox) || CollisionHelper.isVectorInHitbox(this.center, otherHitbox);
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
