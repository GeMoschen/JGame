package de.gemo.gameengine.gui;

import java.util.*;

import org.newdawn.slick.opengl.*;

import de.gemo.game.physics.gui.statics.GUIConfig.GUIElementConfig;
import de.gemo.game.physics.gui.statics.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.events.keyboard.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.interfaces.listener.*;
import de.gemo.gameengine.renderer.*;
import de.gemo.gameengine.textures.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;

public class GUIElement implements IRenderable {

    public static Graphic2D createGraphic2DFromXML(GUIElementConfig config, Texture texture) {
        return createGraphic2DFromXML(config, texture, new Vector3f());
    }

    public static Graphic2D createGraphic2DFromXML(GUIElementConfig config, Texture texture, Vector3f position) {
        return createGraphic2DFromXML(config, new SingleTexture(texture, 0, 0, texture.getTextureWidth(), texture.getTextureHeight()), position);
    }

    public static Graphic2D createGraphic2DFromXML(GUIElementConfig config, SingleTexture texture, Vector3f position) {
        Graphic2D graphic = new Graphic2D(position);
        float x = config.getX();
        float y = config.getY();
        float width = config.getWidth();
        float height = config.getHeight();
        if (width > 0 && height > 0 && x > -1 || y > -1 && x <= texture.getWidth() && y <= texture.getHeight()) {
            graphic.setTexture(texture.crop(x, y, width, height));
        }
        return graphic;
    }

    protected float z = 0;
    protected Vector3f position = new Vector3f(), size = new Vector3f();
    protected float angle = 0f;
    protected float alpha = 1f;
    protected boolean visible = true;
    protected TextureRepeatMode repeatMode = TextureRepeatMode.STRETCH;
    protected Map<String, Graphic2D> graphics = null;
    protected ArrayList<Hitbox> hitboxList = new ArrayList<Hitbox>();

    protected MouseListener mouseListener = null;
    protected FocusListener focusListener = null;

    public final void move(Vector3f movement) {
        this.move(movement.getX(), movement.getY(), movement.getZ());
    }

    public final void move(float x, float y) {
        this.move(x, y, 0);
    }

    public final void move(float x, float y, float z) {
        this.setPosition(this.getPosition().getX() + x, this.getPosition().getY() + y, this.getPosition().getZ() + z);
    }

    public final void setPosition(Vector3f position) {
        this.setPosition(position.getX(), position.getY(), position.getZ());
    }

    public final void setPosition(float x, float y) {
        this.setPosition(x, y, this.getPosition().getZ());
    }

    public final void setPosition(float x, float y, float z) {
        if (this.onChangePosition(this.position.getX(), this.position.getY(), this.position.getZ(), x, y, z)) {
            this.position.set(x, y, z);
            for (Hitbox hitbox : this.hitboxList) {
                hitbox.rotateAround(this.getPosition(), -hitbox.getAngle());
                hitbox.setCenter(this.position.getX() + this.getSize().getX() / 2f, this.position.getY() + this.getSize().getY() / 2f);
                hitbox.rotateAround(this.getPosition(), angle);
            }
        }
    }

    public final Vector3f getPosition() {
        return position;
    }

    public final void setSize(Vector2f size) {
        this.setSize(size.getX(), size.getY());
    }

    public final void setSize(float width, float height) {
        if (this.onChangeSize(this.size.getX(), this.size.getY(), width, height)) {
            // calculate relative movement, because of different anchors
            // the Hitbox has the anchors in the _center
            // the GUIElement has the anchor on the upper-left corner
            float difHalfSizeX = (width - this.size.getX()) / 2f;
            float difHalfSizeY = (height - this.size.getY()) / 2f;
            float scaleX = width / this.size.getX();
            float scaleY = height / this.size.getY();

            // rotate hitboxes
            for (Hitbox hitbox : this.hitboxList) {
                // rotate to 0 degrees
                float angle = hitbox.getAngle();
                hitbox.rotateAround(this.getPosition(), -angle);

                // scale hitbox
                hitbox.scale(scaleX, scaleY);

                // move it
                hitbox.move(difHalfSizeX, difHalfSizeY);

                // rotate again
                hitbox.rotateAround(this.getPosition(), angle);
            }
            this.size.set(width, height, 0);
        }
    }

    public final Vector3f getSize() {
        return size;
    }

    public final void setAngle(float angle) {
        this.rotate(angle - this.angle);
    }

    public final void rotate(float angle) {
        this.angle += angle;
        // corrent angles
        while (this.angle >= 360f) {
            this.angle -= 360;
        }
        while (this.angle < 0) {
            this.angle += 360;
        }

        for (Hitbox hitbox : this.hitboxList) {
            hitbox.rotateAround(this.position, angle);
        }
    }

    public final float getAngle() {
        return angle;
    }

    public final void setAlpha(float alpha) {
        if (alpha < 0f) {
            alpha = 0f;
        }
        if (alpha > 1f) {
            alpha = 1f;
        }
        this.alpha = alpha;
        this.visible = (this.alpha > 0);
        if (this.graphics != null) {
            for (Graphic2D element : this.graphics.values()) {
                element.setAlpha(this.alpha);
            }
        }
    }

    public final float getAlpha() {
        return alpha;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
        if (this.graphics != null) {
            for (Graphic2D element : this.graphics.values()) {
                element.setVisible(this.visible);
            }
        }
    }

    public final boolean isVisible() {
        return this.visible;
    }

    public final ArrayList<Hitbox> getHitboxList() {
        return this.hitboxList;
    }

