package de.gemo.game.entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.gemo.game.animation.Animation;
import de.gemo.game.animation.SingleTexture;
import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexVector;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.interfaces.IKeyAdapter;
import de.gemo.game.interfaces.Vector;

public abstract class GUIElement extends Entity2DClickable implements IKeyAdapter {

    private boolean isFocused = false;
    private boolean autoLooseFocus = true;
    private GUIElementStatus status = GUIElementStatus.NONE;
    private ActionListener listener = null;

    public GUIElement(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
    }

    public GUIElement(float x, float y, Animation animation) {
        super(x, y, animation);
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

    public boolean isHovered() {
        return this.status.equals(GUIElementStatus.HOVERING);
    }

    public boolean isActive() {
        return this.status.equals(GUIElementStatus.ACTIVE);
    }

    public boolean isNormal() {
        return this.status.equals(GUIElementStatus.NONE);
    }

    public void setStatus(GUIElementStatus status) {
        this.status = status;
    }

    public boolean isVectorInClickbox(Vector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.getClickbox());
    }

    public boolean isVectorInClickbox(ComplexVector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.getClickbox());
    }

    public boolean isCollidingWithClickbox(Hitbox otherHitbox) {
        return CollisionHelper.isColliding(this.getClickbox(), otherHitbox);
    }

    public void setAutoLooseFocus(boolean autoLooseFocus) {
        this.autoLooseFocus = autoLooseFocus;
    }

    public boolean isAutoLooseFocus() {
        return autoLooseFocus;
    }

    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void doTick() {
    }
}
