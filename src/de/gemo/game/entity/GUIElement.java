package de.gemo.game.entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.newdawn.slick.opengl.Texture;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexVector;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.interfaces.Vector;

public abstract class GUIElement extends Entity2DClickable {

    private GUIElementStatus status = GUIElementStatus.NONE;
    private ActionListener listener = null;

    public GUIElement(float x, float y, Texture texture) {
        super(x, y, texture);
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

    public boolean isVectorInClickbox(Vector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.getClickbox());
    }

    public boolean isVectorInClickbox(ComplexVector vector) {
        return CollisionHelper.isVectorInHitbox(vector, this.getClickbox());
    }

    public boolean isCollidingWithClickbox(Hitbox otherHitbox) {
        return CollisionHelper.isColliding(this.getClickbox(), otherHitbox);
    }
}
