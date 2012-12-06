package de.gemo.game.core;

import java.util.Collection;
import java.util.HashMap;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.ComplexHitbox;
import de.gemo.game.collision.Vector;
import de.gemo.game.entity.AbstractEntity;
import de.gemo.game.entity.GUIElement;
import de.gemo.game.entity.GUIElementStatus;
import de.gemo.game.events.gui.ClickBeginEvent;
import de.gemo.game.events.gui.ClickReleaseEvent;
import de.gemo.game.events.gui.HoverBeginEvent;
import de.gemo.game.events.gui.HoverEndEvent;
import de.gemo.game.events.gui.HoverEvent;
import de.gemo.game.events.keyboard.IKeyHandler;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.IMouseHandler;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseUpEvent;

public abstract class GUIController implements IKeyHandler, IMouseHandler {

    private final int ID;
    private final String name;
    private HashMap<Integer, GUIElement> allElements, visibleElements, invisibleElements;
    private final ComplexHitbox hitbox;
    protected final Vector mouseVector;

    private GUIElement activeElement = null;

    public GUIController(String name, ComplexHitbox hitbox, Vector mouseVector) {
        this.ID = AbstractEntity.getNextFreeID();
        this.name = name;
        this.hitbox = hitbox;
        this.mouseVector = mouseVector;
        this.allElements = new HashMap<Integer, GUIElement>();
        this.visibleElements = new HashMap<Integer, GUIElement>();
        this.invisibleElements = new HashMap<Integer, GUIElement>();
        this.init();
    }

    public final void clear() {
        this.allElements.clear();
        this.visibleElements.clear();
        this.invisibleElements.clear();
    }

    public final void add(GUIElement element) {
        this.allElements.put(element.getEntityID(), element);
        this.addToAll(element);
        this.updateVisibility(element);
    }

    private final void addToAll(GUIElement element) {
        this.allElements.put(element.getEntityID(), element);
    }

    private final void addVisible(GUIElement element) {
        this.visibleElements.put(element.getEntityID(), element);
    }

    private final void addInvisible(GUIElement element) {
        this.invisibleElements.put(element.getEntityID(), element);
    }

    public final GUIElement getVisible(int entityID) {
        return this.visibleElements.get(entityID);
    }

    public final GUIElement getVisible(GUIElement element) {
        return this.getVisible(element.getEntityID());
    }

    public final GUIElement getInvisible(int entityID) {
        return this.invisibleElements.get(entityID);
    }

    public final GUIElement getInvisible(GUIElement element) {
        return this.getInvisible(element.getEntityID());
    }

    public final boolean has(int entityID) {
        return this.allElements.containsKey(entityID);
    }

    public final boolean has(GUIElement element) {
        return this.has(element.getEntityID());
    }

    public final boolean hasVisible(int entityID) {
        return this.visibleElements.containsKey(entityID);
    }

    public final boolean hasVisible(GUIElement element) {
        return this.hasVisible(element.getEntityID());
    }

    public final boolean hasInvisible(int entityID) {
        return this.invisibleElements.containsKey(entityID);
    }

    public final boolean hasInvisible(GUIElement element) {
        return this.hasInvisible(element.getEntityID());
    }

    private final void partialRemove(GUIElement element) {
        this.visibleElements.remove(element.getEntityID());
        this.invisibleElements.remove(element.getEntityID());
    }

    public final void remove(GUIElement element) {
        this.partialRemove(element);
        this.allElements.remove(element.getEntityID());
    }

    public void render() {
        Renderer.render(this.getName(), this.hitbox);
        for (GUIElement element : this.visibleElements.values()) {
            Renderer.render(element);
        }
    }

    public final int getID() {
        return ID;
    }

    public final String getName() {
        return name;
    }

    public final GUIElement getActiveElement() {
        return activeElement;
    }

    public final boolean isColliding() {
        return CollisionHelper.isVectorInHitbox(this.mouseVector, this.hitbox);
    }

    public final ComplexHitbox getHitbox() {
        return hitbox;
    }

    public Collection<GUIElement> getVisibleElements() {
        return this.visibleElements.values();
    }

    public Collection<GUIElement> getAllElements() {
        return this.allElements.values();
    }

    // //////////////////////////////////////////
    //
    // OPTIMIZATION-METHODS
    //
    // //////////////////////////////////////////

    protected abstract void init();

    public void updateCollisions() {
        boolean setToNull = true;
        boolean isColliding = false;
        for (GUIElement element : this.getVisibleElements()) {
            isColliding = element.isVectorInHitbox(this.mouseVector);
            if (isColliding && !element.getStatus().equals(GUIElementStatus.ACTIVE)) {
                if (element.getStatus().equals(GUIElementStatus.HOVERING)) {
                    this.activeElement = element;
                    element.fireEvent(new HoverEvent(element));
                    setToNull = false;
                } else {
                    this.activeElement = element;
                    element.fireEvent(new HoverBeginEvent(element));
                    element.setStatus(GUIElementStatus.HOVERING);
                    setToNull = false;
                }
            }
            if (!isColliding && !element.getStatus().equals(GUIElementStatus.NONE)) {
                element.fireEvent(new HoverEndEvent(element));
                element.setStatus(GUIElementStatus.NONE);
            }
        }
        if (setToNull) {
            this.activeElement = null;
        }
    }

    public final void updateVisibility() {
        for (GUIElement element : this.allElements.values()) {
            this.updateVisibility(element);
        }
        this.updateCollisions();
    }

    private final void updateVisibility(GUIElement element) {
        if (element.isVisible()) {
            if (!element.isColliding(this.hitbox)) {
                this.partialRemove(element);
                this.addInvisible(element);
                return;
            } else {
                if (!this.hasVisible(element)) {
                    this.partialRemove(element);
                    this.addVisible(element);
                }
            }
        } else {
            if (!this.hasInvisible(element)) {
                this.partialRemove(element);
                this.addInvisible(element);
            }
        }
    }

    // //////////////////////////////////////////
    //
    // MOUSE-EVENTS
    //
    // //////////////////////////////////////////

    public void onMouseIn() {
    }

    public void onMouseOut() {
        for (GUIElement element : this.getVisibleElements()) {
            if (!element.getStatus().equals(GUIElementStatus.NONE)) {
                element.fireEvent(new HoverEndEvent(element));
                element.setStatus(GUIElementStatus.NONE);
            }
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (this.getActiveElement() != null) {
            this.getActiveElement().fireEvent(new ClickBeginEvent(this.getActiveElement()));
        }
    }

    @Override
    public abstract void onMouseMove(MouseMoveEvent event);

    @Override
    public abstract void onMouseDrag(MouseDragEvent event);

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (this.getActiveElement() != null) {
            this.getActiveElement().fireEvent(new ClickReleaseEvent(this.getActiveElement()));
        }
    }

    // //////////////////////////////////////////
    //
    // KEY-EVENTS
    //
    // //////////////////////////////////////////

    @Override
    public abstract void onKeyHold(KeyEvent event);

    @Override
    public abstract void onKeyPressed(KeyEvent event);

    @Override
    public abstract void onKeyReleased(KeyEvent event);
}
