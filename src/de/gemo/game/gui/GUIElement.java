package de.gemo.game.gui;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3i;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import de.gemo.game.animation.Animation;
import de.gemo.game.animation.SingleTexture;
import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexVector;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.core.FontManager;
import de.gemo.game.entity.Entity2DClickable;
import de.gemo.game.events.mouse.AbstractMouseEvent;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseReleaseEvent;
import de.gemo.game.interfaces.IKeyAdapter;
import de.gemo.game.interfaces.Vector;
import de.gemo.game.interfaces.listener.FocusListener;
import de.gemo.game.interfaces.listener.MouseListener;

public abstract class GUIElement extends Entity2DClickable implements IKeyAdapter {

    private boolean isFocused = false;
    private boolean autoLooseFocus = true;
    private GUIElementStatus status = GUIElementStatus.NONE;

    // LISTENERS
    private MouseListener mouseListener = null;
    private FocusListener focusListener = null;

    public GUIElement(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
    }

    public GUIElement(float x, float y, Animation animation) {
        super(x, y, animation);
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void fireMouseEvent(AbstractMouseEvent event) {
        if (this.mouseListener != null) {
            if (event.isMouseMove()) {
                this.mouseListener.onMove((MouseMoveEvent) event);
                return;
            } else if (event.isMouseClick()) {
                this.mouseListener.onClick((MouseDownEvent) event);
                return;
            } else if (event.isMouseRelease()) {
                this.mouseListener.onRelease((MouseReleaseEvent) event);
                return;
            } else if (event.isMouseDrag()) {
                this.mouseListener.onDrag((MouseDragEvent) event);
                return;
            }
        }
    }

    public void setFocusListener(FocusListener focusListener) {
        this.focusListener = focusListener;
    }

    public void fireFocusGainedEvent() {
        if (this.focusListener != null) {
            this.focusListener.onFocusGained();
        }
    }

    public void fireFocusLostEvent() {
        if (this.focusListener != null) {
            this.focusListener.onFocusLost();
        }
    }

    public void fireHoverBeginEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHoverBegin();
        }
    }

    public void fireHoverEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHover();
        }
    }

    public void fireHoverEndEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHoverEnd();
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

    @Override
    public void debugRender() {
        this.getClickbox().render();
        GL11.glTranslatef(getX(), getY(), getZ());
        GL11.glRotatef(this.getAngle(), 0, 0, 1);

        // render center
        GL11.glDisable(GL11.GL_BLEND);

        glColor3f(1.0f, 0, 0);
        glBegin(GL_LINE_LOOP);
        glVertex3i(-2, -2, 0);
        glVertex3i(2, -2, 0);
        glVertex3i(+2, +2, 0);
        glVertex3i(-2, +2, 0);
        glEnd();
        GL11.glEnable(GL11.GL_BLEND);

        // write entity-id
        if (this.isHovered()) {
            FontManager.getStandardFont().drawString((int) (FontManager.getStandardFont().getWidth("ID: " + this.entityID) / -2f), 0, "ID: " + this.entityID, Color.white);
        }

        // translate back
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glRotatef(-this.getAngle(), 0, 0, 1);
        GL11.glTranslatef(-getX(), -getY(), -getZ());
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