    public final void addHitbox(Hitbox hitbox) {
        hitbox.rotateAround(this.position, this.getAngle());
        this.hitboxList.add(hitbox);
    }

    // ////////////////////////////////////////
    //
    // LISTENER
    //
    // ////////////////////////////////////////

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void setFocusListener(FocusListener focusListener) {
        this.focusListener = focusListener;
    }

    // ////////////////////////////////////////
    //
    // COLLISION
    //
    // ////////////////////////////////////////

    public final boolean isColliding(Hitbox otherHitbox) {
        if (!this.isVisible()) {
            return false;
        }

        for (Hitbox hitbox : this.hitboxList) {
            if (CollisionHelper.isCollidingFast(hitbox, otherHitbox)) {
                return true;
            }
        }
        return false;
    }

    // ////////////////////////////////////////
    //
    // CHILDS
    //
    // ////////////////////////////////////////

    public final Graphic2D getGraphic2D(String name) {
        if (this.graphics == null) {
            return null;
        }
        return this.graphics.get(name);
    }

    public final boolean hasGraphic2D(String name) {
        return this.graphics != null && this.graphics.containsKey(name);
    }

    public final boolean removeGraphic2D(String name) {
        if (this.hasGraphic2D(name)) {
            this.graphics.remove(name);
            if (this.graphics.size() < 1) {
                this.graphics = null;
            }
            return true;
        }
        return false;
    }

    public final boolean removeGraphic2D(Graphic2D element) {
        if (element == null) {
            return true;
        }
        if (this.graphics != null) {
            String name = null;
            for (Map.Entry<String, Graphic2D> entry : this.graphics.entrySet()) {
                if (entry.getValue().equals(element)) {
                    name = entry.getKey();
                    break;
                }
            }
            // remove, if found
            if (name != null) {
                this.graphics.remove(name);
                if (this.graphics.size() < 1) {
                    this.graphics = null;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public final boolean addGraphic2D(String name, Graphic2D element) {
        if (name == null || element == null) {
            return false;
        }

        if (this.graphics == null) {
            this.graphics = new HashMap<String, Graphic2D>();
        }

        if (!this.hasGraphic2D(name)) {
            this.graphics.put(name, element);
            return true;
        }
        return false;
    }

    // ////////////////////////////////////////
    //
    // RENDERING
    //
    // ////////////////////////////////////////

    public final void renderHitbox() {
        // render hitbox
        glPushMatrix();
        {
            for (Hitbox hitbox : this.hitboxList) {
                hitbox.render();
            }
        }
        glPopMatrix();
    }

    @Override
    public void addToRenderPipeline() {
        Renderer.addRenderable(GUITextures.GUI01, this);
    }

    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }

        glPushMatrix();
        {
            // render graphics
            if (this.graphics != null) {
                glTranslatef(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
                glRotatef(this.getAngle(), 0, 0, 1);

                for (Graphic2D element : this.graphics.values()) {
                    element.render();
                }
            }
        }
        glPopMatrix();
    }

    public void debugRender() {
        if (!this.isVisible()) {
            return;
        }

        glPushMatrix();
        {
            // render graphics
            if (this.graphics != null) {
                glTranslatef(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
                glRotatef(this.getAngle(), 0, 0, 1);

                for (Graphic2D element : this.graphics.values()) {
                    element.debugRender();
                }
            }
        }
        glPopMatrix();
    }

    // ////////////////////////////////////////
    //
    // CHANGE EVENTS
    //
    // ////////////////////////////////////////

    protected boolean onChangePosition(float oldX, float oldY, float oldZ, float x, float y, float z) {
        return true;
    }

    protected boolean onChangeSize(float oldWidth, float oldHeight, float width, float height) {
        return true;
    }

    // ////////////////////////////////////////
    //
    // FOCUS EVENTS
    //
    // ////////////////////////////////////////

    public void onFocusGained() {
        if (this.focusListener == null) {
            return;
        }
        this.focusListener.onFocusGained(this);
    }

    public void onFocusLost() {
        if (this.focusListener == null) {
            return;
        }
        this.focusListener.onFocusLost(this);
    }

    public void onHoverBegin() {
        if (this.focusListener == null) {
            return;
        }
        this.focusListener.onHoverBegin(this);
    }

    public void onHover() {
        if (this.focusListener == null) {
            return;
        }
        this.focusListener.onHover(this);
    }

    public void onHoverEnd() {
        if (this.focusListener == null) {
            return;
        }
        this.focusListener.onHoverEnd(this);
    }

    // ////////////////////////////////////////
    //
    // MOUSE EVENTS
    //
    // ////////////////////////////////////////

    public final void onMouseMove(MouseMoveEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseMove(this, event);
    }

    public final void onMouseDown(MouseClickEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseClick(this, event);
    }

    public final void onMouseUp(MouseReleaseEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseRelease(this, event);
    }

    public final void onMouseWheel(MouseWheelEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseWheel(this, event);
    }

    public final void onMouseDrag(MouseDragEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseDrag(this, event);
    }

    public final void onMouseHold(MouseHoldEvent event) {
        if (this.mouseListener == null) {
            return;
        }
        this.mouseListener.onMouseHold(this, event);
    }

    // ////////////////////////////////////////
    //
    // KEYBOARD EVENTS
    //
    // ////////////////////////////////////////

    public void onKeyPressed(KeyEvent event) {
    }

    public void onKeyHold(KeyEvent event) {
    }

    public void onKeyReleased(KeyEvent event) {
    }

    // ////////////////////////////////////////
    //
    // COMMON //
    // ////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof GUIElement) {
            if (obj == this) {
                return true;
            }
        }
        return false;
    }
}
