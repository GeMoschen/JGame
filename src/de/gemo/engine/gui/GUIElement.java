package de.gemo.engine.gui;

import org.newdawn.slick.Color;

import de.gemo.engine.animation.Animation;
import de.gemo.engine.animation.MultiTexture;
import de.gemo.engine.animation.SingleTexture;
import de.gemo.engine.collision.CollisionHelper;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.entity.Entity2DClickable;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.AbstractMouseEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.interfaces.input.IKeyAdapter;
import de.gemo.engine.interfaces.listener.FocusListener;
import de.gemo.engine.interfaces.listener.MouseListener;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.units.Vector;

import static org.lwjgl.opengl.GL11.*;

public abstract class GUIElement extends Entity2DClickable implements IKeyAdapter {

    private boolean isFocused = false;
    private boolean autoLooseFocus = true;
    private boolean looseFocusOnFocusClick = false;
    private GUIElementStatus status = GUIElementStatus.NONE;

    // LISTENERS
    private MouseListener mouseListener = null;
    private FocusListener focusListener = null;

    public GUIElement(float x, float y, SingleTexture singleTexture) {
        super(x, y, singleTexture);
    }

    public GUIElement(float x, float y, MultiTexture multiTexture) {
        super(x, y, multiTexture);
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
                this.mouseListener.onMouseMove(this, (MouseMoveEvent) event);
                return;
            } else if (event.isMouseClick()) {
                this.mouseListener.onMouseClick(this, (MouseClickEvent) event);
                return;
            } else if (event.isMouseRelease()) {
                this.mouseListener.onMouseRelease(this, (MouseReleaseEvent) event);
                return;
            } else if (event.isMouseDrag()) {
                this.mouseListener.onMouseDrag(this, (MouseDragEvent) event);
                return;
            }
        }
    }

    public void setFocusListener(FocusListener focusListener) {
        this.focusListener = focusListener;
    }

    public void fireFocusGainedEvent() {
        if (this.focusListener != null) {
            this.focusListener.onFocusGained(this);
        }
    }

    public void fireFocusLostEvent() {
        if (this.focusListener != null) {
            this.focusListener.onFocusLost(this);
        }
    }

    public void fireHoverBeginEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHoverBegin(this);
        }
    }

    public void fireHoverEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHover(this);
        }
    }

    public void fireHoverEndEvent() {
        if (this.focusListener != null) {
            this.focusListener.onHoverEnd(this);
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

    public boolean isCollidingWithClickbox(Hitbox otherHitbox) {
        return CollisionHelper.isColliding(this.getClickbox(), otherHitbox);
    }

    public void setLooseFocusOnFocusClick(boolean looseFocusOnFocusClick) {
        this.looseFocusOnFocusClick = looseFocusOnFocusClick;
    }

    public boolean looseFocusOnClick() {
        return looseFocusOnFocusClick;
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

    @Override
    public boolean handleKeyHold(KeyEvent event) {
        return true;
    }

    @Override
    public boolean handleKeyPressed(KeyEvent event) {
        return true;
    }

    @Override
    public boolean handleKeyReleased(KeyEvent event) {
        return true;
    }

    @Override
    public void debugRender() {
        glPushMatrix();
        {
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            glTranslatef(getX(), getY(), getZ());
            glRotatef(this.getAngle(), 0, 0, 1);

            // render center
            Color.yellow.bind();
            glBegin(GL_LINE_LOOP);
            glVertex3i(-1, -1, 0);
            glVertex3i(1, -1, 0);
            glVertex3i(+1, +1, 0);
            glVertex3i(-1, +1, 0);
            glEnd();

            // write entity-id
            if (this.isFocused() || this.isHovered()) {
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_BLEND);
                FontManager.getStandardFont().drawString((int) (FontManager.getStandardFont().getWidth("ID: " + this.entityID) / -2f), 3, "ID: " + this.entityID, Color.white);
            }
            glRotatef(-this.getAngle(), 0, 0, 1);
            glTranslatef(-getX(), -getY(), -getZ());
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            this.getClickbox().render();
        }
        glPopMatrix();
    }

    public void doTick() {
    }
}
